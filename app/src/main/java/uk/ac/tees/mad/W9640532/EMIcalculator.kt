package uk.ac.tees.mad.W9640532



import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class EMIcalculator : AppCompatActivity() {

    private lateinit var loanAmountEditText: EditText
    private lateinit var interestRateEditText: EditText
    private lateinit var loanTermEditText: EditText
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emicalculator)

        loanAmountEditText = findViewById(R.id.loanAmountEditText)
        interestRateEditText = findViewById(R.id.interestRateEditText)
        loanTermEditText = findViewById(R.id.loanTermEditText)
        resultTextView = findViewById(R.id.resultTextView)
    }

    fun calculateEMI(view: android.view.View) {
        val loanAmount = loanAmountEditText.text.toString()
        val interestRate = interestRateEditText.text.toString()
        val loanTerm = loanTermEditText.text.toString()

        val requestBody = FormBody.Builder()
            .add("loan_amount", loanAmount)
            .add("interest_rate", interestRate)
            .add("loan_term", loanTerm)
            .add("start_date", "2023-01-01")
            .build()

        val request = Request.Builder()
            .url("https://smart-emi-calculator.p.rapidapi.com/")
            .post(requestBody)
            .addHeader("content-type", "application/x-www-form-urlencoded")
            .addHeader("X-RapidAPI-Key", "f6d96b246cmsh9f60d5199ed066dp178283jsn2441bcb10c02")
            .addHeader("X-RapidAPI-Host", "smart-emi-calculator.p.rapidapi.com")
            .build()

        val client = OkHttpClient()

        GlobalScope.launch(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            val responseData = response.body()?.string()

            launch(Dispatchers.Main) {
                // Parse the JSON using Gson for pretty printing
                val gson: Gson = GsonBuilder().setPrettyPrinting().create()
                val jsonResponse = gson.fromJson(responseData, EMIResponse::class.java)


// Extract relevant information for display
                val emi = jsonResponse.emi
                val totalInterest = jsonResponse.totalInterest
                val totalPayment = jsonResponse.totalPayment

// Format information into headings and subheadings
                val yearlyBreakdownFormatted = buildString {
                    appendLine("Yearly Breakdown:")
                    appendLine("----------------")

                    jsonResponse.emiBreakdown.forEach { yearlyEMI ->
                        appendLine("${yearlyEMI.year}:")
                        appendLine("  Yearly EMI: ${yearlyEMI.yearlyEmi}")
                        appendLine("  Yearly Principal: ${yearlyEMI.yearlyPrincipal}")
                        appendLine("  Yearly Interest: ${yearlyEMI.yearlyInterest}")
                        appendLine("  Yearly Remaining Loan Amount: ${yearlyEMI.yearlyRemainingLoanAmount}")

                        appendLine("  Monthly Breakdown:")
                        appendLine("  ------------------")

                        yearlyEMI.monthlyBreakdown.forEach { monthlyEMI ->
                            appendLine("    ${monthlyEMI.month} ${monthlyEMI.date}:")
                            appendLine("      Monthly EMI: ${monthlyEMI.monthlyEmi}")
                            appendLine("      Monthly Principal: ${monthlyEMI.monthlyPrincipal}")
                            appendLine("      Monthly Interest: ${monthlyEMI.monthlyInterest}")
                            appendLine("      Remaining Loan Amount: ${monthlyEMI.remainingLoanAmount}")
                        }
                    }
                }

// Format the entire result
                val formattedResult = """
    EMI Details:
    ----------------
    EMI: $emi
    Total Interest: $totalInterest
    Total Payment: $totalPayment
    
    $yearlyBreakdownFormatted
""".trimIndent()

// Update the TextView with the formatted result
                resultTextView.text = formattedResult
            }
        }
    }
}

data class EMIResponse(
    val emi: Int,
    @SerializedName("total_interest") val totalInterest: Int,
    @SerializedName("total_payment") val totalPayment: Int,
    @SerializedName("emi_breakdown") val emiBreakdown: List<YearlyEMIBreakdown>
)

data class YearlyEMIBreakdown(
    val year: String,
    @SerializedName("yearly_emi") val yearlyEmi: Int,
    @SerializedName("yearly_principal") val yearlyPrincipal: Int,
    @SerializedName("yearly_interest") val yearlyInterest: Int,
    @SerializedName("yearly_remaining_loan_amount") val yearlyRemainingLoanAmount: Int,
    @SerializedName("monthly_breakdown") val monthlyBreakdown: List<MonthlyEMIBreakdown>
)

data class MonthlyEMIBreakdown(
    val date: String,
    val month: String,
    @SerializedName("monthly_emi") val monthlyEmi: Int,
    @SerializedName("monthly_principal") val monthlyPrincipal: Int,
    @SerializedName("monthly_interest") val monthlyInterest: Int,
    @SerializedName("remaining_loan_amount") val remainingLoanAmount: Int
)

