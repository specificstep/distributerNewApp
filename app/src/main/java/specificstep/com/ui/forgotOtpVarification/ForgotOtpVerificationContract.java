package specificstep.com.ui.forgotOtpVarification;

import android.content.Context;

import specificstep.com.ui.base.BasePresenter;
import specificstep.com.ui.base.BaseView;

public interface ForgotOtpVerificationContract {

    interface Presenter extends BasePresenter {

        void onVerifyOtpButtonClicked(Context context, String forgot_otp);

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

        void showForgotPasswordScreen(String userName, String password);
    }

}
