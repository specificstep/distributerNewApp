package specificstep.com.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;


import com.google.common.base.Strings;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import specificstep.com.Database.DatabaseHelper;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.GlobalClasses.MCrypt;
import specificstep.com.GlobalClasses.TransparentProgressDialog;
import specificstep.com.GlobalClasses.URL;
import specificstep.com.Models.CashSummaryModel;
import specificstep.com.Models.Default;
import specificstep.com.Models.User;
import specificstep.com.Models.UserList;
import specificstep.com.data.source.local.Pref;
import specificstep.com.ui.cashSummary.CashSummaryFragment;
import specificstep.com.utility.DateTime;
import specificstep.com.utility.InternetUtil;
import specificstep.com.utility.Utility;
import specificstep.com.R;

/**
 * Created by ubuntu on 10/5/17.
 */

public class CashSummeryAdapter extends BaseAdapter {
    private List<CashSummaryModel> cashSummeryModelArrayList;
    private Context context;
    private DatabaseHelper databaseHelper;
    private LayoutInflater inflater;

    private final int SUCCESS = 1, ERROR = 2, SUCCESS_MESSAGE = 3, ERROR_MESSAGE = 4;
    private TransparentProgressDialog progressDialog1;
    private ArrayList<User> userArrayList;
    private AlertDialog alertDialog;
    private OnReverseBalanceListener listener;
    Pref pref, pref1;
    public interface OnReverseBalanceListener {
        void onBalanceReversed();
    }

    public CashSummeryAdapter(
            Context activity,
            List<CashSummaryModel> cashSummeryModelArrayList,
            OnReverseBalanceListener listener) {

        context = activity;
        this.listener = listener;
        this.cashSummeryModelArrayList = cashSummeryModelArrayList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        databaseHelper = new DatabaseHelper(context);
        userArrayList = databaseHelper.getUserDetail();
        progressDialog1 = new TransparentProgressDialog(context, R.drawable.fotterloading);
    }

