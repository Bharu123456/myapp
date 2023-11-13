package uk.ac.tees.mad.W9640532

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//
//        val buttom = findViewById<Button>(R.id.signout)
//
//        buttom.setOnClickListener{
//            auth.signOut();
//            startActivity(Intent(this,Login::class.java))
//        }


        findViewById<Button>(R.id.buttonSettings).setOnClickListener{
            divertToCalculator()
        }

        listView = findViewById(R.id.listViewRecentTransactions)

        // Initialize the adapter with an empty list
        adapter = CustomAdapter(this, R.layout.list_item_one, mutableListOf())
        listView.adapter = adapter

        // Example of adding values dynamically
        val valuesToAdd = listOf("$200", "$12.4", "$45")
        addValuesToAdapter(valuesToAdd)



    }
    fun divertToCalculator(){

        startActivity(Intent(this,EMIcalculator::class.java))

    }

    private fun addValuesToAdapter(values: List<String>) {
        // Add the values to the adapter
        adapter.addAll(values)

        // Notify the adapter that the data set has changed
        adapter.notifyDataSetChanged()
    }
}




class CustomAdapter(context: Context, resource: Int, objects: List<String>) :
    ArrayAdapter<String>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.list_item_one, parent, false)

        val headingTextView = rowView.findViewById<TextView>(R.id.heading)
        val subheadingTextView = rowView.findViewById<TextView>(R.id.subheading)

        // Assuming your data is a list of strings for simplicity
        val currentItem = getItem(position)
        headingTextView.text = currentItem
        subheadingTextView.text = "Grosery"

        return rowView
    }
}
