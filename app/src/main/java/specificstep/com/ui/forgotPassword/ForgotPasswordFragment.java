package specificstep.com.ui.forgotPassword;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import specificstep.com.GlobalClasses.TransparentProgressDialog;
import specificstep.com.R;
import specificstep.com.ui.base.BaseFragment;
import specificstep.com.ui.home.HomeContract;
import specificstep.com.ui.signIn.SignInActivity;

import static specificstep.com.ui.forgotPassword.ForgotPasswordActivity.EXTRA_OTP;
import static specificstep.com.ui.forgotPassword.ForgotPasswordActivity.EXTRA_PASSWORD;

public class ForgotPasswordFragment extends BaseFragment implements ForgotPasswordContract.View {

    @Inject
    ForgotPasswordContract.Presenter presenter;

    @BindView(R.id.locked_unlocked_old_password_image_view)
    ImageView lockUnlockOldPasswordImageView;
    @BindView(R.id.old_password_edit_text)
    EditText oldPasswordEditText;
    @BindView(R.id.show_hide_old_password_image_view)
    ImageView showHideOldPasswordImageView;
    @BindView(R.id.locked_unlocked_new_password_image_view)
    ImageView lockUnlockNewPasswordImageView;
    @BindView(R.id.new_password_edit_text)
    EditText newPasswordEditText;
    @BindView(R.id.show_hide_new_password_image_view)
    ImageView showHideNewPasswordImageView;
    @BindView(R.id.locked_unlocked_confirm_password_image_view)
    ImageView lockUnlockConfirmPasswordImageView;
    @BindView(R.id.confirm_password_edit_text)
    EditText confirmPasswordEditText;
    @BindView(R.id.show_hide_confirm_password_image_view)
    ImageView showHideConfirmPasswordImageView;

    private boolean mIsInjected;
    private HomeContract.HomeDelegate homeDelegate;
    private TransparentProgressDialog transparentProgressDialog;
    String forgot_otp, password;

