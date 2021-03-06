package specificstep.com.ui.PaymentRequest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import specificstep.com.Adapters.PaymentRequestListAdapter;
import specificstep.com.Database.DatabaseHelper;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.GlobalClasses.MCrypt;
import specificstep.com.GlobalClasses.TransparentProgressDialog;
import specificstep.com.GlobalClasses.URL;
import specificstep.com.Models.Default;
import specificstep.com.Models.PaymentRequestListModel;
import specificstep.com.Models.User;
import specificstep.com.Models.WalletsModel;
import specificstep.com.R;
import specificstep.com.utility.InternetUtil;
import specificstep.com.utility.Utility;

public class PaymentRequestListFragment extends Fragment {

    View view;
    ImageView imgNoData;
    public static RecyclerView recyclerView;
    public static RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    private TransparentProgressDialog transparentProgressDialog;
    private AlertDialog alertDialog;
    Dialog dialogError;
    private String strMacAddress, strUserName, strOtpCode, strRegistrationDateTime;
    private ArrayList<User> userArrayList;
    DatabaseHelper databaseHelper;
    private final int SUCCESS_LOAD = 0, ERROR = 1, SUCCESS_WALLET_LIST = 2;

    ArrayList<PaymentRequestListModel> requestModelsList;
    PaymentRequestListModel requestModel;

    public PaymentRequestListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_payment_request_list, container, false);
        initialize();
        try {
            if (Constants.checkInternet(getActivity())) {
                //makeWalletCall();
                makePaymentRequestListCall();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return view;
    }

    public void initialize() {

        recyclerView = (RecyclerView) view.findViewById(R.id.ll_recycler_payment_request_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        imgNoData = (ImageView) view.findViewById(R.id.imgNoDataPaymentRequestList);
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

    public void makePaymentRequestListCall() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = URL.paymentRequestList;
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "app"
                    };
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
                    System.out.println("Error : " + ex.getMessage());
                    ex.printStackTrace();
                    dismissProgressDialog();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setVisibility(View.GONE);
                            imgNoData.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        }).start();

    }

    // handle thread messages
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == SUCCESS_LOAD) {
                parseSuccessResponse(msg.obj.toString());
                dismissProgressDialog();
            } else if (msg.what == ERROR) {
                dismissProgressDialog();
                displayErrorDialog(msg.obj.toString());
            }
        }
    };

    public void parseSuccessResponse(String response) {
        System.out.println("payment request list Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getInt("status") == 1) {
                String encrypted_response = jsonObject.getString("data");
                String decrypted_response = Constants.decryptAPI((Activity) getActivity(), encrypted_response, strMacAddress);
                System.out.println("Payment Request List : " + "decrypted_response : " + decrypted_response);
                JSONArray array = new JSONArray(decrypted_response);
                if(array.length()>0) {
                    requestModelsList = new ArrayList<PaymentRequestListModel>();
                    for(int i=0;i<array.length();i++) {
                        JSONObject object = array.getJSONObject(i);
                        requestModel = new PaymentRequestListModel();
                        requestModel.setDatetime(object.getString("datetime"));
                        requestModel.setAmount(object.getString("amount"));
                        requestModel.setWallet_name(object.getString("wallet_name"));
                        requestModel.setDeposit_bank(object.getString("deposit_bank"));
                        requestModel.setStatus(object.getString("status"));
                        requestModel.setRemark(object.getString("remark"));
                        requestModel.setAdmin_remark(object.getString("admin_remark"));
                        requestModelsList.add(requestModel);
                    }
                    Collections.reverse(requestModelsList);
                    if(requestModelsList.size()>0) {
                        adapter = new PaymentRequestListAdapter(getActivity(),requestModelsList);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        imgNoData.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        imgNoData.setVisibility(View.VISIBLE);
                    }

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
            System.out.println("Error : " + ex.getMessage());
            ex.printStackTrace();
            try {
                Utility.toast(getActivity(), message);
            }
            catch (Exception e) {
                System.out.println("ERROR : " + e.getMessage());
            }
        }
        // [END]
    }
}
