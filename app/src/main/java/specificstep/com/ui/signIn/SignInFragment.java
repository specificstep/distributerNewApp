package specificstep.com.ui.signIn;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.common.base.Strings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import specificstep.com.Database.DatabaseHelper;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.GlobalClasses.TransparentProgressDialog;
import specificstep.com.GlobalClasses.URL;
import specificstep.com.Models.User;
import specificstep.com.R;
import specificstep.com.ui.base.BaseFragment;
import specificstep.com.ui.dashboard.DashboardActivity;
import specificstep.com.ui.forgotOtpVarification.ForgotOtpVarificationActivity;
import specificstep.com.ui.otpVerification.OtpVerificationActivity;
import specificstep.com.ui.signup.SignUpActivity;
import specificstep.com.utility.InternetUtil;
import specificstep.com.utility.PermissionUtils;
import specificstep.com.utility.Utility;

public class SignInFragment extends BaseFragment implements SignInContract.View {

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 101;
    @BindView(R.id.username_edit_text)
    EditText usernameEditText;
    @BindView(R.id.locked_unlocked_image_view)
    ImageView lockUnlockPasswordImageView;
    @BindView(R.id.password_edit_text)
    EditText passwordEditText;
    @BindView(R.id.show_hide_password_image_view)
    ImageView showHideImageView;
    @BindView(R.id.cb_remember_me)
    CheckBox rememberCheckBox;
    @BindView(R.id.forgot_password_text)
    TextView forgotPassword;
    private SignInContract.Presenter presenter;
    private TransparentProgressDialog transparentProgressDialog;
    private DatabaseHelper databaseHelper;
    private ArrayList<User> userArrayList;
    private String strMacAddress, strUserName, strOtpCode, strRegistrationDateTime;
    private final int SUCCESS = 1, ERROR = 2;
    String TAG = "Sign In Fragment :: ";
    private AlertDialog alertDialog;
    private static String UserName;

