package specificstep.com.ui.DMTTransactionList;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import specificstep.com.Adapters.DMTPaymentListAdapter;
import specificstep.com.Database.DatabaseHelper;
import specificstep.com.Database.NotificationTable;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.GlobalClasses.MCrypt;
import specificstep.com.GlobalClasses.TransparentProgressDialog;
import specificstep.com.GlobalClasses.URL;
import specificstep.com.Models.DMTPaymentListModel;
import specificstep.com.Models.Default;
import specificstep.com.Models.User;
import specificstep.com.R;
import specificstep.com.ui.home.HomeContract;
import specificstep.com.utility.InternetUtil;
import specificstep.com.utility.Utility;

/**
 * A simple {@link Fragment} subclass.
 */
public class DMTPaymentsListFragment extends Fragment {

    View view;
    //TextView txtNoData;
    public static RecyclerView recyclerView;
    public static ImageView imgNoData;
    public static RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    private TransparentProgressDialog transparentProgressDialog;
    private AlertDialog alertDialog;
    Dialog dialogError;

    private String strMacAddress, strUserName, strOtpCode, strRegistrationDateTime;
    private ArrayList<User> userArrayList;
    DatabaseHelper databaseHelper;
    private final int SUCCESS_LOAD = 0, ERROR = 1;

    public static ArrayList<DMTPaymentListModel> mDmtPaymentListModelArrayList;
    DMTPaymentListModel paymentListModel;

    public DMTPaymentsListFragment() {
        // Required empty public constructor
    }

