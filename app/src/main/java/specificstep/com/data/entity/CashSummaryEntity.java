package specificstep.com.data.entity;

import com.google.gson.annotations.SerializedName;

public class CashSummaryEntity {
    @SerializedName("Payment Id")
    String paymentId;
    @SerializedName("Payment From")
    String username; // paymentFrom
    @SerializedName("email")
    String email = "";
    @SerializedName("mobile")
    String mobile = "";
    @SerializedName("Datetime")
    String dateTime;
    @SerializedName("Credit Amount")
    String creditAmount;
    @SerializedName("Debit Amount")
    String debitAmount;
    @SerializedName("Remarks")
    String remarks;
    @SerializedName("Payment To")
    String paymentTo;
    @SerializedName("p_serviceid")
    private String pServiceId;
    @SerializedName("credit_datetime")
    private String creditDateTime;
    @SerializedName("credit_user_balance")
    private String creditUserBalance;
    @SerializedName("debit_user_balance")
    private String debitUserBalance;

    public String getPaymentId() {
        return paymentId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAmount() {
        return debitAmount;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getCreditAmount() {
        return creditAmount;
    }

    public String getDebitAmount() {
        return debitAmount;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getPaymentTo() {
        return paymentTo;
    }

    public String getpServiceId() {
        return pServiceId;
    }

    public String getCreditDateTime() {
        return creditDateTime;
    }

    public String getCreditUserBalance() {
        return creditUserBalance;
    }

    public String getDebitUserBalance() {
        return debitUserBalance;
    }
}
