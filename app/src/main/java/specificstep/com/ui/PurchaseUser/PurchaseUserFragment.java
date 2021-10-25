package specificstep.com.ui.PurchaseUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import specificstep.com.Database.DatabaseHelper;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.GlobalClasses.MCrypt;
import specificstep.com.GlobalClasses.TransparentProgressDialog;
import specificstep.com.GlobalClasses.URL;
import specificstep.com.Models.Default;
import specificstep.com.Models.PurchaseSchemeNameModel;
import specificstep.com.Models.PurchaseSchemeTypeModel;
import specificstep.com.Models.User;
import specificstep.com.R;
import specificstep.com.ui.home.HomeContract;
import specificstep.com.utility.InternetUtil;
import specificstep.com.utility.Utility;

public class PurchaseUserFragment extends Fragment implements View.OnClickListener {

    
    String TAG = "PurchaseUserFragment :: ";

    Spinner mySpinner, spSchemeName;
    Button mBtncancel, mBtnSubmit, mBtnViewDetail;
    EditText mEtQtytobuy, mEtAmount, mEtSchemeTds, mEtNetAmount;

    private final int SUCCESS = 1, ERROR = 2;
    private String strMacAddress, strUserName, strOtpCode, strRegistrationDateTime;
    private TransparentProgressDialog transparentProgressDialog;
    private AlertDialog alertDialog;
    private DatabaseHelper databaseHelper;
    private ArrayList<User> userArrayList;
    private ArrayList<PurchaseSchemeTypeModel> purchaseSchemeTypeList;
    PurchaseSchemeTypeModel mPurchaseSchemeTypeModel;
    private ArrayList<PurchaseSchemeNameModel> purchaseSchemeNameModelsArrayList;
    PurchaseSchemeNameModel mPurchaseSchemeNameModel;
    List<String> items = null;
    String discount_amt = "";
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (homeDelegate != null) {
            homeDelegate.setToolBarTitle(getString(R.string.purchase_user));
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_purchase_user, container, false);

        mySpinner = (Spinner) view.findViewById(R.id.spSchemeType);
       
        transparentProgressDialog = new TransparentProgressDialog(getActivity(), R.drawable.fotterloading);

        spSchemeName = (Spinner) view.findViewById(R.id.spSchemeName);
        mBtncancel = (Button) view.findViewById(R.id.mBtncancel);
        mBtnSubmit = (Button) view.findViewById(R.id.mBtnSubmit);
        mBtnViewDetail = (Button) view.findViewById(R.id.mBtnViewDetail);

        mEtQtytobuy = (EditText) view.findViewById(R.id.mEtQtytobuy);
        mEtAmount = (EditText) view.findViewById(R.id.mEtAmount);
        mEtSchemeTds = (EditText) view.findViewById(R.id.mEtSchemeTds);
        mEtNetAmount = (EditText) view.findViewById(R.id.mEtNetAmount);

        mBtnSubmit.setOnClickListener(this);
        mBtncancel.setOnClickListener(this);
        mBtnViewDetail.setOnClickListener(this);

        databaseHelper = new DatabaseHelper(getActivity());
        userArrayList = databaseHelper.getUserDetail();
        strMacAddress = userArrayList.get(0).getDevice_id();
        strUserName = userArrayList.get(0).getUser_name();
        strOtpCode = userArrayList.get(0).getOtp_code();

        //initialize purchase type aaray
        purchaseSchemeTypeList = new ArrayList<PurchaseSchemeTypeModel>();

        makePurchaseSchemeTypeApiCalls();

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                makePurchaseUserSchemeApiCalls();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spSchemeName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //spSchemeName.setAdapter(new MyPurchaseSchemeAdapter(getActivity(), R.layout.spinner_row, purchaseSchemeNameModelsArrayList));
                mEtAmount.setText(purchaseSchemeNameModelsArrayList.get(spSchemeName.getSelectedItemPosition()).getAmount());
                if(purchaseSchemeNameModelsArrayList.get(spSchemeName.getSelectedItemPosition()).getTds() == null || purchaseSchemeNameModelsArrayList.get(spSchemeName.getSelectedItemPosition()).getTds().equalsIgnoreCase("")) {
                    mEtSchemeTds.setText("0");
                } else {
                    mEtSchemeTds.setText(purchaseSchemeNameModelsArrayList.get(spSchemeName.getSelectedItemPosition()).getTds());
                }

                String margin = purchaseSchemeNameModelsArrayList.get(spSchemeName.getSelectedItemPosition()).getMargin();
                items = Arrays.asList(margin.split("\\s*,\\s*"));