    private HomeContract.HomeDelegate homeDelegate;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeContract.HomeDelegate) {
            homeDelegate = (HomeContract.HomeDelegate) context;
        }
    }

    @Override
    public void onDetach() {
        homeDelegate = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dmtpayments_list, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initialize();
        makeSearchSenderCall();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(homeDelegate != null) {
            homeDelegate.setToolBarTitle("DMT Transaction List");
        }

    }

    public void initialize() {

        recyclerView = (RecyclerView) view.findViewById(R.id.ll_recycler_payments_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        imgNoData = (ImageView) view.findViewById(R.id.imgDmtPaymentListNoData);
        //txtNoData = (TextView) view.findViewById(R.id.txtPaymentsListNoData);
        alertDialog = new AlertDialog.Builder(getActivity()).create();
        dialogError = new Dialog(getActivity());
        dialogError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        databaseHelper = new DatabaseHelper(getActivity());
        userArrayList = databaseHelper.getUserDetail();
        // Store user information in variables
        strMacAddress = userArrayList.get(0).getDevice_id();
        strUserName = userArrayList.get(0).getUser_name();
        strOtpCode = userArrayList.get(0).getOtp_code();
        strRegistrationDateTime = userArrayList.get(0).getReg_date();

    }

    private void makeSearchSenderCall() {
        showProgressDialog();
        // create new threadc
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.transactionList;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "app"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strUserName,
                            strMacAddress,
                            strOtpCode,
                            Constants.APP_VERSION
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    myHandler.obtainMessage(SUCCESS_LOAD, response).sendToTarget();
                }
                catch (Exception ex) {
                    System.out.println("Error in Cash book native method");
                    System.out.println("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    // handle thread messages
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if(msg.what == SUCCESS_LOAD) {
                parseSuccessAddResponse(msg.obj.toString());
                dismissProgressDialog();
            } else if (msg.what == ERROR) {
                dismissProgressDialog();
                displayErrorDialog(msg.obj.toString());
            }
        }
    };

    // parse success response
    private void parseSuccessAddResponse(String response) {
        System.out.println("DMT payment list Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getInt("status") == 1) {
                //ll_recycler_view.setVisibility(View.VISIBLE);
                String encrypted_response = jsonObject.getString("data");
                String message = jsonObject.getString("msg");
                System.out.println("AccountLedger : " + "Message : " + message);
                System.out.println("AccountLedger : " + "encrypted_response : " + encrypted_response);
                String decrypted_response = Constants.decryptAPI((Activity) getActivity(), encrypted_response, strMacAddress);
                System.out.println("AccountLedger : " + "decrypted_response : " + decrypted_response);

                JSONArray array = new JSONArray(decrypted_response);
                if(array.length()>0) {
                    mDmtPaymentListModelArrayList = new ArrayList<DMTPaymentListModel>();
                    for(int i=0;i<array.length();i++) {
                        JSONObject object = array.getJSONObject(i);
                        paymentListModel = new DMTPaymentListModel();
                        paymentListModel.setBeneficiary_first_name(object.getString("beneficiary_first_name"));
                        paymentListModel.setBeneficiary_last_name(object.getString("beneficiary_last_name"));
                        paymentListModel.setBeneficiary_mobile_number(object.getString("beneficiary_mobile_number"));
                        paymentListModel.setTblsendid(object.getString("tblsendid"));
                        paymentListModel.setSender_firstname(object.getString("sender_firstname"));
                        paymentListModel.setSender_lastname(object.getString("sender_lastname"));
                        paymentListModel.setSender_mobilenumber(object.getString("sender_mobilenumber"));
                        paymentListModel.setSender_altmobilenumber(object.getString("sender_altmobilenumber"));
                        paymentListModel.setSender_email_address(object.getString("sender_email_address"));
                        paymentListModel.setTbltransid(object.getString("tbltransid"));
                        paymentListModel.setTrans_id(object.getString("trans_id"));
                        paymentListModel.setTblsender_id(object.getString("tblsender_id"));
                        paymentListModel.setVendor_id(object.getString("vendor_id"));
                        paymentListModel.setAmount(object.getString("amount"));
                        paymentListModel.setTransaction_id(object.getString("transaction_id"));
                        paymentListModel.setProvider_id(object.getString("provider_id"));
                        paymentListModel.setBank(object.getString("bank"));
                        paymentListModel.setTblbeneficiary_id(object.getString("tblbeneficiary_id"));
                        paymentListModel.setTblapi_id(object.getString("tblapi_id"));
                        paymentListModel.setTbluser_id(object.getString("tbluser_id"));
                        paymentListModel.setApi_brid(object.getString("api_brid"));
                        paymentListModel.setTransaction_status(object.getString("transaction_status"));
                        paymentListModel.setAdd_date(object.getString("add_date"));
                        mDmtPaymentListModelArrayList.add(paymentListModel);
                    }

                    if(mDmtPaymentListModelArrayList.size()>0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        imgNoData.setVisibility(View.GONE);
                        adapter = new DMTPaymentListAdapter(getActivity(), mDmtPaymentListModelArrayList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        imgNoData.setVisibility(View.VISIBLE);
                    }

                } else {
                    recyclerView.setVisibility(View.GONE);
                    imgNoData.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    // show progress dialog
    private void showProgressDialog() {
        try {
            if (transparentProgressDialog == null) {
                transparentProgressDialog = new TransparentProgressDialog(getActivity(), R.drawable.fotterloading);
            }
            if (transparentProgressDialog != null) {
                if (!transparentProgressDialog.isShowing()) {
                    transparentProgressDialog.show();
                }
            }
        }
        catch (Exception ex) {
            System.out.println("Error in show progress");
            System.out.println("Error : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // dismiss progress dialog
    private void dismissProgressDialog() {
        try {
            if (transparentProgressDialog != null) {
                if (transparentProgressDialog.isShowing())
                    transparentProgressDialog.dismiss();
            }
        }
        catch (Exception ex) {
            System.out.println("Error in dismiss progress");
            System.out.println("Error : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // display error in dialog
    private void displayErrorDialog(String message) {
        /* [START] - 2017_05_01 - Close all alert dialog logic */
        try {
            if (!alertDialog.isShowing()) {
                alertDialog.setTitle("Info!");
                alertDialog.setCancelable(false);
                alertDialog.setMessage(message);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        }
        catch (Exception ex) {
            System.out.println("Error in error dialog");
            System.out.println("Error : " + ex.getMessage());
            ex.printStackTrace();
            try {
                Utility.toast(getActivity(), message);
            }
            catch (Exception e) {
                System.out.println("Error in toast message");
                System.out.println("ERROR : " + e.getMessage());
            }
        }
        // [END]
    }

    public void showErrorDialog(String message) {
        try {
            if (!alertDialog.isShowing()) {
                alertDialog.setTitle("Info!");
                alertDialog.setCancelable(false);
                alertDialog.setMessage(message);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        }
        catch (Exception ex) {
            System.out.println("Error in error dialog");
            System.out.println("Error : " + ex.getMessage());
            ex.printStackTrace();
            try {
                Utility.toast(getActivity(), message);
            }
            catch (Exception e) {
                System.out.println("Error in toast message");
                System.out.println("ERROR : " + e.getMessage());
            }
        }
    }

}
