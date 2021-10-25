package specificstep.com.ui.splash;

import android.content.Context;

import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;

import specificstep.com.ui.base.BasePresenter;
import specificstep.com.ui.base.BaseView;

public interface SplashContract {

    interface Presenter extends BasePresenter {

        void initialize(FragmentActivity activity);

        void onTimeoutCompleted();
    }

    interface View extends BaseView<Presenter> {

        void scheduleTimeout(long milliSeconds);

        void startMainScreen();

        void startLoginScreen();

        void startSignUpScreen();

        Context context();

        void createFirstTimeNotification();

        void showErrorDialog(@StringRes int strResId);
    }
}
