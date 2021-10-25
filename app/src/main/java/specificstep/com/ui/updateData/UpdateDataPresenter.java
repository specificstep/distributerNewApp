package specificstep.com.ui.updateData;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.google.common.base.Strings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import specificstep.com.Database.DatabaseHelper;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.Models.User;
import specificstep.com.Models.UserList;
import specificstep.com.R;
import specificstep.com.data.exceptions.InvalidAccessTokenException;
import specificstep.com.data.source.local.Pref;
import specificstep.com.data.utils.UserType;
import specificstep.com.interactors.DefaultObserver;
import specificstep.com.interactors.usecases.GetChildUserUseCase;
import specificstep.com.utility.NotificationUtil;

class UpdateDataPresenter implements UpdateDataContract.Presenter {

    private final UpdateDataContract.View view;
    private final Pref pref;
    private final DatabaseHelper databaseHelper;
    private final GetChildUserUseCase childUserUseCase;
    private final NotificationUtil notificationUtil;
    private boolean forceUpdate;
    private ArrayList<User> userArrayList;

    @Inject
    UpdateDataPresenter(UpdateDataContract.View view, Pref pref, DatabaseHelper databaseHelper, GetChildUserUseCase childUserUseCase, NotificationUtil notificationUtil) {
        this.view = view;
        this.pref = pref;
        this.databaseHelper = databaseHelper;
        this.childUserUseCase = childUserUseCase;
        this.notificationUtil = notificationUtil;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {
        childUserUseCase.dispose();
    }

    @Override
    public void initialize(FragmentActivity activity, boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
        updateLastUpdatedDate();
        if (forceUpdate) {
            onUpdateDataButtonClicked(activity);
        }
    }

    private void updateLastUpdatedDate() {
        long lastUpdateMillis = pref.getValue(Pref.KEY_LAST_UPDATE_DATE, 0L);
        if (lastUpdateMillis != 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT, Locale.getDefault());
            view.showLastUpdatedDate(simpleDateFormat.format(new Date(lastUpdateMillis)));
        } else {
            view.hideLastUpdatedDateView();
        }
    }

    @Override
    public void onUpdateDataButtonClicked(FragmentActivity activity) {
        view.hideUpdateDataButton();
        view.showProgressBar();
        view.showStatusBar();
        view.hideLastUpdateDateView();
        view.updateProgress(0);
        databaseHelper.deleteUserListDetail();
        view.disableDrawer();
        fetchUpdate(activity);
    }

    private void fetchUpdate(final FragmentActivity activity) {
        userArrayList = databaseHelper.getUserDetail();
        System.out.println("userName: " + userArrayList.get(0).getUser_name() + " userType: " + UserType.DISTRIBUTOR.getType());
        view.updateStatusText(view.context().getString(R.string.status_updating));
        childUserUseCase.execute(new DefaultObserver<List<UserList>>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onNext(List<UserList> value) {
                super.onNext(value);
                onGetChildSuccess(activity,value);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if(e instanceof InvalidAccessTokenException) {
                    view.showInvalidAccessTokenPopup();
                    return;
                }
                onGetChildError(e);
            }
        }, GetChildUserUseCase.Params.toParams(userArrayList.get(0).getUser_name(), UserType.DISTRIBUTOR.getType()));
    }

    private void onGetChildError(Throwable e) {
        String message;
        if (Strings.isNullOrEmpty(e.getMessage())) {
            message = "";
        } else {
            message = e.getMessage().toLowerCase().trim();
        }

        updateLastUpdatedDate();
        view.showHomeButton();
        view.updateStatusText(view.context().getString(R.string.status_update_failed_format, message));

        view.showErrorDialog(e.getMessage());
        view.showUpdateDataButton();
        view.hideProgressBar();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void onGetChildSuccess(FragmentActivity activity, List<UserList> userLists) {
        view.updateProgress(100);
        notificationUtil.sendNotification(activity,view.context().getString(R.string.update), view.context().getString(R.string.data_update_success));
        pref.setValue(Pref.KEY_LAST_UPDATE_DATE, System.currentTimeMillis());
        updateLastUpdatedDate();
        view.updateStatusText(view.context().getString(R.string.status_update_completed));
        view.showHomeButton();
        if (forceUpdate) {
            view.goBack();
        }
        view.enableDrawer();
    }

    @Override
    public void onHomeButtonClicked() {
        view.goBack();
    }

    @Override
    public void onLogoutButtonClicked() {
        pref.clearPref();
        view.showSignInScreen();
    }
}
