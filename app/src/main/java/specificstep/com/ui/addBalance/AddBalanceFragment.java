package specificstep.com.ui.addBalance;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import specificstep.com.Adapters.ChildUserAdapter;
import specificstep.com.GlobalClasses.AppController;
import specificstep.com.GlobalClasses.TransparentProgressDialog;
import specificstep.com.Models.ChildUserModel;
import specificstep.com.Models.UserList;
import specificstep.com.R;
import specificstep.com.ui.addBalance.DaggerAddBalanceComponent;
import specificstep.com.ui.base.BaseFragment;
import specificstep.com.ui.home.HomeContract;
import specificstep.com.utility.Utility;

public class AddBalanceFragment extends BaseFragment implements AddBalanceContract.View, AdapterView.OnItemClickListener {

    public static final String EXTRA_PHONE_NUMBER = "email";
    public static final String EXTRA_FIRM_NAME = "firm_name";
    @Inject
    AddBalanceContract.Presenter presenter;
    @BindView(R.id.autoCompleteTextView)
    EditText autoCompleteTextView;
    @BindView(R.id.addBalanceLayout)
    ScrollView addBalanceScrollView;
    @BindView(R.id.add_balance_username)
    EditText userNameEditText;
    @BindView(R.id.add_balance_fname)
    EditText firstNameEditText;
    @BindView(R.id.add_balance_mobile)
    EditText mobileEditText;
    @BindView(R.id.add_balance_email)
    EditText emailEditText;
    @BindView(R.id.add_balance_current)
    EditText currentBalanceEditText;
    @BindView(R.id.add_balance_amount)
    EditText balanceEditText;
    @BindView(R.id.add_balance_total)
    EditText totalAmountEditText;
    @BindView(R.id.user_list_view)
    ListView userListRecyclerView;
    @BindView(R.id.imgAddBalanceNoData)
    ImageView imgNoData;

    private boolean mIsInjected;
    private MenuItem balanceMenuItem;
    private TransparentProgressDialog transparentProgressDialog;

    private HomeContract.HomeDelegate homeDelegate;

    @OnClick(R.id.btn_proceed_fragment_recharge)
    void onRechargeButtonClicked(View v) {
        presenter.onRechargeButtonClicked();
    }

    @OnTextChanged(R.id.autoCompleteTextView)
    void onTextChanged(CharSequence text) {
        presenter.onSearchUserTextChanged(text);
    }

    @Override
    public void hideUserListView() {
        userListRecyclerView.setVisibility(View.GONE);
    }

    @OnItemClick(R.id.user_list_view)
    void onUserListItemClick(int position) {
        presenter.onSelectedUser(((ChildUserAdapter)userListRecyclerView.getAdapter()).getItem(position));
    }

