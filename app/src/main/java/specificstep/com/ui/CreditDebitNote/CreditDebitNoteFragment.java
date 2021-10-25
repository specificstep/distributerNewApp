package specificstep.com.ui.CreditDebitNote;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import specificstep.com.Database.DatabaseHelper;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.GlobalClasses.MCrypt;
import specificstep.com.GlobalClasses.MyLocation;
import specificstep.com.GlobalClasses.TransparentProgressDialog;
import specificstep.com.GlobalClasses.URL;
import specificstep.com.Models.CashSummaryUserTypeModel;
import specificstep.com.Models.CreditDebitModel;
import specificstep.com.Models.Default;
import specificstep.com.Models.User;
import specificstep.com.Models.UserList;
import specificstep.com.R;
import specificstep.com.ui.base.BaseFragment;
import specificstep.com.utility.InternetUtil;
import specificstep.com.utility.Utility;

import static android.content.Context.LOCATION_SERVICE;


public class CreditDebitNoteFragment extends BaseFragment {

    View view;
    private final int SUCCESSTYPE = 4, ERROR = 2, SUCCESSWALLET = 3, SUCCESSNOTE = 1;
    private TransparentProgressDialog transparentProgressDialog;
    String TAG = "CreditDebitNoteFragment :: ";
    private String strMacAddress, strUserName, strOtpCode, strRegistrationDateTime;
    private ArrayList<User> userArrayList;
    DatabaseHelper databaseHelper;
    private AlertDialog alertDialog;
    List<CashSummaryUserTypeModel> cashSummaryTypeList;
    CashSummaryUserTypeModel userTypeModel;
    public static RadioGroup ll;
    Spinner userListSpinner;
    private ArrayList<UserList> userListArrayList;
    List<UserList> dataSpinner;
    String[] typeArray = {"Credit","Debit"};
    ArrayAdapter<String> adapterType, adapterWallet;
    List<CreditDebitModel> creditDebitModelList;
    CreditDebitModel creditDebitModel;
    Spinner spnType, spnWallet;
    EditText edtAmount, edtRemarks;
    Button submit;
    List<Integer> userListId;

    private LocationCallback locationCallback;

    MyLocation myLocation = new MyLocation();
    int MY_PERMISSION_LOCATION = 1;

    public CreditDebitNoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_credit_debit_note, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initialize();
        makeUserRoleCall();
        makeWalletCall();
        myLocation.getLocation(getActivity(), locationResult);
        marshmallowGPSPremissionCheck();

