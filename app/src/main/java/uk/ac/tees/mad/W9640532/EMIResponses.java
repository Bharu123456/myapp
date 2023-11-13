package uk.ac.tees.mad.W9640532;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class EMIResponses {

    private int emi;
    private int total_interest;
    private int total_payment;
    private JsonArray emi_breakdown;

    public EMIResponses(JsonObject jsonObject) {
        this.emi = jsonObject.get("emi").getAsInt();
        this.total_interest = jsonObject.get("total_interest").getAsInt();
        this.total_payment = jsonObject.get("total_payment").getAsInt();
        this.emi_breakdown = jsonObject.getAsJsonArray("emi_breakdown");
    }

    public int getEmi() {
        return emi;
    }

    public int getTotalInterest() {
        return total_interest;
    }

    public int getTotalPayment() {
        return total_payment;
    }

    public JsonArray getemi_breakdown() {
        return emi_breakdown;
    }

    public static class Yearlyemi_breakdown {
        private String year;
        private int yearlyEmi;
        private int yearlyPrincipal;
        private int yearlyInterest;
        private int yearlyRemainingLoanAmount;
        private JsonArray monthlyBreakdown;

        public Yearlyemi_breakdown(JsonObject jsonObject) {
            this.year = jsonObject.get("year").getAsString();
            this.yearlyEmi = jsonObject.get("yearly_emi").getAsInt();
            this.yearlyPrincipal = jsonObject.get("yearly_principal").getAsInt();
            this.yearlyInterest = jsonObject.get("yearly_interest").getAsInt();
            this.yearlyRemainingLoanAmount = jsonObject.get("yearly_remaining_loan_amount").getAsInt();
            this.monthlyBreakdown = jsonObject.getAsJsonArray("monthly_breakdown");
        }

        public String getYear() {
            return year;
        }

        public int getYearlyEmi() {
            return yearlyEmi;
        }

        public int getYearlyPrincipal() {
            return yearlyPrincipal;
        }

        public int getYearlyInterest() {
            return yearlyInterest;
        }

        public int getYearlyRemainingLoanAmount() {
            return yearlyRemainingLoanAmount;
        }

        public JsonArray getMonthlyBreakdown() {
            return monthlyBreakdown;
        }
    }

    public static class Monthlyemi_breakdown {
        private String date;
        private String month;
        private int monthlyEmi;
        private int monthlyPrincipal;
        private int monthlyInterest;
        private int remainingLoanAmount;

        public Monthlyemi_breakdown(JsonObject jsonObject) {
            this.date = jsonObject.get("date").getAsString();
            this.month = jsonObject.get("month").getAsString();
            this.monthlyEmi = jsonObject.get("monthly_emi").getAsInt();
            this.monthlyPrincipal = jsonObject.get("monthly_principal").getAsInt();
            this.monthlyInterest = jsonObject.get("monthly_interest").getAsInt();
            this.remainingLoanAmount = jsonObject.get("remaining_loan_amount").getAsInt();
        }

        public String getDate() {
            return date;
        }

        public String getMonth() {
            return month;
        }

        public int getMonthlyEmi() {
            return monthlyEmi;
        }

        public int getMonthlyPrincipal() {
            return monthlyPrincipal;
        }

        public int getMonthlyInterest() {
            return monthlyInterest;
        }

        public int getRemainingLoanAmount() {
            return remainingLoanAmount;
        }
    }
}
