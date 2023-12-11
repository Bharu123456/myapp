package uk.ac.tees.mad.W9640532

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import uk.ac.tees.mad.W9640532.data.Expense
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberImagePainter
import com.google.firebase.database.*
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap


class ViewBills : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val imageBase64Strings = remember { mutableStateListOf<String>() }
            LaunchedEffect(Unit) {
                fetchImageUrls { strings -> imageBase64Strings.addAll(strings) }
            }
            ImagesList(imageBase64Strings)
        }
    }

    @Composable
    fun ImagesList(imagesBase64Strings: List<String>) {
        LazyColumn {
            items(imagesBase64Strings) { base64String ->
                val imageBitmap = base64StringToImageBitmap(base64String)
                imageBitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }

    private fun fetchImageUrls(callback: (List<String>) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance().reference.child("users").child(userId!!).child("expenses")
        var strings : ArrayList<String> = ArrayList()

        database.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                dataSnapshot.children.forEach {
                    val expense = it.child("expense").getValue(Expense::class.java)
                    expense?.let { exp ->
                        if(exp.photoBase64 !=null && exp.photoBase64 !=""){
                            strings.add(exp.photoBase64)
                        }

                    }
                }
                callback(strings)
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    fun base64StringToImageBitmap(base64String: String): ImageBitmap? {
        return try {
            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            bitmap.asImageBitmap()
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}