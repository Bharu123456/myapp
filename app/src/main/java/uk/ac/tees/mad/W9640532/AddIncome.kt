package uk.ac.tees.mad.W9640532

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import uk.ac.tees.mad.W9640532.data.Expense
import java.io.ByteArrayOutputStream
import java.util.Calendar

class AddIncome : AppCompatActivity() {

    private lateinit var expenseTypeGroup: RadioGroup
    private lateinit var amountInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var startMonthInput: EditText
    private lateinit var endMonthInput: EditText
    private lateinit var addExpenseButton: Button
    private lateinit var uploadPhotoButton: Button
    private var photoBase64: String = ""
    private val REQUEST_IMAGE_CAPTURE = 42
    private lateinit var oneTimeExpense : RadioButton
    private lateinit var monthlyExpense : RadioButton
    private var currentFocusedEditText: EditText? = null
    private val REQUEST_CAMERA_PERMISSION = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income)

        expenseTypeGroup = findViewById<RadioGroup>(R.id.expenseTypeGroup)
        oneTimeExpense = findViewById<RadioButton>(R.id.oneTimeExpense)
        monthlyExpense = findViewById<RadioButton>(R.id.monthlyExpense)
        amountInput = findViewById<EditText>(R.id.amountInput)
        startMonthInput = findViewById<EditText>(R.id.startMonthInput)
        endMonthInput = findViewById<EditText>(R.id.endMonthInput)
        addExpenseButton = findViewById<Button>(R.id.addExpenseButton)
        uploadPhotoButton = findViewById<Button>(R.id.uploadPhotoButton)

        expenseTypeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.oneTimeExpense -> {
                    amountInput.visibility = View.VISIBLE
                    startMonthInput.visibility = View.GONE
                    endMonthInput.visibility = View.GONE
                }
                R.id.monthlyExpense -> {
                    amountInput.visibility = View.GONE
                    startMonthInput.visibility = View.VISIBLE
                    endMonthInput.visibility = View.VISIBLE
                }
            }
        }

        setupListeners()
        setupDatePickers()

    }

    private fun setupListeners() {
        expenseTypeGroup.setOnCheckedChangeListener { _, checkedId ->
            handleExpenseTypeChange(checkedId)
        }

        addExpenseButton.setOnClickListener {
            handleAddExpense()
        }

        uploadPhotoButton.setOnClickListener {
            handlePhoto()
        }
    }

    private fun handleExpenseTypeChange(checkedId: Int) {
        when (checkedId) {
            R.id.oneTimeExpense -> {

                startMonthInput.visibility = View.GONE
                endMonthInput.visibility = View.GONE
            }
            R.id.monthlyExpense -> {

                startMonthInput.visibility = View.VISIBLE
                endMonthInput.visibility = View.VISIBLE
            }
        }
    }

    private fun handleAddExpense() {
        if (expenseTypeGroup.checkedRadioButtonId == R.id.oneTimeExpense) {
            val amount = amountInput.text.toString()

            saveIncome("One-Time", amount,"none", "", "")
        } else {
            val startMonth = startMonthInput.text.toString()
            val endMonth = endMonthInput.text.toString()
            val amount = amountInput.text.toString()
            saveIncome("Monthly", amount, "none", startMonth, endMonth)
        }
        Toast.makeText(this, "Income Added", Toast.LENGTH_SHORT).show()
    }



    private fun saveIncome(type: String, amount: String, category: String, startMonth: String, endMonth: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance().reference

        if (userId != null) {
            val expense = Expense(type, amount, "none", startMonth, endMonth, photoBase64,"income")
            val expenseRef = database.child("users").child(userId).child("expenses").push()
            expenseRef.child("expense").setValue(expense)
                .addOnSuccessListener {
                    Toast.makeText(this, "Income saved successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save income", Toast.LENGTH_SHORT).show()
                    finish()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handlePhoto() {
        requestCameraPermission()
    }

    private fun formatDateString(day: Int, month: Int, year: Int): String {
        return String.format("%02d/%02d/%d", day, month + 1, year)
    }

    private fun setupDatePickers() {
        startMonthInput.setOnClickListener {
            showDatePicker { year, month, dayOfMonth ->
                val selectedDate = formatDateString(dayOfMonth, month, year)

                startMonthInput.setText(selectedDate)
            }
        }

        endMonthInput.setOnClickListener {
            showDatePicker { year, month, dayOfMonth ->
                val selectedDate = formatDateString(dayOfMonth, month, year)

                endMonthInput.setText(selectedDate)
            }
        }
    }

    private fun showDatePicker(onDateSet: (year: Int, month: Int, dayOfMonth: Int) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth -> onDateSet(year, month, dayOfMonth) },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }



    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    dispatchTakePictureIntent()
                } else {
                    // Permission denied, you can show a message to the user
                    Toast.makeText(this, "Camera permission is required to use camera", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
                // Handle other permission requests
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            var imageView = findViewById<ImageView>(R.id.imageView2)
            imageView.setImageBitmap(imageBitmap) // Display the image
            photoBase64 = convertBitmapToBase64(imageBitmap) // Convert to Base64
        }
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION)
        } else {
            dispatchTakePictureIntent()
        }
    }





}