                if(!TextUtils.isEmpty(mEtQtytobuy.getText())) {
                    int qty = Integer.valueOf(mEtQtytobuy.getText().toString());
                    if (qty <= 24) {
                        discount_amt = items.get(0);
                    } else if (qty >= 25 && qty <= 74) {
                        discount_amt = items.get(1);
                    } else if (qty >= 75 && qty <= 150) {
                        discount_amt = items.get(2);
                    } else if (qty >= 151) {
                        discount_amt = items.get(3);
                    }
                    int amt = Integer.valueOf(mEtQtytobuy.getText().toString()) * Integer.valueOf(mEtAmount.getText().toString());
                    if (!discount_amt.equals("0") || discount_amt != null || !discount_amt.equals("")) {
                        amt = amt - (Integer.valueOf(discount_amt) * amt) / 100;
                    }
                    mEtNetAmount.setText(amt + "");
                } else {
                    mEtNetAmount.setText("");
                }

                mEtQtytobuy.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {


                        if(s.length()<=0) {
                            mEtNetAmount.setText("");
                        } else {
                            int qty = Integer.valueOf(s.toString());
                            if (qty <= 24) {
                                discount_amt = items.get(0);
                            } else if (qty >= 25 && qty <= 74) {
                                discount_amt = items.get(1);
                            } else if (qty >= 75 && qty <= 150) {
                                discount_amt = items.get(2);
                            } else if (qty >= 151) {
                                discount_amt = items.get(3);
                            }
                            int amt = Integer.valueOf(mEtQtytobuy.getText().toString()) * Integer.valueOf(mEtAmount.getText().toString());
                            if (!discount_amt.equals("0") || discount_amt != null || !discount_amt.equals("")) {
                                amt = amt - (Integer.valueOf(discount_amt) * amt) / 100;
                            }
                            mEtNetAmount.setText(amt + "");

                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    public void makePurchaseSchemeTypeApiCalls() {
        showProgressDialog();
        //scheme type thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.purchase_user_schemetype;
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
                    System.out.println("Add user type response1: " + response);
                    myHandler.obtainMessage(SUCCESS, response).sendToTarget();
                }
                catch (Exception ex) {
                    Log.e(TAG, "  Error  : "+ ex.getMessage() );

                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtncancel:
                mEtQtytobuy.setText("");
                mEtNetAmount.setText("");
                break;
            case R.id.mBtnSubmit:
                if(!TextUtils.isEmpty(mEtQtytobuy.getText())) {
                    makePurchaseUserSubmitCalls();
                } else {
                    Toast.makeText(getActivity(), "Enter Quantity to Buy", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.mBtnViewDetail:
                showDialog();
                break;
        }
    }

    //Submit button call
    public void makePurchaseUserSubmitCalls() {
        showProgressDialog();
        //scheme name thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.purchase_user_submit;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "app",
                            "scheme_name",
                            "scheme_type",
                            "quantity"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strUserName,
                            strMacAddress,
                            strOtpCode,
                            Constants.APP_VERSION,
                            purchaseSchemeNameModelsArrayList.get(spSchemeName.getSelectedItemPosition()).getId(),
                            purchaseSchemeTypeList.get(mySpinner.getSelectedItemPosition()).getPurchaseTypeId(),
                            mEtQtytobuy.getText().toString()
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    System.out.println("user purchase response1: " + response);
                    myHandlerSubmit.obtainMessage(SUCCESS, response).sendToTarget();
                }
                catch (Exception ex) {
                    Log.e(TAG, "  Error  : "+ ex.getMessage() );

                    ex.printStackTrace();
                    myHandlerSubmit.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();

    }

    // handle submit thread messages
    private Handler myHandlerSubmit = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == SUCCESS) {
                dismissProgressDialog();
                parseSubmitSuccessResponse(msg.obj.toString());
            } else if (msg.what == ERROR) {
                dismissProgressDialog();
                displayErrorDialog(msg.obj.toString());
            }
        }
    };

