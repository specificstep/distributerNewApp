package specificstep.com.ui.otpVerification;

import android.content.Context;

import specificstep.com.ui.base.BasePresenter;
import specificstep.com.ui.base.BaseView;


public interface OtpVerificationContract {
    interface Presenter extends BasePresenter {

        void onVerifyOtpButtonClicked();

        void onResendOtpButtonClicked();
    }

    interface View extends BaseView<Presenter> {

        void fillOtp(String otp);

        String getUserName();

        void setProgressIndicator();

        void hideProgressIndicator();

        void enableOtpEditText();

        void disableResendOtpButton();

        Context context();

        void showErrorDialog(String errorMessage);

        void hideCountDownTimer();

        void enableResendButton();

        void disableOtpEditText();

        void showCountDownTimer();

        String getOtp();

        void showErrorMessage(String errorMessage);

        void updateCountDownTime(String timeFormat);

        void showLoginScreen(String userName);
    }
}