    @Override
    public int getCount() {
        return cashSummeryModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return cashSummeryModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class RowHolder {
        private TextView tv_payment_Id, /*tv_username, tv_email, tv_mobile,*/ tv_amount,
                tv_date, tv_balance/*, tv_debit_balance*/;
        private TextView /*txtPaymentFrom, txtPaymentTo, */txtAmount, /*txtDebitAmount,*/
                txtRemarks;
        private TextView txtReverse, txtReverseDateTime;
        //private LinearLayout llReverse, llReverseDateTime;
        //private TableRow tbrCreditAmount, tbrDebitAmount, tbrCreditBalance, tbrDebitBalance;
    }

    @Inject
    public CashSummeryAdapter(Pref pref) {
        this.pref = pref;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final RowHolder rowHolder;
        if (convertView == null) {
            rowHolder = new RowHolder();
            convertView = inflater.inflate(R.layout.item_cash_summery, parent, false);
            rowHolder.tv_payment_Id = (TextView) convertView.findViewById(R.id.rec_trans_payment_id);
            //rowHolder.tv_username = (TextView) convertView.findViewById(R.id.rec_trans_username);
            //rowHolder.tv_email = (TextView) convertView.findViewById(R.id.rec_trans_email);
            //rowHolder.tv_mobile = (TextView) convertView.findViewById(R.id.rec_trans_mobile);
            rowHolder.tv_amount = (TextView) convertView.findViewById(R.id.rec_trans_amount);
            rowHolder.tv_balance = (TextView) convertView.findViewById(R.id.rec_trans_balance);
            rowHolder.tv_date = (TextView) convertView.findViewById(R.id.rec_trans_date);
            /*rowHolder.tv_credit_balance = (TextView) convertView.findViewById(R.id.rec_trans_credit_balance);
            rowHolder.tv_debit_balance = (TextView) convertView.findViewById(R.id.rec_trans_debit_balance);*/
            /* [START] - Reverse field */
            rowHolder.txtReverse = (TextView) convertView.findViewById(R.id.txt_CashSummery_Reverse);
            rowHolder.txtReverseDateTime = (TextView) convertView.findViewById(R.id.txt_CashSummery_ReverseDateTime);
            /*rowHolder.llReverse = (LinearLayout) convertView.findViewById(R.id.ll_CashSummery_Reverse);
            rowHolder.llReverseDateTime = (LinearLayout) convertView.findViewById(R.id.ll_CashSummery_ReverseDateTime);
            rowHolder.txtPaymentFrom = (TextView) convertView.findViewById(R.id.rec_trans_PaymentFrom);
            rowHolder.txtPaymentTo = (TextView) convertView.findViewById(R.id.rec_trans_PaymentTo);
            rowHolder.txtCreditAmount = (TextView) convertView.findViewById(R.id.rec_trans_CreditAmount);
            rowHolder.txtDebitAmount = (TextView) convertView.findViewById(R.id.rec_trans_DebitAmount);*/
            rowHolder.txtRemarks = (TextView) convertView.findViewById(R.id.rec_trans_remark);
            /*rowHolder.tbrCreditAmount = (TableRow) convertView.findViewById(R.id.tbrCreditAmount);
            rowHolder.tbrDebitAmount = (TableRow) convertView.findViewById(R.id.tbrDebitAmount);
            rowHolder.tbrCreditBalance = (TableRow) convertView.findViewById(R.id.tbrCreditBalance);
            rowHolder.tbrDebitBalance = (TableRow) convertView.findViewById(R.id.tbrDebitBalance);*/
            // [END]
            convertView.setTag(rowHolder);
        } else {
            rowHolder = (RowHolder) convertView.getTag();
        }

        final CashSummaryModel cashSummeryModel = cashSummeryModelArrayList.get(position);

        rowHolder.tv_payment_Id.setText(cashSummeryModel.getPayment_id().trim());
        //rowHolder.tv_amount.setText(cashSummeryModel.getAmount());
        rowHolder.tv_date.setText("Date : " + DateTime.getFormattedDate(cashSummeryModel.getDate_time(), DateTime.YYYY_MM_DD_HH_MM_SS_DATE_FORMAT, Constants.DATE_TIME_FORMAT));

        /*rowHolder.tv_username.setText(cashSummeryModel.getUsername());
        rowHolder.tv_email.setText(cashSummeryModel.getEmail());
        rowHolder.tv_mobile.setText(cashSummeryModel.getMobile());

        rowHolder.tv_credit_balance.setText(cashSummeryModel.getCreditUserBalance());
        rowHolder.tv_debit_balance.setText(cashSummeryModel.getDebitUserBalance());

        *//* [START] - Set values in new fields *//*
        rowHolder.txtPaymentFrom.setText(cashSummeryModel.getUsername());
        rowHolder.txtPaymentTo.setText(cashSummeryModel.getPaymentTo());

        // [END]

        *//* [START] - 2017_04_20 - Add RS symbol with amount & Add .00 after amount *//*
        rowHolder.txtCreditAmount.setText(addRsSymbol(cashSummeryModel.getCreditAmount()));
        rowHolder.txtDebitAmount.setText(addRsSymbol(cashSummeryModel.getDebitAmount()));
*/

        try {
            int id1 = CashSummaryFragment.ll.getCheckedRadioButtonId();
            String user_id1 = "";
            if (id1 != 2) {
                RadioButton selectedRadioButton = (RadioButton) CashSummaryFragment.view.findViewById(id1);
                ArrayList<UserList> userListArrayList = databaseHelper.getUserListDetailsByType(selectedRadioButton.getText().toString());
                List<UserList> data = new ArrayList<UserList>();
                for (UserList userList : userListArrayList) {
                    if (userList.getParentUserId().equals(userArrayList.get(0).getUser_id())) {
                        data.add(userList);
                    }
                }
                UserList userList = data.get(CashSummaryFragment.spnPosition);
                user_id1 = userList.getUser_id();
            } else {
                SharedPreferences SharedPreferences = context.getSharedPreferences(Pref.PREF_FILE, Context.MODE_PRIVATE);
                pref = new Pref(SharedPreferences);
                user_id1 = pref.getValue(Pref.KEY_USER_ID, "");
            }
            System.out.println("Selected list UserID: " + user_id1);

            int indexDebit = cashSummeryModel.getUsername().indexOf("-");
            int indexCredit = cashSummeryModel.getPaymentTo().indexOf("-");
            String idFrom = cashSummeryModel.getUsername().substring(0, indexDebit - 1);
            String idTo = cashSummeryModel.getPaymentTo().substring(0, indexCredit - 1);


            if (idTo.trim().equals(user_id1)) {
                rowHolder.tv_amount.setText("Amount : " + addRsSymbol(cashSummeryModel.getCreditAmount()));
                rowHolder.tv_balance.setText("Balance : " + addRsSymbol(cashSummeryModel.getCreditUserBalance()));
                rowHolder.txtRemarks.setText("CREDITED FOR " + cashSummeryModel.getRemarks().toUpperCase() + " FROM " + cashSummeryModel.getUsername().toUpperCase());
            } else if (idFrom.trim().equals(user_id1)) {
                rowHolder.tv_amount.setText("Amount : " + addRsSymbol(cashSummeryModel.getDebitAmount()));
                rowHolder.tv_balance.setText("Balance : " + addRsSymbol(cashSummeryModel.getDebitUserBalance()));
                rowHolder.txtRemarks.setText("DEBITED FOR " + cashSummeryModel.getRemarks().toUpperCase() + " TO " + cashSummeryModel.getPaymentTo().toUpperCase());
            }

            // [END]

            /* [START] - 2017_05_09 - Add validation for display reverse button
             * if  p_serviceid not equal to 8 and empty credit_datetime then display reverse button */
//        Log.d("CashSummery", "DATE : " + position + " : " + cashSummeryModel.getCreditDateTime()
//                + " - Service ID : " + cashSummeryModel.getpServiceId());

            SharedPreferences SharedPreferences = context.getSharedPreferences(Pref.PREF_FILE, Context.MODE_PRIVATE);
            pref = new Pref(SharedPreferences);
            String user_id = pref.getValue(Pref.KEY_USER_ID, "");
            System.out.println("Pref user id: " + user_id);
            int index = cashSummeryModel.getUsername().indexOf("-");
            System.out.println("Pref username: " + cashSummeryModel.getUsername());
            String id = cashSummeryModel.getUsername().substring(0, index);
            System.out.println("Pref ID: " + id.trim());
            if (!TextUtils.equals(cashSummeryModel.getpServiceId(), "8")
                    && (TextUtils.isEmpty(cashSummeryModel.getCreditDateTime())
                    /*&& id.trim().equals(user_id)*/
                    || cashSummeryModel.getCreditDateTime().trim().length() == 0) && id.trim().equals(user_id)) {
                // if date time is same as current date time then display reverse button else hide button
            /*if (isCurrentDateSame(cashSummeryModel.getDate_time())) {
                rowHolder.llReverse.setVisibility(View.VISIBLE);
            } else {
                rowHolder.llReverse.setVisibility(View.GONE);
            }*/
                rowHolder.txtReverse.setVisibility(View.VISIBLE);
                rowHolder.txtReverseDateTime.setVisibility(View.GONE);
            } else {
                rowHolder.txtReverse.setVisibility(View.GONE);
                if (!Strings.isNullOrEmpty(cashSummeryModel.getCreditDateTime())) {
                    rowHolder.txtReverseDateTime.setVisibility(View.VISIBLE);
                    rowHolder.txtReverseDateTime.setText("Reverse Date Time: " + cashSummeryModel.getCreditDateTime());
                } else {
                    rowHolder.txtReverseDateTime.setVisibility(View.GONE);
                }
            }
            // [END]

            rowHolder.txtReverse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayCashSummeryDialog(
                            cashSummeryModel.getPayment_id(),
                            cashSummeryModel.getUsername(),
                            cashSummeryModel.getPaymentTo(),
                            cashSummeryModel.getDebitAmount(),
                            cashSummeryModel.getCreditAmount(),
                            cashSummeryModel.getRemarks(),
                            cashSummeryModel.getDate_time());
                }
            });
        } catch (Exception e) {
            System.out.println("Data bind error: " + e.toString());
        }

        return convertView;
    }

    // Is current date match with add balance date time or not
    // Format = 2017-05-09 17:32:29
    private boolean isCurrentDateSame(String dateTime) {
        try {
            String addBalanceDate = dateTime.substring(0, 10);
            String separator = dateTime.substring(4, 5);
            String currentDate = DateTime.getDate_YYYY_MM_DD(separator);
            // Log.d("CashSummery", "addBalanceDate : " + addBalanceDate + " ,separator : " + separator + " ,currentDate : " + currentDate);
            if (TextUtils.equals(addBalanceDate, currentDate)) {
                return true;
            } else {
                return false;
            }
        }
        catch (Exception ex) {
            Log.e("CashSummery", "Error in current date check");
            ex.printStackTrace();
            return false;
        }
    }

    private void displayCashSummeryDialog(String paymentId, String paymetFrom, String paymentTo,
                                          String debitAmount, String creditAmount, String remarks, String dateTime) {
        final Dialog dialogCashSummery = new Dialog(context);
        dialogCashSummery.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCashSummery.setContentView(R.layout.dialog_cash_summery);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogCashSummery.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialogCashSummery.getWindow().setAttributes(lp);
        dialogCashSummery.setCancelable(false);

        final TextView txtPaymentId = (TextView) dialogCashSummery.findViewById(R.id.txt_Dialog_CashSummery_PaymentId);
        TextView txtPaymentFrom = (TextView) dialogCashSummery.findViewById(R.id.txt_Dialog_CashSummery_PaymentFrom);
        TextView txtPaymentTo = (TextView) dialogCashSummery.findViewById(R.id.txt_Dialog_CashSummery_PaymentTo);
        TextView txtDebitAmount = (TextView) dialogCashSummery.findViewById(R.id.txt_Dialog_CashSummery_DebitAmount);
        TextView txtCreditAmount = (TextView) dialogCashSummery.findViewById(R.id.txt_Dialog_CashSummery_CreditAmount);
        TextView txtRemarks = (TextView) dialogCashSummery.findViewById(R.id.txt_Dialog_CashSummery_Remarks);
        TextView txtDateTime = (TextView) dialogCashSummery.findViewById(R.id.txt_Dialog_CashSummery_DateTime);
        Button btnReverse = (Button) dialogCashSummery.findViewById(R.id.btn_Dialog_CashSummery_Reverse);
        Button btnCancel = (Button) dialogCashSummery.findViewById(R.id.btn_Dialog_CashSummery_Cancel);

        Log.d("CashSummery", "Payment Id : " + paymentId);
        Log.d("CashSummery", "Payment From : " + paymetFrom);
        Log.d("CashSummery", "Payment To : " + paymentTo);
        Log.d("CashSummery", "Debit Amount : " + debitAmount);
        Log.d("CashSummery", "Credit Amount : " + creditAmount);
        Log.d("CashSummery", "Remarks : " + remarks);
        Log.d("CashSummery", "Date Time : " + dateTime);

        txtPaymentId.setText(paymentId.trim());
        txtPaymentFrom.setText(paymetFrom);
        txtPaymentTo.setText(paymentTo);
        txtDebitAmount.setText(addRsSymbol(debitAmount));
        txtCreditAmount.setText(addRsSymbol(creditAmount));
        txtRemarks.setText(remarks);
        txtDateTime.setText(dateTime);

        btnReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_device_id = "";
                str_device_id = userArrayList.get(0).getDevice_id();
                String paymentId = txtPaymentId.getText().toString().trim();
                String encodedPaymentId = "";
                ArrayList<Default> defaultArrayList;
                defaultArrayList = databaseHelper.getDefaultSettings();
                String user_id = defaultArrayList.get(0).getUser_id();
                Log.d("CashSummery", "device_id : " + str_device_id);
                MCrypt mCrypt = new MCrypt(user_id, str_device_id);
                try {
                    byte[] encrypted_bytes = mCrypt.encrypt(paymentId);
                    encodedPaymentId = Base64.encodeToString(encrypted_bytes, Base64.DEFAULT);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                dialogCashSummery.cancel();
                showProgressDialog();
                makeReverseBalance(encodedPaymentId);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCashSummery.cancel();
            }
        });

        dialogCashSummery.show();
    }

    private String addRsSymbol(String amount) {
        String cAmount = amount;
        // Decimal format
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat format = new DecimalFormat("0.#");
        format.setDecimalFormatSymbols(symbols);
        // Add RS symbol in credit and debit amount
        try {
            if (!TextUtils.equals(cAmount, "0")) {
                cAmount = context.getResources().getString(R.string.currency_format, String.valueOf(format.parse(cAmount).floatValue()));
            }
        }
        catch (Exception ex) {
            Log.e("Cash Adapter", "Error in decimal number");
            Log.e("Cash Adapter", "Error : " + ex.getMessage());
            ex.printStackTrace();
            cAmount = context.getResources().getString(R.string.currency_format, amount);
        }
        return cAmount;
    }

    private void makeReverseBalance(final String paymentId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set login user url
                    String url = URL.GET_REVERSE_PAYMENT;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "app",
                            "paymentid"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            userArrayList.get(0).getUser_name(),
                            userArrayList.get(0).getDevice_id(),
                            userArrayList.get(0).getOtp_code(),
                            Constants.APP_VERSION,
                            paymentId
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    myHandler.obtainMessage(SUCCESS, response).sendToTarget();
                }
                catch (Exception ex) {
                    Log.e("CashSummery", "Error in CashSummery");
                    Log.e("CashSummery", "Error : " + ex.getMessage());
                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    // parse success response
    private void parseReverseBalanceResponse(String response) {
        Log.i("CashSummery", "Child Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String message = jsonObject.getString("msg");
                myHandler.obtainMessage(SUCCESS_MESSAGE, message).sendToTarget();
            } else {
                String message = jsonObject.getString("msg");
                myHandler.obtainMessage(ERROR_MESSAGE, message).sendToTarget();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            myHandler.obtainMessage(ERROR_MESSAGE, "No result found").sendToTarget();
        }
    }

    // handle user login messages
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == SUCCESS) {
                dismissProgressDialog();
                parseReverseBalanceResponse(msg.obj.toString());
            } else if (msg.what == ERROR) {
                dismissProgressDialog();
                displayErrorDialog("Reverse Payment Error", msg.obj.toString());
            } else if (msg.what == SUCCESS_MESSAGE) {
                displaySuccessDialog("Reverse Payment", msg.obj.toString());
            } else if (msg.what == ERROR_MESSAGE) {
                displayErrorDialog("Reverse Payment Error", msg.obj.toString());
            }
        }
    };

    // Display user login error dialog
    private void displaySuccessDialog(String title, String message) {
        /* [START] - 2017_05_01 - Close all alert dialog logic */
        try {
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(title);
            alertDialog.setCancelable(false);
            alertDialog.setMessage(message);
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                    if(listener != null) {
                        listener.onBalanceReversed();
                    }
                }
            });
            alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorBlack));
                }
            });
            alertDialog.show();
        }
        catch (Exception ex) {
            Log.e("CashSummery", "Error in error dialog");
            Log.e("CashSummery", "Error : " + ex.getMessage());
            ex.printStackTrace();
            try {
                Utility.toast(context, message);
            }
            catch (Exception e) {
                Log.e("CashSummery", "Error in toast message");
                Log.e("CashSummery", "ERROR : " + e.getMessage());
            }
        }
        // [END]
    }

    // Display user login error dialog
    private void displayErrorDialog(String title, String message) {
        /* [START] - 2017_05_01 - Close all alert dialog logic */
        try {
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(title);
            alertDialog.setCancelable(false);
            alertDialog.setMessage(message);
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorBlack));
                }
            });
            alertDialog.show();
        }
        catch (Exception ex) {
            Log.e("CashSummery", "Error in error dialog");
            Log.e("CashSummery", "Error : " + ex.getMessage());
            ex.printStackTrace();
            try {
                Utility.toast(context, message);
            }
            catch (Exception e) {
                Log.e("CashSummery", "Error in toast message");
                Log.e("CashSummery", "ERROR : " + e.getMessage());
            }
        }
        // [END]
    }

    // dismiss progress dialog
    private void dismissProgressDialog() {
        if (progressDialog1 != null) {
            if (progressDialog1.isShowing() == true)
                progressDialog1.dismiss();
        }
    }

    // show progress dialog
    private void showProgressDialog() {
        if (progressDialog1 == null) {
            progressDialog1 = new TransparentProgressDialog(context, R.drawable.fotterloading);
        }
        if (!progressDialog1.isShowing()) {
            progressDialog1.show();
        }
    }
}