    public static SignInFragment getInstance(String userName) {
        SignInFragment fragment = new SignInFragment();
        if (!Strings.isNullOrEmpty(userName)) {
            Bundle bundle = new Bundle();
            bundle.putString(SignInActivity.EXTRA_USER_NAME, userName);
            UserName = userName;
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public void setPresenter(SignInContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        ButterKnife.bind(this, view);
        Constants.chaneIcon(getActivity(),(ImageView) view.findViewById(R.id.imageView));
        databaseHelper = new DatabaseHelper(getActivity());
        userArrayList = databaseHelper.getUserDetail();
        strMacAddress = userArrayList.get(0).getDevice_id();
        strUserName = userArrayList.get(0).getUser_name();
        strOtpCode = userArrayList.get(0).getOtp_code();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(SignInActivity.EXTRA_USER_NAME)) {
            autoFillUserName(getArguments().getString(SignInActivity.EXTRA_USER_NAME));
        }
        presenter.initialize(usernameEditText.getText().toString().trim());
        readPhoneState();
    }

    @Override
    public void onDestroy() {
        presenter.destroy();
        super.onDestroy();
    }

    @OnClick({R.id.login_button, R.id.cb_remember_me, R.id.show_hide_password_image_view, R.id.forgot_password_text})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                presenter.onLoginButtonClicked(getActivity());
                break;
            case R.id.cb_remember_me:
                presenter.onRememberMeToggleChanged(rememberCheckBox.isChecked());
                break;
            case R.id.show_hide_password_image_view:
                presenter.onPasswordToggleVisible();
                break;
            case R.id.forgot_password_text:
                setProgressIndicator();
                makeForgotPasswordApiCall();
                break;
        }
    }

    //Forgot password call
    public void makeForgotPasswordApiCall() {

        //scheme name thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set cashBook url
                    String url = URL.forgot_password;
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
                    System.out.println("Forgot response: " + response);
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

    // handle submit thread messages
    private Handler myHandler = new Handler() {
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

    //parse submit api
    public void parseSubmitSuccessResponse(String response) {

        Log.e(TAG, " Forgot Response : "+ response );

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("1")) {
                String message = jsonObject.getString("message");
                Log.e(TAG, "Message : "+ message );
                Log.e(TAG, "Message : "+ message );
                Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
                Intent intent = new Intent(getActivity(), ForgotOtpVarificationActivity.class);
                intent.putExtra(OtpVerificationActivity.EXTRA_USERNAME, UserName);
                startActivity(intent);
                getActivity().finish();
            } else if (jsonObject.getString("status").equals("2")) {
                Toast.makeText(getActivity(),jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
            } else {

            }
        }
        catch (JSONException e) {
            Log.e(TAG,"Cashbook : " + "Error 4 : " + e.getMessage());
            Utility.toast(getActivity(), "No result found");
            e.printStackTrace();
        }

    }

    @OnTextChanged(R.id.password_edit_text)
    void onPasswordTextChanged() {
        presenter.onPasswordChanged(passwordEditText.getText().toString().trim());
    }

    public void readPhoneState() {
        if (!PermissionUtils.hasPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)) {
            if (PermissionUtils.shouldShowRequestPermission(this, Manifest.permission.READ_PHONE_STATE)) {
                PermissionUtils.requestPermission(this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
        }
    }

    @Override
    public void showPasswordVisibilityIcon(@DrawableRes int drawableResId) {
        showHideImageView.setImageResource(drawableResId);
    }

    @Override
    public void showPassword() {
        passwordEditText.setTransformationMethod(null);
        passwordEditText.setSelection(passwordEditText.getText().length());
    }

    @Override
    public void hidePassword() {
        passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
        passwordEditText.setSelection(passwordEditText.getText().length());
    }

    @Override
    public void hidePasswordToggle() {
        showHideImageView.setVisibility(View.GONE);
    }

    @Override
    public void showPasswordToggle() {
        showHideImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPasswordIcon(@DrawableRes int drawableResId) {
        lockUnlockPasswordImageView.setImageResource(drawableResId);
    }

    @Override
    public void showConfirmRememberPopup() {
        String htmlText = " %s ";
        String alert_message;
        alert_message = "<html><body leftmargin=\"0\" topmargin=\"0\" rightmargin=\"0\" bottommargin=\"0\"><p align=\"justify\">";
        alert_message += getString(R.string.remember_password_confirm_prompt_msg,
                Constants.changeAppName(getActivity()),
                Constants.changeAppName(getActivity()));
        alert_message += "</p></body></html>";

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.alert_login);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.50);
        dialog.getWindow().setLayout(width, height);
        WebView webView = (WebView) dialog.findViewById(R.id.web_alert_content);
        TextView tv_yes = (TextView) dialog.findViewById(R.id.tv_yes_alert_login);
        TextView tv_no = (TextView) dialog.findViewById(R.id.tv_no_alert_login);
        webView.setScrollContainer(false);
        webView.loadData(String.format(htmlText, alert_message), "text/html", "utf-8");
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                rememberCheckBox.setChecked(false);
            }
        });
        dialog.show();
    }

    @Override
    public String getUserName() {
        return usernameEditText.getText().toString().trim();
    }

    @Override
    public String getPassword() {
        return passwordEditText.getText().toString().trim();
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context context() {
        return getActivity();
    }

    @Override
    public boolean isRememberPassword() {
        return rememberCheckBox.isChecked();
    }

    @Override
    public void hideProgressIndicator() {
        if (transparentProgressDialog != null) {
            transparentProgressDialog.dismiss();
            transparentProgressDialog = null;
        }
    }

    @Override
    public void showErrorDialog(String errorMessage) {
        showDialog(getString(R.string.info), errorMessage);
    }

    @Override
    public void showMaxLoginRetryPopup(int maxRetry) {
        showDialog(
                getString(R.string.login_error),
                getString(R.string.max_login_retry_message, maxRetry),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        presenter.onMaxRetryPopupOkButtonClicked();
                    }
                });
    }

    @Override
    public void selectRememberPasswordCheckBox() {
        rememberCheckBox.setChecked(true);
    }

    @Override
    public void autoFillPassword(String password) {
        passwordEditText.setText(password);
        passwordEditText.setSelection(password.length());
    }

    @Override
    public void showRemainingSignInRetryPopup(int remainingRetry) {
        showDialog(getString(R.string.login_error), getString(R.string.invalid_error_remaining) + " " + remainingRetry + " " + (remainingRetry > 1 ? getString(R.string.login_attempts_plural) : getString(R.string.login_attempts_single)));
    }

    @Override
    public void showSignUpScreen() {
        startActivity(new Intent(getActivity(), SignUpActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        getActivity().finish();
    }

    @Override
    public void showMainScreen() {
        Intent intent = new Intent(getActivity(), DashboardActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void setProgressIndicator() {
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
    public void autoFillUserName(String userName) {
        usernameEditText.setText(userName);
        usernameEditText.setSelection(usernameEditText.getText().length());
    }
}
