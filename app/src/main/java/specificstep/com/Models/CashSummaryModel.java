package specificstep.com.Models;

/**
 * Created by programmer044 on 28/02/17.
 */

public class CashSummaryModel {

    String payment_id;
    String username; // paymentFrom
    String email;
    String mobile;
    String amount;
    String date_time;

    /* [START] - Add new fields */
    String creditAmount;
    String debitAmount;
    String remarks;
    String paymentTo;
    String creditUserBalance;
    String debitUserBalance;
    // [END]

    /* [START] - 2017_05_09 - Add new field for reverse balance */
    private String pServiceId;
    private String creditDateTime;
    // [END]

    public String getpServiceId() {
        return pServiceId;
    }

    public void setpServiceId(String pServiceId) {
        this.pServiceId = pServiceId;
    }

    public String getCreditDateTime() {
        return creditDateTime;
    }

    public void setCreditDateTime(String creditDateTime) {
        this.creditDateTime = creditDateTime;
    }

    public String getPaymentTo() {
        return paymentTo;
    }

    public void setPaymentTo(String paymentTo) {
        this.paymentTo = paymentTo;
    }

    public String getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(String creditAmount) {
        this.creditAmount = creditAmount;
    }

    public String getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(String debitAmount) {
        this.debitAmount = debitAmount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getCreditUserBalance() {
        return creditUserBalance;
    }

    public void setCreditUserBalance(String creditUserBalance) {
        this.creditUserBalance = creditUserBalance;
    }

    public String getDebitUserBalance() {
        return debitUserBalance;
    }

    public void setDebitUserBalance(String debitUserBalance) {
        this.debitUserBalance = debitUserBalance;
    }
}
