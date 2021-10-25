package specificstep.com.ui.signup;

import android.content.Context;

import specificstep.com.ui.base.BasePresenter;
import specificstep.com.ui.base.BaseView;

public interface SignUpContract {

    interface Presenter extends BasePresenter {

        void register(String userName);

        void makeAutoOtpCall(String userName);
    }

    interface View extends BaseView<Presenter> {

        void setProgressIndicator();

        boolean isActive();

        void showError(String errorMsg);

        Context context();

        void showInternetNotAvailableDialog();

        void hideProgressIndicator();

        void showErrorDialog(String errorMessage);

        void showVerifyRegistrationScreen(String userName);

        void showOtpScreen(String userName);

        void showLoginScreen(String userName);
    }

}
