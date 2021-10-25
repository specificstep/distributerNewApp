package specificstep.com.ui.otpVerification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import specificstep.com.GlobalClasses.TransparentProgressDialog;
import specificstep.com.R;
import specificstep.com.Sms.SmsReceiver;
import specificstep.com.ui.base.BaseFragment;
import specificstep.com.ui.signIn.SignInActivity;

import static specificstep.com.ui.otpVerification.OtpVerificationActivity.EXTRA_USERNAME;

public class OtpVerificationFragment extends BaseFragment implements OtpVerificationContract.View {

    private static final int PERMISSION_REQUEST_CODE = 101;
    @BindView(R.id.otp_edit_text)
    EditText otpEditText;
    @BindView(R.id.timer_text_view)
    TextView timerTextView;
    @BindView(R.id.resend_otp_button)
    Button resendOtpButton;
    private OtpVerificationContract.Presenter presenter;
    private SmsReceiver messageReadReceiver;
    private String userName;
    private TransparentProgressDialog transparentProgressDialog;

    public static OtpVerificationFragment getInstance(String userName) {
        OtpVerificationFragment fragment = new OtpVerificationFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_USERNAME, userName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setPresenter(OtpVerificationContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_otp_verification, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

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
        if (bundle.containsKey(EXTRA_USERNAME)) {
            userName = bundle.getString(EXTRA_USERNAME, "");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_USERNAME, userName);
    }

    @OnClick({R.id.verify_otp_button, R.id.resend_otp_button})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.verify_otp_button:
                presenter.onVerifyOtpButtonClicked();
                break;
            case R.id.resend_otp_button:
                presenter.onResendOtpButtonClicked();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.stop();
    }

    @Override
    public void onDestroy() {
        presenter.destroy();
        super.onDestroy();
    }

    @Override
    public void fillOtp(String otp) {
        otpEditText.setText(otp);
    }

    @Override
    public String getUserName() {
        return userName;
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
    public void hideProgressIndicator() {
        if (transparentProgressDialog != null) {
            transparentProgressDialog.dismiss();
            transparentProgressDialog = null;
        }
    }

    @Override
    public void enableOtpEditText() {
        otpEditText.setEnabled(true);
    }

    @Override
    public void disableResendOtpButton() {
        resendOtpButton.setEnabled(false);
    }

    @Override
    public Context context() {
        return getActivity();
    }

    @Override
    public void showErrorDialog(String errorMessage) {
        showDialog(getString(R.string.info), errorMessage);
    }

    @Override
    public void hideCountDownTimer() {
        timerTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void enableResendButton() {
        resendOtpButton.setEnabled(true);
    }

    @Override
    public void disableOtpEditText() {
        otpEditText.setEnabled(false);
    }

    @Override
    public void showCountDownTimer() {
        timerTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public String getOtp() {
        return otpEditText.getText().toString().trim();
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        Snackbar.make(getView(), errorMessage, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void updateCountDownTime(String timeFormat) {
        timerTextView.setText(timeFormat);
    }

    @Override
    public void showLoginScreen(String userName) {
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        intent.putExtra(SignInActivity.EXTRA_USER_NAME, userName);
        startActivity(intent);
        getActivity().finish();
    }
}
