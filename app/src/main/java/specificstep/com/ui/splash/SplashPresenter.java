package specificstep.com.ui.splash;

import androidx.fragment.app.FragmentActivity;

import android.util.Log;

import com.google.common.base.Strings;
import com.google.firebase.iid.FirebaseInstanceId;

import javax.inject.Inject;

import specificstep.com.Database.NotificationTable;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.Models.NotificationModel;
import specificstep.com.R;
import specificstep.com.data.source.local.Pref;
import specificstep.com.utility.DateTime;


public class SplashPresenter implements SplashContract.Presenter {

    private static final int MAX_RETRY = 7;
    private final SplashContract.View view;
    private final Pref pref;
    private final NotificationTable notificationTable;
    private int noOfRetry;

    @Inject
    SplashPresenter(SplashContract.View view, Pref pref, NotificationTable notificationTable) {
        this.view = view;
        this.pref = pref;
        this.notificationTable = notificationTable;
    }

    @Inject
    void setupListeners() {
        view.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void initialize(FragmentActivity activity) {
        view.scheduleTimeout(2000);
        if (pref.getValue(Pref.KEY_IS_FIRST_TIME, true)) {
            pref.setValue(Pref.KEY_IS_FIRST_TIME, false);
            addFirstTimeNotification(activity);
            view.createFirstTimeNotification();
        }
    }

    private void addFirstTimeNotification(FragmentActivity activity) {
        NotificationModel model = new NotificationModel();
        model.title = Constants.changeAppName(activity);
        model.message = view.context().getString(R.string.app_installed_success);
        model.receiveDateTime = DateTime.getCurrentDateTime();
        model.saveDateTime = DateTime.getCurrentDateTime();
        model.readFlag = "0";
        model.readDateTime = "";
        Log.d("SplashScreen", "Notification = " + "title : " + model.title + "Message : " + model.message);
        notificationTable.addNotificationData(model);
    }

    @Override
    public void onTimeoutCompleted() {
        if (hasFirebaseToken()) {
            if (pref.getValue(Pref.KEY_IS_LOGGED_IN, false)) {
                //Logged In
                view.startMainScreen();
            } else if (pref.getValue(Pref.KEY_IS_OTP_VERIFIED, false)) {
                //OTP verified
                view.startLoginScreen();
            } else {
                view.startSignUpScreen();
            }
        }
    }

    private boolean hasFirebaseToken() {
        if (!Strings.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken())) {
            return true;
        }
        if (noOfRetry == MAX_RETRY) {
            showInternetConnectionError();
        } else {
            noOfRetry++;
            view.scheduleTimeout(2000);
            FirebaseInstanceId.getInstance().getToken();
        }
        return false;
    }

    private void showInternetConnectionError() {
        view.showErrorDialog(R.string.message_no_intenet);
    }
}
