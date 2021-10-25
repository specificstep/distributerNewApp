package specificstep.com.ui.AcLedger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import specificstep.com.Adapters.AccountLedgerAdapter;
import specificstep.com.Database.DatabaseHelper;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.GlobalClasses.MCrypt;
import specificstep.com.GlobalClasses.TransparentProgressDialog;
import specificstep.com.GlobalClasses.URL;
import specificstep.com.Models.AccountLedgerModel;
import specificstep.com.Models.CashSummaryUserTypeModel;
import specificstep.com.Models.Default;
import specificstep.com.Models.User;
import specificstep.com.Models.UserList;
import specificstep.com.R;
import specificstep.com.data.source.local.Pref;
import specificstep.com.data.utils.DateTime;
import specificstep.com.ui.base.BaseFragment;
import specificstep.com.ui.home.HomeActivity;
import specificstep.com.ui.home.HomeContract;
import specificstep.com.utility.InternetUtil;
import specificstep.com.utility.Utility;

public class AcLedgerFragment extends BaseFragment implements View.OnClickListener{


    String TAG = "AcLedgerFragment :: ";
    private final int SUCCESS = 1, ERROR = 2, SUCCESSBALANCE = 3, SUCCESSTYPE = 4;
    /* [START] - All View objects */
    // View class object for display fragment view
    private View view;
    // Load more data view
    private View footerView;
    private View footerViewNoMoreData;
    // [END]

    /* [START] - Other class objects */
    private Context context;
    // Custom log message class
    // private LogMessage log;
    private SharedPreferences sharedPreferences;
    // All static variables class
    private Constants constants;
    // Database class
    private DatabaseHelper databaseHelper;
    private AccountLedgerAdapter acLedgerAdapter;
    private TransparentProgressDialog transparentProgressDialog;
    // private Calendar myCalendar;
    private Calendar fromCalendar, toCalendar;
    private SimpleDateFormat simpleDateFormat;
    // [END]

    /* [START] - Controls objects */
    private EditText edtFromDate, edtToDate;
    private EditText edtFromDateTest, edtToDateTest;
    private Button btnSearch;
    //private TextView txtNoMoreData;
    ImageView imgNoData;
    private ListView lstCashbookSearch;
    private LinearLayout ll_recycler_view;
    private DatePicker dpResult;
    private TextView txtOpeningBalance, txtClosingBalance;
    // [END]

    /* [START] - Variables */
    private static boolean loadMoreFlage = false;
    boolean FLAG_INVALID_DETAIL = false;
    // Counter for 3 times invalid details
    private int count = 0;
    // use for get cashbook data from and to limit
    private int start = 0, end = 10;
    // use for set date time picker
    private int year, month, day;
    private String strMacAddress, strUserName, strOtpCode, strRegistrationDateTime;
    private ArrayList<User> userArrayList;
    private ArrayList<AccountLedgerModel> acLedgerModels;
    private ArrayList<AccountLedgerModel> beforeAcLedgerModels;
    private boolean isAlertOkClicked = false ;
    private AlertDialog alertDialog;
    String balance;
    public static RadioGroup ll;

    List<CashSummaryUserTypeModel> cashSummaryTypeList;
    CashSummaryUserTypeModel userTypeModel;
    private ArrayList<UserList> userListArrayList,userListArrayList1;
    Spinner userListSpinner;
    String selectedUserId;
    int SelectedUserPos;
    List<UserList> dataSpinner;
    Pref pref;

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

    // [END]