    //parse submit api
    public void parseSubmitSuccessResponse(String response) {

        Log.e(TAG, " AccountLedger Response : "+ response );

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String message = jsonObject.getString("msg");
                Log.e(TAG, "Message : "+ message );
                Log.e(TAG, "Message : "+ message );
                Toast.makeText(getActivity(),message, Toast.LENGTH_LONG).show();

                mEtQtytobuy.setText("");
                mEtAmount.setText("");
                mEtSchemeTds.setText("");
                mEtNetAmount.setText("");
                getFragmentManager().beginTransaction().detach(this).attach(this).commit();
                //String decrypted_response = decryptAPI(encrypted_response);
                //System.out.println("Add User Type: decrypted_response : " + decrypted_response);
                //Log.e(TAG,"AccountLedger : " + "decrypted_response : " + decrypted_response);
            } else if (jsonObject.getString("status").equals("2")) {
                    Toast.makeText(getActivity(),jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
            } else {

            }
        }
        catch (JSONException e) {
            Log.e(TAG,"Cashbook : " + "Error 4 : " + e.getMessage());
            Utility.toast(getActivity(), "No result found");
            e.printStackTrace();
        }

    }

    public void showDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_purchase_discount_detail);

        TextView slab1 = (TextView) dialog.findViewById(R.id.txtDiscountSlab1);
        TextView slab2 = (TextView) dialog.findViewById(R.id.txtDiscountSlab2);
        TextView slab3 = (TextView) dialog.findViewById(R.id.txtDiscountSlab3);
        TextView slab4 = (TextView) dialog.findViewById(R.id.txtDiscountSlab4);
        TextView discount = (TextView) dialog.findViewById(R.id.txtDiscount);

        slab1.setText(items.get(0));
        slab2.setText(items.get(1));
        slab3.setText(items.get(2));
        slab4.setText(items.get(3));
        if(TextUtils.isEmpty(mEtQtytobuy.getText()) || mEtQtytobuy.getText().toString().equals("0")) {
            discount.setText("");
        } else {
            discount.setText("Apply Discount - " + discount_amt + "%");
        }

        Button dialogButton = (Button) dialog.findViewById(R.id.btnDiscountClose);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    // handle thread messages
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == SUCCESS) {
                dismissProgressDialog();
                parseSuccessResponse(msg.obj.toString());
            } else if (msg.what == ERROR) {
                dismissProgressDialog();
                displayErrorDialog(msg.obj.toString());
            }
        }
    };




    // parse success response
    private void parseSuccessResponse(String response) {
        Log.e("TAG","PurchaseUser Response : " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
               
                String encrypted_response = jsonObject.getString("data");
                String message = jsonObject.getString("msg");
                Log.e(TAG,"PurchaseUser : " + "Message : " + message);
                Log.e(TAG,"PurchaseUser : " + "encrypted_response : " + encrypted_response);
                String decrypted_response = Constants.decryptAPI((Activity) getActivity(), encrypted_response, strMacAddress);
                Log.e(TAG,"PurchaseUser : " + "decrypted_response : " + decrypted_response);
               //loadMoreData(decrypted_response);
                parsing_response(decrypted_response);
            }
        }
        catch (JSONException e) {
            Log.e(TAG,"Cashbook : " + "Error 4 : " + e.getMessage());
            Utility.toast(getActivity(), "No result found");
            e.printStackTrace();
        }
    }

    public void parsing_response(String response) throws JSONException {

        //add user type data into arraylist
        purchaseSchemeTypeList = new ArrayList<PurchaseSchemeTypeModel>();
        JSONObject objectData = new JSONObject(response);
        JSONObject data = objectData.getJSONObject("usertype");
        Iterator<String> keys = data.keys();
        for(int i=0;i<data.length() && keys.hasNext();i++) {
            System.out.println("Data size is: " + data.length());
                mPurchaseSchemeTypeModel = new PurchaseSchemeTypeModel();
                String keyValue = (String) keys.next();
                mPurchaseSchemeTypeModel.setPurchaseTypeId(keyValue);
                mPurchaseSchemeTypeModel.setPurchaseTypeName(data.getString(keyValue));
            purchaseSchemeTypeList.add(mPurchaseSchemeTypeModel);
        }

            //display schemetype into drop down
            mySpinner.setAdapter(new MyCustomAdapter(getActivity(), R.layout.spinner_row, purchaseSchemeTypeList));

            makePurchaseUserSchemeApiCalls();

    }

    //Method: get data for scheme name
    public void makePurchaseUserSchemeApiCalls() {
        showProgressDialog();
        //scheme name thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.purchase_user_schemename;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "app",
                            "scheme_name"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strUserName,
                            strMacAddress,
                            strOtpCode,
                            Constants.APP_VERSION,
                            purchaseSchemeTypeList.get(mySpinner.getSelectedItemPosition()).getPurchaseTypeId()
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    System.out.println("Add user scheme response1: " + response);
                    myHandlerScheme.obtainMessage(SUCCESS, response).sendToTarget();
                }
                catch (Exception ex) {
                    Log.e(TAG, "  Error  : "+ ex.getMessage() );

                    ex.printStackTrace();
                    myHandlerScheme.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();

    }

    // handle user scheme thread messages
    private Handler myHandlerScheme = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == SUCCESS) {
                dismissProgressDialog();
                parseSchemeSuccessResponse(msg.obj.toString());
            } else if (msg.what == ERROR) {
                dismissProgressDialog();
                displayErrorDialog(msg.obj.toString());
            }
        }
    };

    //parse scheme api
    public void parseSchemeSuccessResponse(String response) {

        Log.e(TAG, " AccountLedger Response : "+ response );

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String encrypted_response = jsonObject.getString("data");
                String message = jsonObject.getString("msg");
                Log.e(TAG, "Message : "+ message );
                Log.e(TAG, "Message : "+ message );

                String decrypted_response = Constants.decryptAPI((Activity) getActivity(), encrypted_response, strMacAddress);

                Log.e(TAG,"AccountLedger : " + "encrypted_response : " + encrypted_response);
                //String decrypted_response = decryptAPI(encrypted_response);
                //System.out.println("Add User Type: decrypted_response : " + decrypted_response);
                //Log.e(TAG,"AccountLedger : " + "decrypted_response : " + decrypted_response);
                parsing_scheme_response(decrypted_response);
            } else if (jsonObject.getString("status").equals("2") &&
                    jsonObject.getString("message").equalsIgnoreCase("Invalid Details")) {

            } else {

            }
        }
        catch (JSONException e) {
            Log.e(TAG,"Cashbook : " + "Error 4 : " + e.getMessage());
            Utility.toast(getActivity(), "No result found");
            e.printStackTrace();
        }

    }

    public void parsing_scheme_response(String response) throws JSONException {

        //add user scheme data
        purchaseSchemeNameModelsArrayList = new ArrayList<PurchaseSchemeNameModel>();
        JSONObject objectData = new JSONObject(response);
        JSONArray array = objectData.getJSONArray("scheme");
        for(int i=0;i<array.length();i++) {
            mPurchaseSchemeNameModel = new PurchaseSchemeNameModel();
            JSONObject obj = array.getJSONObject(i);
            mPurchaseSchemeNameModel.setId(obj.getString("id"));
            mPurchaseSchemeNameModel.setScheme_name(obj.getString("scheme_name"));
            mPurchaseSchemeNameModel.setScheme_description(obj.getString("scheme_description"));
            mPurchaseSchemeNameModel.setTax_id(obj.getString("tax_id"));
            mPurchaseSchemeNameModel.setTypes_of_level_id(obj.getString("types_of_level_id"));
            mPurchaseSchemeNameModel.setAmount(obj.getString("amount"));
            mPurchaseSchemeNameModel.setScheme_type(obj.getString("scheme_type"));
            mPurchaseSchemeNameModel.setMinimum_balance(obj.getString("minimum_balance"));
            mPurchaseSchemeNameModel.setMargin(obj.getString("margin"));
            mPurchaseSchemeNameModel.setUpfront(obj.getString("upfront"));
            mPurchaseSchemeNameModel.setTds(obj.getString("tds"));
            mPurchaseSchemeNameModel.setUnder_section(obj.getString("under_section"));
            mPurchaseSchemeNameModel.setStatus(obj.getString("status"));
            mPurchaseSchemeNameModel.setMode(obj.getString("mode"));
            mPurchaseSchemeNameModel.setCreated_by(obj.getString("created_by"));
            mPurchaseSchemeNameModel.setUpdated_by(obj.getString("updated_by"));
            mPurchaseSchemeNameModel.setIp_address(obj.getString("ip_address"));
            mPurchaseSchemeNameModel.setCreated_date(obj.getString("created_date"));
            mPurchaseSchemeNameModel.setUpdated_date(obj.getString("updated_date"));
            purchaseSchemeNameModelsArrayList.add(mPurchaseSchemeNameModel);
        }
        System.out.println("Purchase Scheme name size : " + purchaseSchemeNameModelsArrayList.size());
        spSchemeName.setAdapter(new MyPurchaseSchemeAdapter(getActivity(), R.layout.spinner_row, purchaseSchemeNameModelsArrayList));
        mEtAmount.setText(purchaseSchemeNameModelsArrayList.get(spSchemeName.getSelectedItemPosition()).getAmount());
        if(purchaseSchemeNameModelsArrayList.get(spSchemeName.getSelectedItemPosition()).getTds() == null || purchaseSchemeNameModelsArrayList.get(spSchemeName.getSelectedItemPosition()).getTds().equalsIgnoreCase("")) {
            mEtSchemeTds.setText("0");
        } else {
            mEtSchemeTds.setText(purchaseSchemeNameModelsArrayList.get(spSchemeName.getSelectedItemPosition()).getTds());
        }

        String margin = purchaseSchemeNameModelsArrayList.get(spSchemeName.getSelectedItemPosition()).getMargin();
        items = Arrays.asList(margin.split("\\s*,\\s*"));

        if(!TextUtils.isEmpty(mEtQtytobuy.getText())) {
            int qty = Integer.valueOf(mEtQtytobuy.getText().toString());
            if (qty <= 24) {
                discount_amt = items.get(0);
            } else if (qty >= 25 && qty <= 74) {
                discount_amt = items.get(1);
            } else if (qty >= 75 && qty <= 150) {
                discount_amt = items.get(2);
            } else if (qty >= 151) {
                discount_amt = items.get(3);
            }
            int amt = Integer.valueOf(mEtQtytobuy.getText().toString()) * Integer.valueOf(mEtAmount.getText().toString());
            if (!discount_amt.equals("0") || discount_amt != null || !discount_amt.equals("")) {
                amt = amt - (Integer.valueOf(discount_amt) * amt) / 100;
            }
            mEtNetAmount.setText(amt + "");
        } else {
            mEtNetAmount.setText("");
        }

        mEtQtytobuy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length()<=0) {
                    mEtNetAmount.setText("");
                } else {
                    int qty = Integer.valueOf(s.toString());
                    if (qty <= 24) {
                        discount_amt = items.get(0);
                    } else if (qty >= 25 && qty <= 74) {
                        discount_amt = items.get(1);
                    } else if (qty >= 75 && qty <= 150) {
                        discount_amt = items.get(2);
                    } else if (qty >= 151) {
                        discount_amt = items.get(3);
                    }
                    int amt = Integer.valueOf(mEtQtytobuy.getText().toString()) * Integer.valueOf(mEtAmount.getText().toString());
                    if(!discount_amt.equals("0") || discount_amt != null || !discount_amt.equals("")) {
                        amt = amt - (Integer.valueOf(discount_amt) * amt) / 100;
                    }
                    mEtNetAmount.setText(amt+"");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    // display error in dialog
    private void displayErrorDialog(String message) {
        /* [START] - 2017_05_01 - Close all alert dialog logic */

        try {
            alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Info!");
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
                    alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlack));
                }
            });
            alertDialog.show();
        }
        catch (Exception ex) {
           // Log.e(TAG,"Error in error dialog");
           // Log.e(TAG,"Error : " + ex.getMessage());
            ex.printStackTrace();
            try {
                Utility.toast(getActivity(), message);
            }
            catch (Exception e) {
                //Log.e(TAG,"Error in toast message");
               // Log.e(TAG,"ERROR : " + e.getMessage());
            }
        }
        // [END]
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
           
            ex.printStackTrace();
        }
    }

    public class MyCustomAdapter extends ArrayAdapter<ArrayList<PurchaseSchemeTypeModel>> {

        ArrayList<PurchaseSchemeTypeModel> addUserTypeModelArrayList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<PurchaseSchemeTypeModel> objects) {
            super(context, textViewResourceId, Collections.singletonList(objects));
            this.addUserTypeModelArrayList = objects;
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        @Override
        public int getCount() {
            return addUserTypeModelArrayList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            //return super.getView(position, convertView, parent);

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_row, parent, false);
            TextView label = (TextView) row.findViewById(R.id.weekofday);
            //label.setText(DayOfWeek[position]);
            label.setText(addUserTypeModelArrayList.get(position).getPurchaseTypeName());

            return row;
        }
    }

    public class MyPurchaseSchemeAdapter extends ArrayAdapter<ArrayList<PurchaseSchemeNameModel>> {

        ArrayList<PurchaseSchemeNameModel> addUserTypeModelArrayList;

        public MyPurchaseSchemeAdapter(Context context, int textViewResourceId,
                                       ArrayList<PurchaseSchemeNameModel> objects) {
            super(context, textViewResourceId, Collections.singletonList(objects));
            this.addUserTypeModelArrayList = objects;
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        @Override
        public int getCount() {
            return addUserTypeModelArrayList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            //return super.getView(position, convertView, parent);

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_row, parent, false);
            TextView label = (TextView) row.findViewById(R.id.weekofday);
            //label.setText(DayOfWeek[position]);
            label.setText(addUserTypeModelArrayList.get(position).getScheme_name());

            return row;
        }
    }

}