    public static ForgotPasswordFragment getInstance(String key_otp, String password) {

        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_OTP, key_otp);
        bundle.putString(EXTRA_PASSWORD, password);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setPresenter(ForgotPasswordContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    /*@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(homeDelegate != null) {
            homeDelegate.setToolBarTitle(getString(R.string.change_password));
        }
        if (!mIsInjected) {
            mIsInjected = injectDependency();
        }

        presenter.initialize();
    }*/

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            retrieveArguments(savedInstanceState);
        } else {
            retrieveArguments(getArguments());
        }
    }

    private void retrieveArguments(Bundle bundle) {
        if (bundle == null) return;
            forgot_otp = bundle.getString(EXTRA_OTP, "");
            password = bundle.getString(EXTRA_PASSWORD, "");
            System.out.println("******* New Forgot password: " + password + " *******************");

    }

    @OnClick({
            R.id.btn_ChangePassword,
            R.id.show_hide_old_password_image_view,
            R.id.show_hide_new_password_image_view,
            R.id.show_hide_confirm_password_image_view})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ChangePassword:
                if(checkPassword()) {
                    /** password == 1 -> check validation
                     * */
                    if(password.equalsIgnoreCase("1")) {
                        String new_pass = newPasswordEditText.getText().toString();

                        if (checkValidation(new_pass)) {
                            //api calls
                            presenter.onChangePasswordButtonClicked(getActivity(),forgot_otp,newPasswordEditText.getText().toString(),confirmPasswordEditText.getText().toString());
                        } else {
                            Toast.makeText(getActivity(), "password must contain " +
                                    "Special Character , Uppercase & Lowercase Latter and Number with minimum 8 characters", Toast.LENGTH_LONG).show();
                        }

                    }
                    else {
                        //api calls
                        presenter.onChangePasswordButtonClicked(getActivity(),forgot_otp,newPasswordEditText.getText().toString(),confirmPasswordEditText.getText().toString());
                    }
                }

                break;
            case R.id.show_hide_old_password_image_view:
                presenter.toggleOldPasswordVisibility();
                break;
            case R.id.show_hide_new_password_image_view:
                presenter.toggleNewPasswordVisibility();
                break;
            case R.id.show_hide_confirm_password_image_view:
                presenter.toggleConfirmPasswordVisibility();
                break;
        }
    }

    private boolean checkPassword() {
        boolean status = false;
        if(TextUtils.isEmpty(newPasswordEditText.getText())) {
            Toast.makeText(getActivity(),"Please Enter New Password.", Toast.LENGTH_LONG).show();
            status = false;
        } else if(TextUtils.isEmpty(confirmPasswordEditText.getText())) {
            Toast.makeText(getActivity(),"Please Enter Confirm Password.", Toast.LENGTH_LONG).show();
            status = false;
        } else if(!newPasswordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())){
            Toast.makeText(getActivity(),"New Password and Confirm Password does not match!", Toast.LENGTH_LONG).show();
            status = false;
        } else {
            status = true;
        }
        return status;
    }

    private boolean checkValidation(String new_pass) {
        //Password validation as below if you receive 1 in password
        //Password Must Contain
        //Special Character,
        // Uppercase & Lowercase Latter,
        // Number,
        // Minimum 8 Character.
        if(new_pass.length()>=8 && isValidPassword(new_pass)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    @OnTextChanged(R.id.old_password_edit_text)
    void onOldPasswordTextChanged(CharSequence text) {
        presenter.onOldPasswordTextChanged(text);
    }

    @OnTextChanged(R.id.new_password_edit_text)
    void onNewPasswordTextChanged(CharSequence text) {
        presenter.onNewPasswordTextChanged(text);
    }

    @OnTextChanged(R.id.confirm_password_edit_text)
    void onConfirmPasswordTextChanged(CharSequence text) {
        presenter.onConfirmPasswordTextChanged(text);
    }

    @Override
    public void onDestroy() {
        presenter.destroy();
        super.onDestroy();

    }

    @Override
    public void showShowHideOldPasswordImageView() {
        showHideOldPasswordImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideShowHideOldPasswordImageView() {
        showHideOldPasswordImageView.setVisibility(View.GONE);
    }

    @Override
    public void hideOldPassword() {
        oldPasswordEditText.setTransformationMethod(new PasswordTransformationMethod());
        oldPasswordEditText.setSelection(oldPasswordEditText.getText().length());
    }

    @Override
    public void showOldPassword() {
        oldPasswordEditText.setTransformationMethod(null);
        oldPasswordEditText.setSelection(oldPasswordEditText.getText().length());
    }

    @Override
    public void hideNewPassword() {
        newPasswordEditText.setTransformationMethod(new PasswordTransformationMethod());
        newPasswordEditText.setSelection(newPasswordEditText.getText().length());
    }

    @Override
    public void hideShowHideNewPasswordImageView() {
        showHideNewPasswordImageView.setVisibility(View.GONE);
    }

    @Override
    public void showNewPassword() {
        newPasswordEditText.setTransformationMethod(null);
        newPasswordEditText.setSelection(newPasswordEditText.getText().length());
    }

    @Override
    public void showShowHideNewPasswordImageView() {
        showHideNewPasswordImageView.setVisibility(View.VISIBLE);
        newPasswordEditText.setSelection(newPasswordEditText.getText().length());
    }

    @Override
    public void hideConfirmPassword() {
        confirmPasswordEditText.setTransformationMethod(new PasswordTransformationMethod());
        newPasswordEditText.setSelection(newPasswordEditText.getText().length());
    }

    @Override
    public void hideShowHideConfirmPasswordImageView() {
        showHideConfirmPasswordImageView.setVisibility(View.GONE);
    }

    @Override
    public void showConfirmPassword() {
        confirmPasswordEditText.setTransformationMethod(null);

        newPasswordEditText.setSelection(newPasswordEditText.getText().length());
    }

    @Override
    public void showShowHideConfirmPasswordImageView() {
        showHideConfirmPasswordImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showOldPasswordIcon(@DrawableRes int id) {
        lockUnlockOldPasswordImageView.setImageResource(id);
    }

    @Override
    public void showNewPasswordIcon(@DrawableRes int id) {
        lockUnlockNewPasswordImageView.setImageResource(id);
    }

    @Override
    public void showConfirmPasswordIcon(@DrawableRes int id) {
        lockUnlockConfirmPasswordImageView.setImageResource(id);
    }

    @Override
    public String getConfirmPassword() {
        return confirmPasswordEditText.getText().toString().trim();
    }

    @Override
    public String getNewPassword() {
        return newPasswordEditText.getText().toString().trim();
    }

    @Override
    public String getOldPassword() {
        return oldPasswordEditText.getText().toString().trim();
    }

    @Override
    public void setOldPassword(String oldPassword) {
        oldPasswordEditText.setText(oldPassword);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    public Context context() {
        return getActivity();
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
    public void showLoginScreen() {
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().setResult(Activity.RESULT_CANCELED);
        getActivity().finish();
    }

    @Override
    public void updateToggleOldPasswordIcon(@DrawableRes int drawableRes) {
        showHideOldPasswordImageView.setImageResource(drawableRes);
    }

    @Override
    public void updateToggleConfirmPasswordIcon(@DrawableRes int drawableRes) {
        showHideConfirmPasswordImageView.setImageResource(drawableRes);
    }

    @Override
    public void updateToggleNewPasswordIcon(@DrawableRes int drawableRes) {
        showHideNewPasswordImageView.setImageResource(drawableRes);
    }

}