  /*  private HomeActivity mainActivity() {
        return ((HomeActivity) getActivity());
    }
*/
    private Context getContextInstance() {
        if (context == null) {
            context = AcLedgerFragment.this.getActivity();
            return context;
        } else {
            return context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account_ledger, null);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        context = AcLedgerFragment.this.getActivity();
        alertDialog = new AlertDialog.Builder(getContextInstance()).create();

        initControls();
        setListener();
        makeUserRoleCall();
        setCurrentDateOnView();
        formDatePicker();
        toDatePicker();
        makeAccountLedgerBalance();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(homeDelegate != null) {
            homeDelegate.setToolBarTitle(getString(R.string.ac_ledger));
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initControls() {
        /* [START] - Initialise class objects */

        // log = new LogMessage(CashbookFragment.class.getSimpleName());
        constants = new Constants();
        databaseHelper = new DatabaseHelper(getContextInstance());
        sharedPreferences = getActivity().getSharedPreferences(constants.SHAREEDPREFERENCE, Context.MODE_PRIVATE);
        transparentProgressDialog = new TransparentProgressDialog(getContextInstance(), R.drawable.fotterloading);
        acLedgerModels = new ArrayList<AccountLedgerModel>();
        beforeAcLedgerModels = new ArrayList<AccountLedgerModel>();
        fromCalendar = Calendar.getInstance();
        toCalendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        // [END]

        /* [START] - get user data from database and store into string variables */
        userArrayList = databaseHelper.getUserDetail();
        // Store user information in variables
        strMacAddress = userArrayList.get(0).getDevice_id();
        strUserName = userArrayList.get(0).getUser_name();
        strOtpCode = userArrayList.get(0).getOtp_code();
        strRegistrationDateTime = userArrayList.get(0).getReg_date();
        // [END]

        /* [START] - Initialise control objects */
        // All TextView
        //txtNoMoreData = (TextView) view.findViewById(R.id.txt_NoMoreData);
        imgNoData = (ImageView) view.findViewById(R.id.imgAccountLedgerNoData);
        // All Button
        btnSearch = (Button) view.findViewById(R.id.btn_search_CashBook);
        // All EditText
        edtFromDate = (EditText) view.findViewById(R.id.from_date_CashBook);
        edtFromDateTest = (EditText) view.findViewById(R.id.from_date_CashBook_Test);
        edtToDate = (EditText) view.findViewById(R.id.to_date_CashBook);
        edtToDateTest = (EditText) view.findViewById(R.id.to_date_CashBook_Test);
        // ListView
        lstCashbookSearch = (ListView) view.findViewById(R.id.lv_trans_search_CashBook);
        // LinearLayout
        ll_recycler_view = (LinearLayout) view.findViewById(R.id.ll_recycler_view_CashBook);
        // Load more data view
        footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.load_more_items, null);
        footerViewNoMoreData = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_no_moredata, null);
        txtOpeningBalance = (TextView) view.findViewById(R.id.txtAccountOpening);
        txtClosingBalance = (TextView) view.findViewById(R.id.txtAccountClosing);
        userListSpinner = (Spinner) view.findViewById(R.id.userList);
        // [END]
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void addRadioButtons(int number) {
        for (int row = 0; row < 1; row++) {
            ll = new RadioGroup(getActivity());
            ll.setOrientation(LinearLayout.HORIZONTAL);
            for (int i = 0; i < number; i++) {
                if(Integer.parseInt(cashSummaryTypeList.get(i).getRid()) == 2 ||
                        Integer.parseInt(cashSummaryTypeList.get(i).getRid()) == 3 ||
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

                    if(Integer.parseInt(cashSummaryTypeList.get(i).getRid()) != 2) {

                        List<UserList> userListArrayList = databaseHelper.getUserListDetailsByType(cashSummaryTypeList.get(i).getRole_name());
                        if (userListArrayList.size() > 0) {
                            rdbtn.setVisibility(View.VISIBLE);
                        } else {
                            rdbtn.setVisibility(View.GONE);
                        }
                    }

                    if(Integer.parseInt(cashSummaryTypeList.get(i).getRid()) == 2) {
                        rdbtn.setText("Self");
                        rdbtn.setChecked(true);
                        onSelfRadioButtonChecked();
                    } else {
                        rdbtn.setText(cashSummaryTypeList.get(i).getRole_name());
                    }

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


                    if(Integer.parseInt(cashSummaryTypeList.get(i).getRid()) != 2) {
                        if (data.size() > 0) {
                            rdbtn.setVisibility(View.VISIBLE);
                            ll.addView(rdbtn);
                        } else {
                            rdbtn.setVisibility(View.GONE);
                        }
                    } else {
                        ll.addView(rdbtn);
                    }
                }
            }
            ((ViewGroup) view.findViewById(R.id.radiogroupType)).addView(ll);

        }
    }

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

    public void onResellerUserRadioButtonChecked(String userType) {
        List<UserList> userListArrayList = databaseHelper.getUserListDetailsByType(userType);
        showUserListSpinner();
        prepareUserListSpinnerData(userListArrayList);
    }

    public void onDealerUserRadioButtonChecked(String userType) {
        List<UserList> userListArrayList = databaseHelper.getUserListDetailsByType(userType);
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

    public String getSelectedUserType() {
        return userListSpinner.getSelectedItem() != null ? userListSpinner.getSelectedItem().toString() : "";
    }

    public int getSelectedUserTypePosition() {
        return userListSpinner.getSelectedItemPosition();
    }

    public boolean isResellerSelected() {
        //return userTypeRadioGroup.getCheckedRadioButtonId() == R.id.rbt_Reseller;
        int id = ll.getCheckedRadioButtonId();
        if(id == 3) {
            return true;
        }
        return false;
    }

    public boolean isDealerSelected() {
        int id = ll.getCheckedRadioButtonId();
        if(id == 7) {
            return true;
        }
        return false;
    }

    public boolean isRetailerSelected() {
        int id = ll.getCheckedRadioButtonId();
        if(id == 4) {
            return true;
        }
        return false;
    }

    // Load current cashbook
    private void loadDefaultAccountLedger() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date from_date = sdf.parse(edtFromDate.getText().toString());
            Date to_date = sdf.parse(edtToDate.getText().toString());

            if (from_date.getMonth() == to_date.getMonth()) {
                acLedgerModels.clear();
                start = 0;
                end = 10;
                showProgressDialog();
                imgNoData.setVisibility(View.GONE);

                if(isRetailerSelected() || isResellerSelected() || isDealerSelected()) {
                    System.out.println("Data Spinner: " + dataSpinner);
                    SelectedUserPos = getSelectedUserTypePosition();
                    selectedUserId = dataSpinner.get(SelectedUserPos).getUser_id();
                    System.out.println("Spinner Pos: " + SelectedUserPos + " Spinner USerId: " + selectedUserId);
                } else {
                    selectedUserId = "";
                    System.out.println("SelectedUserId: " + selectedUserId);
                }

                makeNativeAccountLedger(); //1

                //@kns.p
                acLedgerAdapter = new AccountLedgerAdapter(getContextInstance(), acLedgerModels);
                lstCashbookSearch.setAdapter(acLedgerAdapter);

                setBalance();


            } else {
                Utility.toast(getContextInstance(), "Please select dates of same month.");
            }
        }
        catch (Exception ex) {
            Log.e(TAG,"Cashbook : " + "Error 3 : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void setListener() {
        // On click listener
        btnSearch.setOnClickListener(this);
        edtFromDateTest.setOnClickListener(this);
        edtToDateTest.setOnClickListener(this);
    }

    public void setBalance() {

        try {
            if(acLedgerModels.size()>0) {
                Double openBal = 0.0;
                NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
                Number numberBalance = null, numberAmount = null, numberEndBal = null;
                numberBalance = format.parse(acLedgerModels.get(0).balance);
                numberAmount = format.parse(acLedgerModels.get(0).amount);
                if(acLedgerModels.get(0).cr_dr.equals("Credit")) {
                    openBal = numberBalance.doubleValue() - numberAmount.doubleValue();
                } else {
                    openBal = numberBalance.doubleValue() + numberAmount.doubleValue();
                }
                BigDecimal cached = new BigDecimal(openBal+"");
                txtOpeningBalance.setText(getResources().getString(R.string.currency_format, Utility.formatBigDecimalToString(cached)));
                numberEndBal = format.parse(acLedgerModels.get(acLedgerModels.size()-1).balance);
                BigDecimal bigDecimal = new BigDecimal((numberEndBal.doubleValue())+"");
                txtClosingBalance.setText(getResources().getString(R.string.currency_format, Utility.formatBigDecimalToString(bigDecimal)));
            } else {
                System.out.println("CachedBalance: " + balance);
                BigDecimal cached = new BigDecimal(balance);
                txtOpeningBalance.setText(getResources().getString(R.string.currency_format, Utility.formatBigDecimalToString(cached)));
                txtClosingBalance.setText(getResources().getString(R.string.currency_format, Utility.formatBigDecimalToString(cached)));
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }


    /**
     * Set from date and it's click listener
     */
    private void formDatePicker() {
        final DatePickerDialog.OnDateSetListener fromDatePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                try {
                    fromCalendar.set(Calendar.YEAR, year);
                    fromCalendar.set(Calendar.MONTH, monthOfYear);
                    fromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateFromLabel(edtFromDate);

                    /* [START] - 2017_04_18 - set to date selection validation and update to date label */
                    try {
                        // set default year in to_date_picker as per from_date_picker
                        toCalendar.set(Calendar.YEAR, year);
                        // set default month in to_date_picker as per from_date_picker
                        toCalendar.set(Calendar.MONTH, monthOfYear);
                        // get last date from from_date_picker selected month
                        int lastDayOfMonth = fromCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        // get current date for validate to_date_picker max date
                        String currentFullDate = DateTime.getDate();
                        // get current date from full date
                        String currentDate = DateTime.getDayFromFullDate(currentFullDate);
                        // get current month from full date
                        String currentMonth = DateTime.getMonthFromFullDate(currentFullDate);
                        // Check current month and from_date_picker month are same or not.
                        if (Integer.parseInt(currentMonth) == monthOfYear + 1) {
                            // if current month and from_date_picker month are same, check current date and last day of month
                            if (Integer.parseInt(currentDate) < lastDayOfMonth) {
                                // if current date is less then last day of month then set current date in to calender
                                toCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(currentDate));
                            } else {
                                // else set last day of month in to calender
                                toCalendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
                            }
                        } else {
                            // else set last day of month in to calender
                            toCalendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
                        }
                        updateToLabel(edtToDate);
                    }
                    catch (Exception ex) {
                        Log.e(TAG, "Error  : "+ ex.getMessage() );

                        ex.printStackTrace();
                    }
                    // [END]
                }
                catch (Exception ex) {
                    Log.e(TAG, " Cashbook Error 1 : "+ ex.getMessage() );

                    ex.printStackTrace();
                }
            }
        };

        edtFromDate.setText(simpleDateFormat.format(fromCalendar.getTime()));
        edtFromDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                try {
                    long timeInMilliseconds = new Date().getTime();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                            fromDatePicker,
                            fromCalendar.get(Calendar.YEAR),
                            fromCalendar.get(Calendar.MONTH),
                            fromCalendar.get(Calendar.DAY_OF_MONTH));
                    // datePickerDialog.getDatePicker().setMaxDate(max_TimeInMilliseconds);
                    datePickerDialog.getDatePicker().setMaxDate(timeInMilliseconds);
                    datePickerDialog.show();
                }
                catch (Exception e) {
                    Log.e(TAG,"Cashbook : " + "Error 1 : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Set to date and it's click listener
     */
    private void toDatePicker() {
        final DatePickerDialog.OnDateSetListener toDatePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                toCalendar.set(Calendar.YEAR, year);
                toCalendar.set(Calendar.MONTH, monthOfYear);
                toCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateToLabel(edtToDate);
            }
        };

        edtToDate.setText(simpleDateFormat.format(toCalendar.getTime()));
        edtToDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                try {
                    Calendar calendar = Calendar.getInstance();
                    Date mDate = sdf.parse(edtFromDate.getText().toString());
                    long min_TimeInMilliseconds = mDate.getTime();
                    long max_Time = calendar.getTimeInMillis();
                    long max_TimeInMilliseconds = 0;

                    /* [START] - 2017_04_19 - set to date selection validation and update to date label */
                    try {
                        // set default year in to_date_picker as per from_date_picker
                        calendar.set(Calendar.YEAR, year);
                        // set default month in to_date_picker as per from_date_picker
                        calendar.set(Calendar.MONTH, mDate.getMonth());
                        int monthOfYear = mDate.getMonth();
                        // get last date from from_date_picker selected month
                        int lastDayOfMonth = fromCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        // get current date for validate to_date_picker max date
                        String currentFullDate = DateTime.getDate();
                        // get current date from full date
                        String currentDate = DateTime.getDayFromFullDate(currentFullDate);
                        // get current month from full date
                        String currentMonth = DateTime.getMonthFromFullDate(currentFullDate);
                        // Check current month and from_date_picker month are same or not.
                        if (Integer.parseInt(currentMonth) == monthOfYear + 1) {
                            // if current month and from_date_picker month are same, check current date and last day of month
                            if (Integer.parseInt(currentDate) < lastDayOfMonth) {
                                // if current date is less then last day of month then set current date in to calender
                                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(currentDate));
                            } else {
                                // else set last day of month in to calender
                                calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
                            }
                        } else {
                            // else set last day of month in to calender
                            calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
                        }
                        max_TimeInMilliseconds = calendar.getTimeInMillis();
                    }
                    catch (Exception ex) {
                        Log.e(TAG, "  Error  : "+ ex.getMessage() );

                        ex.printStackTrace();
                        max_TimeInMilliseconds = max_Time;
                    }
                    // [END]

                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                            toDatePicker,
                            toCalendar.get(Calendar.YEAR),
                            toCalendar.get(Calendar.MONTH),
                            toCalendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.getDatePicker().setMinDate(min_TimeInMilliseconds);
                    datePickerDialog.getDatePicker().setMaxDate(max_TimeInMilliseconds);
                    // datePickerDialog.updateDate(2017, 03, 03);
                    datePickerDialog.show();
                }
                catch (Exception e) {
                    Log.e(TAG, " Cashbook Error 12 : "+ e.getMessage() );

                    e.printStackTrace();
                }
            }
        });
    }

    private void updateFromLabel(EditText editText) {
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(sdf.format(fromCalendar.getTime()));
    }

    private void updateToLabel(EditText editText) {
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(sdf.format(toCalendar.getTime()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == edtFromDateTest) {

        } else if (v == edtToDateTest) {

        } else if (v == btnSearch) {
            isAlertOkClicked = false ;
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date from_date = sdf.parse(edtFromDate.getText().toString());
                Date to_date = sdf.parse(edtToDate.getText().toString());

                if (from_date.getMonth() == to_date.getMonth()) {
                    acLedgerModels.clear();
                    start = 0;
                    end = 10;
                    showProgressDialog();
                    imgNoData.setVisibility(View.GONE);

                    if(isRetailerSelected() || isResellerSelected() || isDealerSelected()) {
                        System.out.println("Data Spinner: " + dataSpinner);
                        SelectedUserPos = getSelectedUserTypePosition();
                        selectedUserId = dataSpinner.get(SelectedUserPos).getUser_id();
                        System.out.println("Spinner Pos: " + SelectedUserPos + " Spinner USerId: " + selectedUserId);
                    } else {
                        selectedUserId = "";
                        System.out.println("SelectedUserId: " + selectedUserId);
                    }

                    makeNativeAccountLedger();//2
                    acLedgerAdapter = new AccountLedgerAdapter(getContextInstance(), acLedgerModels);
                    lstCashbookSearch.setAdapter(acLedgerAdapter);
                    setBalance();

                    //cashbookAdapter = new CashbookAdapter(getContextInstance(), cashbookModels);
                    //lstCashbookSearch.setAdapter(cashbookAdapter);
                } else {
                    Utility.toast(getContextInstance(), "Please select dates of same month.");
                }
            }
            catch (Exception ex) {
                Log.e(TAG, "Error 3 : "+ ex.getMessage() );
                ex.printStackTrace();
            }
        }

    }

    // display current date
    public void setCurrentDateOnView() {
        dpResult = (DatePicker) view.findViewById(R.id.dpResult);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // set current date into text view
        edtFromDateTest.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));

        // set current date into date picker
        dpResult.init(year, month, day, null);
    }

    /* [START] - 2017_04_28 - Add native code for cash book, and Remove volley code */
    private void makeNativeAccountLedger() {
        // create new threadc
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                    DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date from_date = sdf.parse(edtFromDate.getText().toString());
                    Date to_date = sdf.parse(edtToDate.getText().toString());
                    // set cashBook url
                    String url = URL.accountLedger;
                    // Set parameters list in string array
                    String[] parameters = {
                            "username",
                            "mac_address",
                            "otp_code",
                            "user_type",
                            "fromdate",
                            "todate",
                            "app",
                            "userid"
                    };
                    // set parameters values in string array
                    String[] parametersValues = {
                            strUserName,
                            strMacAddress,
                            strOtpCode,
                            "4",
                            outputFormat.format(from_date),
                            outputFormat.format(to_date),
                            Constants.APP_VERSION,
                            selectedUserId
                    };
                    String response = InternetUtil.getUrlData(url, parameters, parametersValues);
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

    // parse success response
    private void parseSuccessResponse(String response) {
        Log.e(TAG, " AccountLedger Response : "+ response );

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                ll_recycler_view.setVisibility(View.VISIBLE);
                String encrypted_response = jsonObject.getString("data");
                String message = jsonObject.getString("msg");
                Log.e(TAG, "Message : "+ message );
                Log.e(TAG, "Message : "+ message );

                Log.e(TAG,"AccountLedger : " + "encrypted_response : " + encrypted_response);
                String decrypted_response = Constants.decryptAPI((Activity) getActivity(), encrypted_response, strMacAddress);

                Log.e(TAG,"AccountLedger : " + "decrypted_response : " + decrypted_response);
                if(isAlertOkClicked == false) {
                    loadMoreData(decrypted_response);
                }
            } else if (jsonObject.getString("status").equals("2")) {
                int footerCount = lstCashbookSearch.getFooterViewsCount();
                Log.e(TAG,"Footer Count 1 : " + footerCount);

                // lstCashbookSearch.removeFooterView(footerView);
                removeFooterView();
                lstCashbookSearch.addFooterView(footerViewNoMoreData);

                loadMoreFlage = true;
                FLAG_INVALID_DETAIL = true;
                count++;
            } else {
                int footerCount = lstCashbookSearch.getFooterViewsCount();
                Log.e(TAG,"Footer Count 2 : " + footerCount);
                // lstCashbookSearch.removeFooterView(footerView);
                removeFooterView();
                lstCashbookSearch.addFooterView(footerViewNoMoreData);
            }
        }
        catch (JSONException e) {
            Log.e(TAG,"Cashbook : " + "Error 4 : " + e.getMessage());
            Utility.toast(getContextInstance(), "No result found");
            e.printStackTrace();
        }
    }


    private void makeAccountLedgerBalance() {
        // create new threadc
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.balance;
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
                    myHandler.obtainMessage(SUCCESSBALANCE, response).sendToTarget();
                }
                catch (Exception ex) {
                    Log.e(TAG, "  Error  : "+ ex.getMessage() );

                    ex.printStackTrace();
                    myHandler.obtainMessage(ERROR, "Please check your internet access").sendToTarget();
                }
            }
        }).start();
    }

    // parse success response
    private void parseSuccessResponseBalance(String response) {
        Log.e(TAG, " AccountLedger Balance Response : "+ response );

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                ll_recycler_view.setVisibility(View.VISIBLE);
                String encrypted_response = jsonObject.getString("data");
                String message = jsonObject.getString("msg");
                Log.e(TAG, "Message : "+ message );
                Log.e(TAG, "Message : "+ message );

                Log.e(TAG,"AccountLedger : " + "encrypted_response : " + encrypted_response);
                String decrypted_response = Constants.decryptAPI((Activity) getActivity(), encrypted_response, strMacAddress);

                Log.e(TAG,"AccountLedger : " + "decrypted_response : " + decrypted_response);
                JSONObject object = new JSONObject(decrypted_response);
                balance = object.getString("balance");
                loadDefaultAccountLedger();
            } else {

            }
        }
        catch (JSONException e) {
            Log.e(TAG,"Cashbook : " + "Error 4 : " + e.getMessage());
            Utility.toast(getContextInstance(), "No result found");
            e.printStackTrace();
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
                Utility.toast(getContextInstance(), message);
            }
            catch (Exception e) {
                Log.e(TAG,"Error in toast message");
                Log.e(TAG,"ERROR : " + e.getMessage());
            }
        }
        // [END]
    }

    // handle thread messages
    private Handler myHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (msg.what == SUCCESS) {

                dismissProgressDialog();
                parseSuccessResponse(msg.obj.toString());
            } else if (msg.what == ERROR) {
                dismissProgressDialog();
                displayErrorDialog(msg.obj.toString());
            } else if (msg.what == SUCCESSBALANCE) {
                dismissProgressDialog();
                parseSuccessResponseBalance(msg.obj.toString());
            } else if (msg.what == SUCCESSTYPE) {
                dismissProgressDialog();
                parseSuccessTypeResponse(msg.obj.toString());
            }
        }
    };
    // [END]

    private void removeFooterView() {
        int footerCount = lstCashbookSearch.getFooterViewsCount();
        Log.e(TAG,"Footer Count All : " + footerCount);
        lstCashbookSearch.removeFooterView(footerView);
        lstCashbookSearch.removeFooterView(footerViewNoMoreData);
    }

    /*Method : loadMoreData
               load data on scroll*/
    public void loadMoreData(String response) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy hh:mm:ss");
            /*   DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date tamp_date  = null;*/

            JSONObject jsonObject1 = new JSONObject(response);
            JSONArray jsonArray = jsonObject1.getJSONArray("accounts");

            if(jsonArray.length()>0) {

                if (jsonArray.length() < 10) {

                    removeFooterView();

                    lstCashbookSearch.addFooterView(footerViewNoMoreData);

                    loadMoreFlage = true;
                    // txtNoMoreData.setVisibility(View.VISIBLE);
                /*if (jsonArray.length() == 0) {
                    Utility.toast(getContextInstance(), getResources().getString(R.string.alert_servicer_down));
                }*/
                } else {
                    if (start == 0) {
                        int footerCount = lstCashbookSearch.getFooterViewsCount();
                        Log.e(TAG, "Footer Count 4 : " + footerCount);
                        removeFooterView();
                        lstCashbookSearch.addFooterView(footerView);
                    }
                    loadMoreFlage = false;
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    AccountLedgerModel acledgerModel = new AccountLedgerModel();
                    System.out.println("Original date: " + object.getString("created_date"));

                    /*if(Double.parseDouble(object.getString("amount")) > 0) {*/
                    if (!object.getString("amount").equals("0") || !object.getString("amount").equals("0.00")) {

                        try {
                            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            Date newDate = spf.parse(object.getString("created_date"));
                            spf = new SimpleDateFormat("dd-MMM-yy hh:mm:ss");
                            acledgerModel.created_date = spf.format(newDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        // acledgerModel.created_date = object.getString("created_date");
                        acledgerModel.type = object.getString("type");
                        /* [START] - Remove Balance field */
                        // cashbookModel.balance = object.getString("Balance");
                        //acledgerModel.balance = "";
                        // [END]
                        acledgerModel.payment_id = object.getString("recharge_uniqid");
                        acledgerModel.particular = object.getString("payment_name");
                        acledgerModel.cr_dr = object.getString("cr_dr");
                        acledgerModel.amount = object.getString("amount");
                        acledgerModel.balance = object.getString("balance");

                        acLedgerModels.add(acledgerModel);
                        beforeAcLedgerModels.add(acledgerModel);
                    }
                }
                setBalance();
                Collections.reverse(acLedgerModels);
                //acLedgerAdapter.notifyDataSetChanged();
                removeFooterView();
                imgNoData.setVisibility(View.GONE);
            } else {
                imgNoData.setVisibility(View.VISIBLE);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"Cashbook : " + "Error 8 : " + e.toString());

            alertDialog = new AlertDialog.Builder(getContextInstance()).create();
            alertDialog.setTitle("Alert!");
            alertDialog.setCancelable(false);
            alertDialog.setMessage(getResources().getString(R.string.alert_servicer_down));
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                    isAlertOkClicked = true ;
                    loadMoreFlage = false;
                    removeFooterView();
                    dismissProgressDialog();
                }
            });
            alertDialog.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener
                    //@kns.p :: uncomment intent /compare with old code
                    //Intent intent = new Intent(getActivity(), MainActivity.class);
                   // startActivity(intent);
                    //getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                }
                return false;
            }
        });

    }

    // show progress dialog
    private void showProgressDialog() {
        try {
            if (transparentProgressDialog == null) {
                transparentProgressDialog = new TransparentProgressDialog(getContextInstance(), R.drawable.fotterloading);
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

}