    @OnFocusChange(R.id.add_balance_current) void onFocusChanged(boolean focused) {
       if(!focused) {
           try {
               InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
               imm.hideSoftInputFromWindow(balanceEditText.getWindowToken(), 0);
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mIsInjected = injectDependency();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeContract.HomeDelegate) {
            homeDelegate = (HomeContract.HomeDelegate) context;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.start();
    }

    @Override
    public void onStop() {
        presenter.stop();
        super.onStop();
    }

    @Override
    public void onDetach() {
        homeDelegate = null;
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_balance, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(homeDelegate != null) {
            homeDelegate.setToolBarTitle(getString(R.string.add_balance));
        }
        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
            }
        });


    }

    @OnTextChanged(R.id.add_balance_amount)
    void onRechargeAmountTextChanged(CharSequence text) {

        presenter.onRechargeAmountChanged(text);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!mIsInjected) {
            mIsInjected = injectDependency();
        }
        presenter.initialize();
        if(getArguments() != null && getArguments().containsKey(EXTRA_PHONE_NUMBER) && getArguments().containsKey(EXTRA_FIRM_NAME)) {
            presenter.loadUserDetailsByPhoneNumber(getArguments().getString(EXTRA_PHONE_NUMBER), getArguments().getString(EXTRA_FIRM_NAME));
        }
    }

    boolean injectDependency() {
        try {
            DaggerAddBalanceComponent.builder()
                    .applicationComponent(((AppController) getActivity().getApplication()).getApplicationComponent())
                    .addBalanceModule(new AddBalanceModule(this))
                    .build()
                    .inject(this);
            return true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_balance, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        balanceMenuItem = menu.findItem(R.id.action_balance_menu_main);
    }

    @Override
    public void setUserListAdapter(List<ChildUserModel> userModels) {
        ChildUserAdapter adapter = new ChildUserAdapter(getActivity(), userModels);
        userListRecyclerView.setAdapter(adapter);
    }

    @Override
    public void showBalance(BigDecimal amount) {
        if(balanceMenuItem != null) {
            balanceMenuItem.setTitleCondensed(getResources().getString(R.string.currency_format, Utility.formatBigDecimalToString(amount)));
        }
    }

    @Override
    public void onDestroy() {
        presenter.destroy();
        super.onDestroy();
    }

    @Override
    public Context context() {
        return getActivity();
    }

    @Override
    public void showInfoDialog(String errorMessage) {
        showDialog(getString(R.string.info), errorMessage);
    }

    @Override
    public void showUserName(String userName) {
        userNameEditText.setText(userName);
        userNameEditText.setSelection(userName.length());
    }

    @Override
    public void showFirmName(String firmName) {
        firstNameEditText.setText(firmName);
        firstNameEditText.setSelection(firmName.length());
    }

    @Override
    public void showEmail(String email) {
        emailEditText.setText(email);
        emailEditText.setSelection(email.length());
    }

    @Override
    public void showPhoneNumber(String phoneNo) {
        mobileEditText.setText(phoneNo);
        mobileEditText.setSelection(phoneNo.length());
    }

    @Override
    public void showAmount(float balance) {
        currentBalanceEditText.setText(String.valueOf(balance));
        currentBalanceEditText.setSelection(balanceEditText.getText().length());
    }

    @Override
    public void showTotalAmount(String balance) {
        totalAmountEditText.setText(balance);
        totalAmountEditText.setSelection(totalAmountEditText.getText().length());
    }

    @Override
    public void showUserContainer() {
        addBalanceScrollView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAutoCompleteView() {
        autoCompleteTextView.setVisibility(View.GONE);
    }

    @Override
    public void hideUserContainer() {
        addBalanceScrollView.setVisibility(View.GONE);
    }

    @Override
    public void showAutoCompleteView() {
        autoCompleteTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showUserListView() {
        userListRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoData() {
        imgNoData.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoData() {
        imgNoData.setVisibility(View.GONE);
    }

    @Override
    public void showLoadingView() {
        hideLoadingView();
        transparentProgressDialog = new TransparentProgressDialog(getActivity(), R.drawable.fotterloading);

        if (!transparentProgressDialog.isShowing()) {
            transparentProgressDialog.show();
        }

    }

    @Override
    public void hideLoadingView() {
        if (transparentProgressDialog != null) {
            transparentProgressDialog.dismiss();
            transparentProgressDialog = null;
        }
    }

    @Override
    public CharSequence getUserEnteredAmount() {
        return balanceEditText.getText().toString();
    }

    @Override
    public void showToastMessage(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getRechargeBalance() {
        return balanceEditText.getText().toString().trim();
    }

    @Override
    public void showErrorDialog(String msg) {
        showDialog(getString(R.string.error), msg);
    }

    @Override
    public void showWarningDialog(String msg) {
        showDialog(getString(R.string.info), msg);
    }

    @Override
    public String getUserName() {
        return userNameEditText.getText().toString().trim();
    }

    @Override
    public String getFirmName() {
        return firstNameEditText.getText().toString().trim();
    }

    @Override
    public String getPhone() {
        return mobileEditText.getText().toString().trim();
    }

    @Override
    public String getEmail() {
        return emailEditText.getText().toString().trim();
    }

    @Override
    public String getCurrentBalance() {
        return currentBalanceEditText.getText().toString().trim();
    }

    @Override
    public String getTotalAmount() {
        return totalAmountEditText.getText().toString().trim();
    }

    @Override
    public void showConfirmRechargePopup(String userName, String firmName, String phone, String email, String currentBalance, String rechargeBalance, String totalAmount) {
        final Dialog dialog = new Dialog(getActivity(), R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog_success);

        TextView dialog_username = (TextView) dialog.findViewById(R.id.tv_recharge_id);
        TextView dialog_firm_name = (TextView) dialog.findViewById(R.id.tv_recharge_status);
        TextView dialog_phone_no = (TextView) dialog.findViewById(R.id.tv_date_time);
        TextView dialog_email = (TextView) dialog.findViewById(R.id.tv_mo_no);
        TextView dialog_amount = (TextView) dialog.findViewById(R.id.tv_company);
        final TextView dialog_new_amount = (TextView) dialog.findViewById(R.id.tv_product);
        TextView dialog_total_amount = (TextView) dialog.findViewById(R.id.tv_amount);

        dialog_username.setText(userName);
        dialog_firm_name.setText(firmName);
        dialog_phone_no.setText(phone);
        dialog_email.setText(email);
        dialog_amount.setText(getString(R.string.currency_format, currentBalance));
        dialog_new_amount.setText(getString(R.string.currency_format, rechargeBalance));
        dialog_total_amount.setText(getString(R.string.currency_format, totalAmount));

        Button cancelButton = (Button) dialog.findViewById(R.id.btn_cancel);
        Button confirmButton = (Button) dialog.findViewById(R.id.btn_ok);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                presenter.onConfirmRechargeButtonClicked(getActivity());
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void goBack() {
        if (homeDelegate != null) {
            homeDelegate.onAddBalanceCompleted();
        }
    }

    @Override
    public void showAddBalanceSuccessPopup(String message) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.done))
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        goBack();
                    }
                })
                .create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlack));
            }
        });
        dialog.show();
    }

    @Override
    public void setAutoCompleteText(String text) {
        autoCompleteTextView.setText(text);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        presenter.onUserSelected((UserList) adapterView.getItemAtPosition(i));
    }

}
