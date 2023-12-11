package uk.ac.tees.mad.W9640532

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import uk.ac.tees.mad.W9640532.data.Expense
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var listView: ListView
    private lateinit var expenses: MutableList<Expense>
    private lateinit var adapter: CustomAdapter
    private var currentbal = 0

    private lateinit var executor: Executor
    private lateinit var bioPrompt: BiometricPrompt
    private lateinit var pinfo: BiometricPrompt.PromptInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.EMICalculator).setOnClickListener{
            divertToCalculator()
        }

        findViewById<Button>(R.id.buttonAddExpense).setOnClickListener{
            startActivity(Intent(this,AddExpence::class.java))
        }
        findViewById<Button>(R.id.addIncome).setOnClickListener{
            startActivity(Intent(this,AddIncome::class.java))
        }

        findViewById<Button>(R.id.reportsbtn).setOnClickListener{
            startActivity(Intent(this,Report::class.java))
        }
        findViewById<Button>(R.id.signoutbtn).setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this,Login::class.java))
        }

        findViewById<Button>(R.id.viewBills).setOnClickListener{
            startActivity(Intent(this,ViewBills::class.java))
        }
        getAll()

        executor = ContextCompat.getMainExecutor(this)
        bioPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Handle error
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
//                    getAll()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                   FirebaseAuth.getInstance().signOut()
                    finish()
                }
            })

        pinfo = BiometricPrompt.PromptInfo.Builder().setTitle("Login to View")
            .setSubtitle("FingerPrint")
            .setNegativeButtonText("No Password").build()


        bioPrompt.authenticate(pinfo)

        listView = findViewById(R.id.listViewRecentTransactions)
        expenses = mutableListOf()

        adapter = CustomAdapter(this, R.layout.list_item_one, expenses)
        listView.adapter = adapter

    }
    fun divertToCalculator(){

        startActivity(Intent(this,EMIcalculator::class.java))

    }
    fun getAll(){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance().reference.child("users").child(userId!!).child("expenses")


        database.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                expenses.clear()
                dataSnapshot.children.forEach {
                    val expense = it.child("expense").getValue(Expense::class.java)
                    expense?.let { exp ->
                        expenses.add(exp)

                        if(!exp.type.equals("One-Time"))
                        {
                            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            val startDate = LocalDate.parse(exp.startMonth, formatter)
                            val endDate = LocalDate.parse(exp.endMonth, formatter)
                            val today = LocalDate.now()
                            val isTodayInRange = today.isAfter(startDate) && today.isBefore(endDate) || today.isEqual(startDate) || today.isEqual(endDate)
                            if(isTodayInRange) {
                                if(exp.amount!=null && exp.role.equals("income")) {
                                    currentbal += Integer.parseInt(exp.amount)
                                }else{
                                    currentbal -= Integer.parseInt(exp.amount)
                                }
                            }


                        }else{
                            if(exp.amount!=null && exp.role.equals("income")) {
                                currentbal += Integer.parseInt(exp.amount)
                            }else{
                                currentbal -= Integer.parseInt(exp.amount)
                            }
                        }



                    }
                }
                adapter.notifyDataSetChanged()
                findViewById<TextView>(R.id.textViewBalance).setText("£"+currentbal)
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

}




class CustomAdapter(context: Context, resource: Int, objects: List<Expense>) :
    ArrayAdapter<Expense>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.list_item_one, parent, false)

        val headingTextView = rowView.findViewById<TextView>(R.id.heading)
        val subheadingTextView = rowView.findViewById<TextView>(R.id.subheading)

        val currentItem = getItem(position)
        currentItem?.let {
            if(it.role.equals("expense")){
                headingTextView.setTextColor(Color.RED)
                subheadingTextView.setTextColor(Color.RED)
                headingTextView.text = "- "+it.amount
                subheadingTextView.text = it.category

            }else{
                headingTextView.setTextColor(Color.GREEN)
                subheadingTextView.setTextColor(Color.GREEN)
                headingTextView.text = "+ "+it.amount
                subheadingTextView.text = it.category
            }


        }

        return rowView
    }
}

