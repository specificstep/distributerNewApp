package specificstep.com.ui.cashSummary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import specificstep.com.Adapters.CashSummeryAdapter;
import specificstep.com.Database.DatabaseHelper;
import specificstep.com.GlobalClasses.AppController;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.GlobalClasses.MCrypt;
import specificstep.com.GlobalClasses.TransparentProgressDialog;
import specificstep.com.GlobalClasses.URL;
import specificstep.com.Models.CashSummaryModel;
import specificstep.com.Models.CashSummaryUserTypeModel;
import specificstep.com.Models.Default;
import specificstep.com.Models.User;
import specificstep.com.Models.UserList;
import specificstep.com.R;
import specificstep.com.data.source.local.Pref;
import specificstep.com.ui.base.BaseFragment;
import specificstep.com.ui.cashSummary.DaggerCashSummaryComponent;
import specificstep.com.ui.home.HomeContract;
import specificstep.com.utility.InternetUtil;
import specificstep.com.utility.Utility;

public class CashSummaryFragment extends BaseFragment implements CashSummaryContract.View, RadioGroup.OnCheckedChangeListener {

    @Inject
    CashSummaryContract.Presenter presenter;
    @BindView(R.id.ll_Search)
    LinearLayout searchContainer;
    @BindView(R.id.from_date)
    EditText fromDateEditText;
    @BindView(R.id.to_date)
    EditText toDateEditText;
    @BindView(R.id.dp_CashSummery_Result)
    DatePicker currentDatePicker;
    /*@BindView(R.id.radio)
    RadioGroup userTypeRadioGroup;*/
    @BindView(R.id.userList)
    Spinner userListSpinner;
    @BindView(R.id.ll_ResetSearch)
    LinearLayout resetControlsContainer;
    @BindView(R.id.txt_CashSummery_Result)
    TextView userTypeTextView;
    @BindView(R.id.ll_recycler_view)
    View searchResultContainerView;
    @BindView(R.id.lst_trans_search_fragment_trans_search)
    ListView searchResultListView;
    @BindView(R.id.imgCashSummaryNoData)
    ImageView imgNoData;
    @BindView(R.id.txtCashSummaryOpening)
    TextView txtOpeningBalance;
    @BindView(R.id.txtCashSummaryClosing)
    TextView txtClosingBalance;
    /*@BindView(R.id.rbt_Self)
    RadioButton selfRadioButton;
    @BindView(R.id.rbt_Reseller)
    RadioButton resellerRadioButton;
    @BindView(R.id.rbt_Dealer)
    RadioButton dealerRadioButton;*/
    public static RadioGroup ll;
    String balance;
    public static View view;
    DatePickerDialog.OnDateSetListener fromDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            presenter.onFromDateSelected(year, month, dayOfMonth);
        }
    };
    DatePickerDialog.OnDateSetListener toDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            presenter.onToDateSelected(year, month, dayOfMonth);
        }
    };
    private boolean mIsInjected;
    private HomeContract.HomeDelegate homeDelegate;
    private TransparentProgressDialog transparentProgressDialog;

    private String strMacAddress, strUserName, strOtpCode, strRegistrationDateTime;
    private ArrayList<User> userArrayList;
    private DatabaseHelper databaseHelper;
    private final int SUCCESS = 1, ERROR = 2, SUCCESSBALANCE = 3;
    String TAG = "Cash Summary Fragment :: ";
    private AlertDialog alertDialog;
    List<CashSummaryUserTypeModel> cashSummaryTypeList;
    CashSummaryUserTypeModel userTypeModel;
    Pref pref;
    public static int spnPosition;
    private ArrayList<UserList> userListArrayList;
    List<UserList> dataSpinner;
    public static String selectedUserType;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //userTypeRadioGroup.setOnCheckedChangeListener(this);
        //selfRadioButton.setChecked(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsInjected = injectDependency();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cash_summary, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ButterKnife.bind(this, view);
        return view;
    }

    public void initialize() {

        databaseHelper = new DatabaseHelper(getActivity());
        userArrayList = databaseHelper.getUserDetail();
        strMacAddress = userArrayList.get(0).getDevice_id();
        strUserName = userArrayList.get(0).getUser_name();
        strOtpCode = userArrayList.get(0).getOtp_code();
        alertDialog = new AlertDialog.Builder(getActivity()).create();

    }

    @OnClick(R.id.btn_search_fragment_trans_search)
    void onSearchButtonClicked() {
        int id = ll.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = (RadioButton) view.findViewById(id);
        selectedUserType = selectedRadioButton.getText().toString();
        presenter.onSearchButtonClicked(selectedRadioButton.getText().toString());
        spnPosition = userListSpinner.getSelectedItemPosition();
    }

    @OnClick(R.id.btn_ResetSearch)
    void onResetButtonClicked() {
        presenter.onResetButtonClicked();
    }

    @OnClick(R.id.from_date)
    void onFromDateEditTextClick() {
        presenter.onFromDateEditTextClicked();
    }

    @OnClick(R.id.to_date)
    void onToDateEditTextClick() {
        presenter.onToDateEditTextClicked();
    }

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
    public void onDestroy() {
        presenter.destroy();
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(homeDelegate != null) {
            homeDelegate.setToolBarTitle(getString(R.string.cash_summary));
        }
        if (!mIsInjected) {
            mIsInjected = injectDependency();
        }
        initialize();
        makeUserRoleCall();
        makeCashSummaryBalance();
    }

    boolean injectDependency() {
        try {
            DaggerCashSummaryComponent.builder()
                    .applicationComponent(((AppController) getActivity().getApplication()).getApplicationComponent())
                    .cashSummaryModule(new CashSummaryModule(this))
                    .build()
                    .inject(this);
            return true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void hideResellerRadioButton() {
        int id = ll.getCheckedRadioButtonId();
        if(id == 3) {
            RadioButton selectedRadioButton = (RadioButton) view.findViewById(id);
            selectedRadioButton.setVisibility(View.GONE);
        }
        //resellerRadioButton.setVisibility(View.GONE);
    }

    @Override
    public void showResellerRadioButton() {
        int id = ll.getCheckedRadioButtonId();
        if(id == 3) {
            RadioButton selectedRadioButton = (RadioButton) view.findViewById(id);
            selectedRadioButton.setVisibility(View.VISIBLE);
        }
        //resellerRadioButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateDatePicker(int year, int month, int dayOfMonth) {
        currentDatePicker.init(year, month, dayOfMonth, null);
    }

    @Override
    public void updateFromDate(String strDate) {
        fromDateEditText.setText(strDate);
    }

    @Override
    public void updateToDate(String strDate) {
        toDateEditText.setText(strDate);
    }

    @Override
    public void showDatePickerForFromDate(Calendar calendar) {
        long timeInMilliseconds = new Date().getTime();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                fromDateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(timeInMilliseconds);
        datePickerDialog.show();
    }

    @Override
    public void showToDateDatePickerDialog(Calendar calendar, long maxTimeMillis) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                toDateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxTimeMillis);
        datePickerDialog.show();
    }

    @Override
    public void hideUserListSpinner() {
        userListSpinner.setVisibility(View.GONE);
    }

    @Override
    public void showUserListSpinner() {
        userListSpinner.setVisibility(View.VISIBLE);
    }

    @Override
    public Context context() {
        return getActivity();
    }

    @Override
    public void setUserSpinnerAdapter(List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner, data);
        userListSpinner.setAdapter(adapter);
    }

    @Override
    public void showSearchWidgetContainer() {
        searchContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideResetViewContainer() {
        resetControlsContainer.setVisibility(View.GONE);
    }

    @Override
    public void showToastMessage(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public String getSelectedUserType() {
        return userListSpinner.getSelectedItem() != null ? userListSpinner.getSelectedItem().toString() : "";
    }

    @Override
    public boolean isResellerSelected() {
        //return userTypeRadioGroup.getCheckedRadioButtonId() == R.id.rbt_Reseller;
        int id = ll.getCheckedRadioButtonId();
        if(id == 3) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isDealerSelected() {
        int id = ll.getCheckedRadioButtonId();
        if(id == 7) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isRetailerSelected() {
        int id = ll.getCheckedRadioButtonId();
        if(id == 4) {
            return true;
        }
        return false;
    }

    @Override
    public int getSelectedUserTypePosition() {
        return userListSpinner.getSelectedItemPosition();
    }

    @Override
    public void setListAdapter(List<CashSummaryModel> summaryModels, final String userType) {
        CashSummeryAdapter cashSummeryAdapter_new = new CashSummeryAdapter(
                getActivity(),
                summaryModels,
                new CashSummeryAdapter.OnReverseBalanceListener() {
                    @Override
                    public void onBalanceReversed() {
                        presenter.reloadCashSummary(userType);
                    }
                }
        );

        setBalance(summaryModels);
        if(summaryModels.size()>0) {
            searchResultListView.setAdapter(cashSummeryAdapter_new);
            searchResultListView.setVisibility(View.VISIBLE);
            imgNoData.setVisibility(View.GONE);
        } else {
            searchResultListView.setVisibility(View.GONE);
            imgNoData.setVisibility(View.VISIBLE);
        }

    }

    public void setBalance(List<CashSummaryModel> acLedgerModels) {

        try {
            if(acLedgerModels.size()>0) {

                int id = ll.getCheckedRadioButtonId();
                String user_id = "";
                if(id != 2) {
                    RadioButton selectedRadioButton = (RadioButton) view.findViewById(id);
                    ArrayList<UserList> userListArrayList = databaseHelper.getUserListDetailsByType(selectedRadioButton.getText().toString());
                    List<UserList> data = new ArrayList<UserList>();
                    for (UserList userList : userListArrayList) {
                        if (userList.getParentUserId().equals(userArrayList.get(0).getUser_id())) {
                            data.add(userList);
                        }
                    }
                    UserList userList = data.get(getSelectedUserTypePosition());
                    user_id = userList.getUser_id();
                } else {
                    SharedPreferences SharedPreferences = getActivity().getSharedPreferences(Pref.PREF_FILE, Context.MODE_PRIVATE);
                    pref = new Pref(SharedPreferences);
                    user_id = pref.getValue(Pref.KEY_USER_ID, "");
                }
                System.out.println("Selected UserID: " + user_id);

                int indexDebit = acLedgerModels.get(0).getUsername().indexOf("-");
                int indexCredit = acLedgerModels.get(0).getPaymentTo().indexOf("-");
                System.out.println("indexDebit: " + indexDebit + " indexCredit: " + indexCredit);
                String idDebit = acLedgerModels.get(0).getUsername().substring(0,indexDebit-1);
                String idCredit = acLedgerModels.get(0).getPaymentTo().substring(0,indexCredit-1);
                System.out.println("idDebit: " + idDebit + "idCredit: " + idCredit + "temp");

                Double openBal = 0.0;
                NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
                Number numberBalance = null, numberAmount = null, numberEndBal = null;
                if(idDebit.trim().equals(user_id)) {
                    numberBalance = format.parse(acLedgerModels.get(0).getDebitUserBalance());
                    numberAmount = format.parse(acLedgerModels.get(0).getDebitAmount());
                    openBal = numberBalance.doubleValue() + numberAmount.doubleValue();
                } else {
                    numberBalance = format.parse(acLedgerModels.get(0).getCreditUserBalance());
                    numberAmount = format.parse(acLedgerModels.get(0).getCreditAmount());
                    openBal = numberBalance.doubleValue() - numberAmount.doubleValue();
                }

                int indexDebitEnd = acLedgerModels.get(acLedgerModels.size()-1).getUsername().indexOf("-");
                int indexCreditEnd = acLedgerModels.get(acLedgerModels.size()-1).getPaymentTo().indexOf("-");
                System.out.println("indexDebitEnd: " + indexDebitEnd + " indexCreditEnd: " + indexCreditEnd);
                String idDebitEnd = acLedgerModels.get(acLedgerModels.size()-1).getUsername().substring(0,indexDebitEnd-1);
                String idCreditEnd = acLedgerModels.get(acLedgerModels.size()-1).getPaymentTo().substring(0,indexCreditEnd-1);
                System.out.println("idDebitEnd: " + idDebitEnd + "idCreditEnd: " + idCreditEnd + "temp");
                if(idDebitEnd.trim().equals(user_id)) {
                    numberEndBal = format.parse(acLedgerModels.get(acLedgerModels.size()-1).getDebitUserBalance());
                } else {
                    numberEndBal = format.parse(acLedgerModels.get(acLedgerModels.size()-1).getCreditUserBalance());
                }
                BigDecimal cached = new BigDecimal(openBal+"");
                txtOpeningBalance.setText(getResources().getString(R.string.currency_format, Utility.formatBigDecimalToString(cached)));
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

    private void makeCashSummaryBalance() {
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
                String encrypted_response = jsonObject.getString("data");
                String message = jsonObject.getString("msg");
                Log.e(TAG, "Message : "+ message );
                Log.e(TAG, "Message : "+ message );

                Log.e(TAG,"AccountLedger : " + "encrypted_response : " + encrypted_response);
                String decrypted_response = Constants.decryptAPI((Activity) getActivity(), encrypted_response, strMacAddress);

                Log.e(TAG,"AccountLedger : " + "decrypted_response : " + decrypted_response);
                JSONObject object = new JSONObject(decrypted_response);
                balance = object.getString("balance");
                if(getActivity() != null) {
                    txtOpeningBalance.setText(getResources().getString(R.string.currency_format, balance));
                    txtClosingBalance.setText(getResources().getString(R.string.currency_format, balance));
                }
            } else {

            }
        }
        catch (JSONException e) {
            Log.e(TAG,"Cashbook : " + "Error 4 : " + e.getMessage());
            Utility.toast(getActivity(), "No result found");
            e.printStackTrace();
        }
    }

    @Override
    public void updateDetailText(String text) {
        userTypeTextView.setText(text);
    }

    @Override
    public void showResetViewContainer() {
        resetControlsContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSearchWidgetContainer() {
        searchContainer.setVisibility(View.GONE);
    }

    @Override
    public void showSearchResultContainer() {
        searchResultContainerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgressIndicator() {
        if (transparentProgressDialog != null) {
            transparentProgressDialog.dismiss();
            transparentProgressDialog = null;
        }
        transparentProgressDialog = new TransparentProgressDialog(getActivity(), R.drawable.fotterloading);

        if (!transparentProgressDialog.isShowing()) {
            transparentProgressDialog.show();
        }
    }

    @Override
    public void showErrorDialog(String errorMessage) {
        showDialog(getString(R.string.info), errorMessage);
    }

    @Override
    public void hideProgressIndicator() {
        if (transparentProgressDialog != null) {
            transparentProgressDialog.dismiss();
            transparentProgressDialog = null;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
        switch (id) {
            /*case R.id.rbt_Reseller:
                presenter.onResellerUserRadioButtonChecked();
                break;
            case R.id.rbt_Dealer:
                presenter.onDealerUserRadioButtonChecked();
                break;
            case R.id.rbt_Retailer:
                presenter.onRetailerUserRadioButtonChecked();
                break;
            case R.id.rbt_Self:
                presenter.onSelfRadioButtonChecked();
                break;*/
        }
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
            }
        }
    };
    // [END]

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void parseSuccessResponse(String response) {

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
                showErrorDialog(jsonObject.getString("msg")+"");
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
        try {
            for (int row = 0; row < 1; row++) {
                ll = new RadioGroup(getActivity());
                ll.setOrientation(LinearLayout.HORIZONTAL);
                for (int i = 0; i < number; i++) {
                    if (Integer.parseInt(cashSummaryTypeList.get(i).getRid()) == 2 ||
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

                    /*if(Integer.parseInt(cashSummaryTypeList.get(i).getRid()) != 2) {

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
                        presenter.onSelfRadioButtonChecked();
                    } else {
                        rdbtn.setText(cashSummaryTypeList.get(i).getRole_name());
                    }
                    ll.addView(rdbtn);*/


                        if (Integer.parseInt(cashSummaryTypeList.get(i).getRid()) != 2) {

                            List<UserList> userListArrayList = databaseHelper.getUserListDetailsByType(cashSummaryTypeList.get(i).getRole_name());
                            if (userListArrayList.size() > 0) {
                                rdbtn.setVisibility(View.VISIBLE);
                            } else {
                                rdbtn.setVisibility(View.GONE);
                            }
                        }

                        if (Integer.parseInt(cashSummaryTypeList.get(i).getRid()) == 2) {
                            rdbtn.setText("Self");
                            rdbtn.setChecked(true);
                            presenter.onSelfRadioButtonChecked();
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
                            if (userList.getParentUserId().equals(userArrayList.get(0).getUser_id())) {
                                data.add(userList.getUser_name() + " - " + userList.getPhone_no());
                            }
                        }


                        if (Integer.parseInt(cashSummaryTypeList.get(i).getRid()) != 2) {
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


                presenter.initialize();

            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private View.OnClickListener mThisButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            int s = ((RadioButton) v).getId();
            if(s == 2) {
                presenter.onSelfRadioButtonChecked();
            } else if(s == 3) {
                presenter.onResellerUserRadioButtonChecked(((RadioButton) v).getText().toString());
                System.out.println(((RadioButton) v).getText().toString());
            } else if(s == 4) {
                presenter.onRetailerUserRadioButtonChecked(((RadioButton) v).getText().toString());
                System.out.println(((RadioButton) v).getText().toString());
            } else if(s == 7) {
                presenter.onDealerUserRadioButtonChecked(((RadioButton) v).getText().toString());
                System.out.println(((RadioButton) v).getText().toString());
            }
        }
    };

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

}
