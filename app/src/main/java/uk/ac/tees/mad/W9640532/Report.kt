package uk.ac.tees.mad.W9640532

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import uk.ac.tees.mad.W9640532.data.Expense
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates

class Report : AppCompatActivity() {
    private lateinit var pieChart: PieChart
    var entries = mutableListOf<PieEntry>()
    var dataSet = PieDataSet(entries, "Expense Report")
    var exp_value : Int = 0
    var inc_value :Int =0
    var percentage :Int = 0
    lateinit var progressBar: ProgressBar
    var sc by Delegates.notNull<Double>()

    private lateinit var listView: ListView
    private lateinit var expenses: MutableList<Expense>
    private lateinit var adapter: CustomAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        pieChart = findViewById(R.id.pieChart)
        progressBar = findViewById(R.id.progressBar2)
        val myInt = 0
        val myDouble = myInt.toDouble() // myDouble will be 0.0

        listView = findViewById(R.id.listViewRecentTransactions)
        expenses = mutableListOf()
        adapter = CustomAdapter(this, R.layout.list_item_one, expenses)
        listView.adapter = adapter

        sc = myDouble
        getdata()
        fetchfromDb()
    }

    
    private fun getdata()
    {

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance().reference.child("users").child(userId!!).child("expenses")


        database.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                dataSnapshot.children.forEach {
                    val expense = it.child("expense").getValue(Expense::class.java)
                    expense?.let { exp ->


                        if(!exp.type.equals("One-Time"))
                        {
                            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            val startDate = LocalDate.parse(exp.startMonth, formatter)
                            val endDate = LocalDate.parse(exp.endMonth, formatter)
                            val today = LocalDate.now()
                            val isTodayInRange = today.isAfter(startDate) && today.isBefore(endDate) || today.isEqual(startDate) || today.isEqual(endDate)
                            if(isTodayInRange) {
                                if(exp.role.equals("income"))
                                {
                                    inc_value += Integer.parseInt(exp.amount)
                                }else{
                                    exp_value += Integer.parseInt(exp.amount)
                                }

                            }
                        }else{
                            if(exp.role.equals("income"))
                            {
                                inc_value += Integer.parseInt(exp.amount)
                            }else{
                                exp_value += Integer.parseInt(exp.amount)
                            }
                        }

                    }
                }
                entries.add(PieEntry(exp_value.toFloat() , "Expense"))
                entries.add(PieEntry(inc_value.toFloat() , "Income"))
                dataSet = PieDataSet(entries,"")
                var arr = ArrayList<Int>()
                arr.add(ColorTemplate.rgb("#FF0000"))
                arr.add(ColorTemplate.rgb("#2ecc71"))
                dataSet.colors = arr;
                pieChart.data = PieData(dataSet)
                pieChart.invalidate()
                sc =  calculateFinancialScore(inc_value,exp_value)
                progressBar.progress = sc.toInt()
                findViewById<TextView>(R.id.textViewScore).text = "60-40 Financial Score: "+String.format("%.2f", sc)

            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
        
    }

    fun calculateFinancialScore(income: Int, expenditure: Int): Double {
        if (income <= 0) {
            return 0.0 // Return 0 if income is zero or negative
        }

        val maxExpenditure = income * 0.6 // 60% of income
        if (expenditure <= maxExpenditure) {
            return 100.0 // Full score if expenditure is within 60% of income
        }

        if (expenditure >= income) {
            return 0.0 // No score if expenditure is equal to or greater than income
        }

        // Calculate score for expenditure between 60% of income and 100% of income
        return (1 - ((expenditure - maxExpenditure) / (income - maxExpenditure))) * 100
    }


    fun fetchfromDb(){
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
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

}