        adapterType = new ArrayAdapter<String>(getActivity(),
                R.layout.item_spinner, typeArray);
        spnType.setAdapter(adapterType);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(valid()) {
                    makeCreditDebitNoteCall();
                }
            }
        });

        return view;
    }

    public void initialize() {

        transparentProgressDialog = new TransparentProgressDialog(getActivity(), R.drawable.fotterloading);
        databaseHelper = new DatabaseHelper(getActivity());
        userArrayList = databaseHelper.getUserDetail();
        alertDialog = new AlertDialog.Builder(getActivity()).create();
        userListSpinner = (Spinner) view.findViewById(R.id.userList);
        // Store user information in variables
        strMacAddress = userArrayList.get(0).getDevice_id();
        strUserName = userArrayList.get(0).getUser_name();
        strOtpCode = userArrayList.get(0).getOtp_code();
        strRegistrationDateTime = userArrayList.get(0).getReg_date();

        edtAmount = (EditText) view.findViewById(R.id.edtCrDrAmount);
        edtRemarks = (EditText) view.findViewById(R.id.edtCrDrRemarks);
        spnType = (Spinner) view.findViewById(R.id.spnCrDrType);
        spnWallet = (Spinner) view.findViewById(R.id.spnCrDrWallet);
        submit = (Button) view.findViewById(R.id.btnCrDrSubmit);

    }

    private void marshmallowGPSPremissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && getActivity().checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && getActivity().checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_LOCATION);
        } else {
            //   gps functions.

        }
    }

    public MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {

        @Override
        public void gotLocation(Location location) {
            // TODO Auto-generated method stub
            try {
                if(location != null) {
                    double Longitude = location.getLongitude();
                    double Latitude = location.getLatitude();
                    Constants.Lati = Latitude + "";
                    Constants.Long = Longitude + "";
                    System.out.println("Got Location : Longitude: " + Longitude
                            + " Latitude: " + Latitude);
                } else {
                    Location l = getLastKnownLocation();
                    double Longitude = l.getLongitude();
                    double Latitude = l.getLatitude();
                    Constants.Lati = Latitude + "";
                    Constants.Long = Longitude + "";
                    System.out.println("Got Location : Longitude: " + Longitude
                            + " Latitude: " + Latitude);
                }

            } catch (Exception e) {
                System.out.println("Location permission denied. " + e.toString());
            }
        }
    };

    private Location getLastKnownLocation() {
        Location l=null;
        LocationManager mLocationManager = (LocationManager)getActivity().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                l = mLocationManager.getLastKnownLocation(provider);
            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        try {
            if (requestCode == MY_PERMISSION_LOCATION
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("per grant");
                //  gps functionality
            } else {
                System.out.println("per not grant");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public boolean valid() {

        boolean flag;
        if(TextUtils.isEmpty(edtAmount.getText().toString())) {
            Toast.makeText(getActivity(),"Enter amount", Toast.LENGTH_LONG).show();
            return false;
        } else if(TextUtils.isEmpty(edtRemarks.getText().toString())) {
            Toast.makeText(getActivity(),"Enter remarks", Toast.LENGTH_LONG).show();
            return false;
        } /*else if(Constants.Lati == null || Constants.Long.equals("")) {
            Toast.makeText(getActivity(),"Please Allow Location Permission.",Toast.LENGTH_LONG).show();
            return false;
        } */else {
            return true;
        }

    }

    public void makeWalletCall() {
        showProgressDialog();
        // create new threadc
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.walletType;
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
                    myHandler.obtainMessage(SUCCESSWALLET, response).sendToTarget();
                }
                catch (Exception ex) {
                    Log.e(TAG, "  Error  : "+ ex.getMessage() );

                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();

    }

    public void makeUserRoleCall() {
        showProgressDialog();
        // create new threadc
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.roleType;
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
                    myHandler.obtainMessage(SUCCESSTYPE, response).sendToTarget();
                }
                catch (Exception ex) {
                    Log.e(TAG, "  Error  : "+ ex.getMessage() );

                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();

    }

    public void makeCreditDebitNoteCall() {
        try {
            showProgressDialog();
            int selectedUserTypePosition = userListSpinner.getSelectedItemPosition();
            List<UserList> data = new ArrayList<UserList>();
            for (UserList userList : userListArrayList) {
                if (userList.getParentUserId().equals(userArrayList.get(0).getUser_id())) {
                    data.add(userList);
                }
            }
            UserList userList = data.get(selectedUserTypePosition);
            final String selectedUserId = userList.getUser_id();

        // create new threadc
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.crdrNote;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "app",
                            "userid",
                            "type",
                            "remarks",
                            "wallet_id",
                            "amount",
                            "lati",
                            "long"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strUserName,
                            strMacAddress,
                            strOtpCode,
                            Constants.APP_VERSION,
                            selectedUserId,
                            String.valueOf(spnType.getSelectedItemPosition()),
                            edtRemarks.getText().toString(),
                            creditDebitModelList.get(spnWallet.getSelectedItemPosition()).getWallet_type(),
                            edtAmount.getText().toString(),
                            Constants.Lati,
                            Constants.Long
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
                    myHandler.obtainMessage(SUCCESSNOTE, response).sendToTarget();
                }
                catch (Exception ex) {
                    Log.e(TAG, "  Error  : "+ ex.getMessage() );

                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
        } catch (Exception e) {
            System.out.println("crdr note error: " + e.toString());
        }
    }

    // handle thread messages
    private Handler myHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == ERROR) {
                dismissProgressDialog();
                displayErrorDialog(msg.obj.toString());
            } else if (msg.what == SUCCESSTYPE) {
                dismissProgressDialog();
                parseSuccessTypeResponse(msg.obj.toString());
            } else if (msg.what == SUCCESSWALLET) {
                dismissProgressDialog();
                parseSuccessWalletResponse(msg.obj.toString());
            } else if (msg.what == SUCCESSNOTE) {
                dismissProgressDialog();
                parseSuccessNoteResponse(msg.obj.toString());
            }
        }
    };
    // [END]

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void parseSuccessNoteResponse(String response) {

        Log.e(TAG, " Cash Summary Type Response : "+ response );

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String message = jsonObject.getString("msg");
                //Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
                showDialog("",message+"");
                edtAmount.setText("");
                edtRemarks.setText("");
            } else {
                showDialog("Alert!",jsonObject.getString("msg")+"");
            }
        }
        catch (JSONException e) {
            Log.e(TAG,"Cashbook : " + "Error 4 : " + e.getMessage());
            Utility.toast(getActivity(), "No result found");
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void parseSuccessWalletResponse(String response) {

        Log.e(TAG, " Cash Summary Type Response : "+ response );

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String encrypted_response = jsonObject.getString("data");
                String message = jsonObject.getString("msg");
                Log.e(TAG, "Message : "+ message );
                Log.e(TAG, "Message : "+ message );

                Log.e(TAG,"AccountLedger : " + "encrypted_response : " + encrypted_response);
                String decrypted_response = Constants.decryptAPI((Activity) getActivity(), encrypted_response, strMacAddress);

                Log.e(TAG,"AccountLedger : " + "decrypted_response : " + decrypted_response);

                JSONArray array = new JSONArray(decrypted_response);
                creditDebitModelList = new ArrayList<CreditDebitModel>();
                List<String> wallet = new ArrayList<String>();
                for(int i=0;i<array.length();i++) {
                    JSONObject object = array.getJSONObject(i);
                    creditDebitModel = new CreditDebitModel();
                    creditDebitModel.setWallet_type(object.getString("wallet_type"));
                    creditDebitModel.setWallet_name(object.getString("wallet_name"));
                    creditDebitModel.setBalance(object.getString("balance"));
                    creditDebitModelList.add(creditDebitModel);
                    wallet.add(object.getString("wallet_name"));
                }

                adapterWallet = new ArrayAdapter<String>(getActivity(),
                        R.layout.item_spinner, wallet);
                spnWallet.setAdapter(adapterWallet);

            } else {
                showDialog("Alert!",jsonObject.getString("msg")+"");
            }
        }
        catch (JSONException e) {
            Log.e(TAG,"Cashbook : " + "Error 4 : " + e.getMessage());
            Utility.toast(getActivity(), "No result found");
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void parseSuccessTypeResponse(String response) {

        Log.e(TAG, " Cash Summary Type Response : "+ response );

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String encrypted_response = jsonObject.getString("data");
                String message = jsonObject.getString("msg");
                Log.e(TAG, "Message : "+ message );
                Log.e(TAG, "Message : "+ message );

                Log.e(TAG,"AccountLedger : " + "encrypted_response : " + encrypted_response);
                String decrypted_response = Constants.decryptAPI((Activity) getActivity(), encrypted_response, strMacAddress);

                Log.e(TAG,"AccountLedger : " + "decrypted_response : " + decrypted_response);

                JSONArray array = new JSONArray(decrypted_response);
                cashSummaryTypeList = new ArrayList<CashSummaryUserTypeModel>();
                for(int i=0;i<array.length();i++) {
                    JSONObject object = array.getJSONObject(i);
                    userTypeModel = new CashSummaryUserTypeModel();
                    userTypeModel.setRid(object.getString("rid"));
                    userTypeModel.setRole_name(object.getString("role_name"));
                    cashSummaryTypeList.add(userTypeModel);
                }

                if(cashSummaryTypeList.size()>0) {
                    addRadioButtons(cashSummaryTypeList.size());
                }

            } else {
                showDialog("Alert!",jsonObject.getString("msg")+"");
            }
        }
        catch (JSONException e) {
            Log.e(TAG,"Cashbook : " + "Error 4 : " + e.getMessage());
            Utility.toast(getActivity(), "No result found");
            e.printStackTrace();
        }

    }

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void addRadioButtons(int number) {
        userListId = new ArrayList<Integer>();
        for (int row = 0; row < 1; row++) {
            ll = new RadioGroup(getActivity());
            ll.setOrientation(LinearLayout.HORIZONTAL);
            for (int i = 0; i < number; i++) {
                if(Integer.parseInt(cashSummaryTypeList.get(i).getRid()) == 3 ||
                        Integer.parseInt(cashSummaryTypeList.get(i).getRid()) == 4 ||
                        Integer.parseInt(cashSummaryTypeList.get(i).getRid()) == 7) {
                    RadioButton rdbtn = new RadioButton(getActivity());
                    CheckBox checkBox = new CheckBox(getActivity());
                    rdbtn.setTextSize(11);
                    rdbtn.setButtonTintList(ContextCompat.getColorStateList(getActivity(), R.color.white));
                    rdbtn.setHighlightColor(getResources().getColor(R.color.white));
                    rdbtn.setMaxLines(2);
                    rdbtn.setDrawingCacheBackgroundColor(getResources().getColor(R.color.white));
                    rdbtn.setTextColor(getResources().getColor(R.color.white));
                    rdbtn.setOnClickListener(mThisButtonListener);

                    rdbtn.setId(Integer.parseInt(cashSummaryTypeList.get(i).getRid()));

                    userListArrayList = databaseHelper.getUserListDetailsByType(cashSummaryTypeList.get(i).getRole_name());

                    userArrayList = databaseHelper.getUserDetail();
                    List<String> data = new ArrayList<>();
                    dataSpinner = new ArrayList<UserList>();
                    //data.add(getActivity().getString(R.string.all));
                    for (UserList userList : userListArrayList) {
                        //System.out.println("Login UserId: " + userArrayList.get(0).getUser_id());
                        //System.out.println("Parent UserId: " + userList.getParentUserId());
                        if(userList.getParentUserId().equals(userArrayList.get(0).getUser_id())) {
                            data.add(userList.getUser_name() + " - " + userList.getPhone_no());
                        }
                    }


                    if (data.size() > 0) {
                        rdbtn.setVisibility(View.VISIBLE);
                        ll.addView(rdbtn);
                        userListId.add(Integer.valueOf(cashSummaryTypeList.get(i).getRid()));
                    } else {
                        rdbtn.setVisibility(View.GONE);
                    }

                    /*if(Integer.parseInt(cashSummaryTypeList.get(i).getRid()) == 3) {
                        rdbtn.setChecked(true);
                        onResellerUserRadioButtonChecked(cashSummaryTypeList.get(i).getRole_name());
                    }*/
                    rdbtn.setText(cashSummaryTypeList.get(i).getRole_name());
                }
            }
            ((ViewGroup) view.findViewById(R.id.radiogroupType)).addView(ll);
            if(userListId != null && userListId.size() > 0) {
                ll.check(userListId.get(0));
                int selectedId = ll.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton radioButton = (RadioButton) view.findViewById(selectedId);

                if (selectedId == 3) {
                    onResellerUserRadioButtonChecked(radioButton.getText().toString());
                } else if (selectedId == 4) {
                    onRetailerUserRadioButtonChecked(radioButton.getText().toString());
                } else if (selectedId == 7) {
                    onDealerUserRadioButtonChecked(radioButton.getText().toString());
                }
            }

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
            Log.e(TAG,"Error in show progress");
            Log.e(TAG,"Error : " + ex.getMessage());
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
            Log.e(TAG,"Error in dismiss progress");
            Log.e(TAG,"Error : " + ex.getMessage());
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
            Log.e(TAG,"Error in error dialog");
            Log.e(TAG,"Error : " + ex.getMessage());
            ex.printStackTrace();
            try {
                Utility.toast(getActivity(), message);
            }
            catch (Exception e) {
                Log.e(TAG,"Error in toast message");
                Log.e(TAG,"ERROR : " + e.getMessage());
            }
        }
        // [END]
    }

    private View.OnClickListener mThisButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            int s = ((RadioButton) v).getId();
            if(s == 2) {
                onSelfRadioButtonChecked();
            } else if(s == 3) {
                onResellerUserRadioButtonChecked(((RadioButton) v).getText().toString());
                System.out.println(((RadioButton) v).getText().toString());
            } else if(s == 4) {
                onRetailerUserRadioButtonChecked(((RadioButton) v).getText().toString());
                System.out.println(((RadioButton) v).getText().toString());
            } else if(s == 7) {
                onDealerUserRadioButtonChecked(((RadioButton) v).getText().toString());
                System.out.println(((RadioButton) v).getText().toString());
            }
        }
    };

    public void onSelfRadioButtonChecked() {
        userListArrayList = databaseHelper.getUserListDetailsById();
        System.out.println("User Array: " + userListArrayList);
        hideUserListSpinner();
        prepareUserListSpinnerData(userListArrayList);
    }

    private void prepareUserListSpinnerData(List<UserList> userListArrayList) {
        userArrayList = databaseHelper.getUserDetail();
        List<String> data = new ArrayList<>();
        dataSpinner = new ArrayList<UserList>();
        //data.add(getActivity().getString(R.string.all));
        for (UserList userList : userListArrayList) {
            //System.out.println("Login UserId: " + userArrayList.get(0).getUser_id());
            //System.out.println("Parent UserId: " + userList.getParentUserId());
            if(userList.getParentUserId().equals(userArrayList.get(0).getUser_id())) {
                data.add(userList.getUser_name() + " - " + userList.getPhone_no());
                dataSpinner.add(userList);
            }
        }
        setUserSpinnerAdapter(data);
    }

    public void setUserSpinnerAdapter(List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner, data);
        userListSpinner.setAdapter(adapter);
    }

    public void hideUserListSpinner() {
        userListSpinner.setVisibility(View.GONE);
    }

    public void onResellerUserRadioButtonChecked(String userType) {
        userListArrayList = databaseHelper.getUserListDetailsByType(userType);
        showUserListSpinner();
        prepareUserListSpinnerData(userListArrayList);
    }

    public void onDealerUserRadioButtonChecked(String userType) {
        userListArrayList = databaseHelper.getUserListDetailsByType(userType);
        showUserListSpinner();
        prepareUserListSpinnerData(userListArrayList);
    }

    public void onRetailerUserRadioButtonChecked(String userType) {
        userListArrayList = databaseHelper.getUserListDetailsByType(userType);
        showUserListSpinner();
        prepareUserListSpinnerData(userListArrayList);
    }

    public void showUserListSpinner() {
        userListSpinner.setVisibility(View.VISIBLE);
    }

}
