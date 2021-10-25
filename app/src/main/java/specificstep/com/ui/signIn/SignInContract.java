package specificstep.com.ui.signIn;

import android.content.Context;

import androidx.annotation.DrawableRes;

import specificstep.com.ui.base.BasePresenter;
import specificstep.com.ui.base.BaseView;

public interface SignInContract {

    interface Presenter extends BasePresenter {

        void onPasswordChanged(String password);

        void onLoginButtonClicked(Context context);

        void onRememberMeToggleChanged(boolean checked);

        void onPasswordToggleVisible();

        void initialize(String userName);

        void onMaxRetryPopupOkButtonClicked();

    }

    interface View extends BaseView<Presenter> {

        void showPasswordVisibilityIcon(@DrawableRes int drawableResId);

        void showPassword();

        void hidePassword();

        void hidePasswordToggle();

        void showPasswordToggle();

        void showPasswordIcon(@DrawableRes int drawableResId);

        void showConfirmRememberPopup();

        String getUserName();

        String getPassword();

        void showErrorMessage(String message);

        Context context();

        boolean isRememberPassword();

        void hideProgressIndicator();

        void showErrorDialog(String errorMessage);

        void showMainScreen();

        void setProgressIndicator();

        void autoFillUserName(String userName);

        void showRemainingSignInRetryPopup(int remainingRetry);

        void showSignUpScreen();

        void showMaxLoginRetryPopup(int maxRetry);

        void selectRememberPasswordCheckBox();

        void autoFillPassword(String password);
    }
}
