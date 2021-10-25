package specificstep.com.ui.dashboard;

import android.content.Context;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import specificstep.com.Database.DatabaseHelper;
import specificstep.com.Database.NotificationTable;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.Models.User;
import specificstep.com.Models.UserList;
import specificstep.com.data.exceptions.InvalidAccessTokenException;
import specificstep.com.data.source.local.Pref;
import specificstep.com.data.utils.UserType;
import specificstep.com.exceptions.ErrorMessageFactory;
import specificstep.com.interactors.DefaultObserver;
import specificstep.com.interactors.exception.DefaultErrorBundle;
import specificstep.com.interactors.usecases.GetBalanceUseCase;
import specificstep.com.interactors.usecases.GetChildUserUseCase;
import specificstep.com.ui.home.Flow;

public class DashboardPresenter implements DashboardContract.Presenter {

    private final DashboardContract.View view;
    private final Pref pref;
    private final NotificationTable notificationTable;
    private final GetBalanceUseCase getBalanceUseCase;
    private final GetChildUserUseCase childUserUseCase;
    private BigDecimal balance;
    private DatabaseHelper databaseHelper;
    private ArrayList<User> userArrayList;

    @Inject
    public DashboardPresenter(DashboardContract.View view,
                              Pref pref,
                              NotificationTable notificationTable,
                              GetBalanceUseCase getBalanceUseCase, GetChildUserUseCase childUserUseCase) {
        this.view = view;
        this.pref = pref;
        this.notificationTable = notificationTable;
        this.getBalanceUseCase = getBalanceUseCase;
        this.childUserUseCase = childUserUseCase;
    }

    @Inject
    public void setupListeners() {
        view.setPresenter(this);
    }

    @Override
    public void start() {
        registerNotificationCounterReceiver();
        fetchNotificationCount();
        refreshBalance();
    }

    @Override
    public void stop() {
        unRegisterNotificationCounterReceiver();
    }

    private void unRegisterNotificationCounterReceiver() {
        view.unRegisterNotificationReceiver();
    }

    private void registerNotificationCounterReceiver() {
        view.registerNotificationReceiver(Constants.ACTION_NOTIFICATION_UPDATE);
    }

    @Override
    public void destroy() {
        childUserUseCase.dispose();
        getBalanceUseCase.dispose();
    }

    @Override
    public void onAddRechargeButtonClicked() {
        view.showMainScreen(Flow.ADD_BALANCE);
    }

    @Override
    public void onAddUserButtonClicked() {

    }

    @Override
    public void onPurchaseUserButtonClicked() {

    }

    @Override
    public void onAcLedgerButtonClicked() {
        view.showMainScreen(Flow.AC_LEDGER);
    }


    @Override
    public void onListUserButtonClicked() {
        view.showMainScreen(Flow.USER_LIST);
    }

    @Override
    public void onSearchTransactionButtonClicked() {
        view.showMainScreen(Flow.CASH_SUMMARY);
    }

    @Override
    public void onUpdateButtonClicked() {
        view.showMainScreen(Flow.UPDATE);
    }

    @Override
    public void onChangePasswordButtonClicked() {
        view.showMainScreen(Flow.CHANGE_PASSWORD);
    }

    @Override
    public void onNotificationButtonClicked() {
        view.showMainScreen(Flow.NOTIFICATION);
    }

    private void fetchUpdate(Context context) {
        databaseHelper = new DatabaseHelper(context);
        userArrayList = databaseHelper.getUserDetail();
        childUserUseCase.execute(new DefaultObserver<List<UserList>>() {
        }, GetChildUserUseCase.Params.toParams(userArrayList.get(0).getUser_name(), UserType.DISTRIBUTOR.getType()));
    }

    @Override
    public void refreshBalance() {
        String cachedBalance = pref.getValue(Pref.KEY_BALANCE, "");
        if (!TextUtils.isEmpty(cachedBalance)) {
            balance = new BigDecimal(cachedBalance);
        }

        if (balance != null) {
            view.showBalanceInMenu(balance);
        }

        getBalanceUseCase.execute(new DefaultObserver<BigDecimal>() {
            @Override
            public void onNext(BigDecimal value) {
                super.onNext(value);
                DashboardPresenter.this.balance = value;
                view.showBalanceInMenu(value);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (e instanceof InvalidAccessTokenException) {
                    view.showInvalidAccessTokenPopup();
                    return;
                }
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }
        }, null);
    }

    private void showErrorMessage(DefaultErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(this.view.context(),
                errorBundle.getException());
        this.view.showErrorDialog(errorMessage);
    }

    @Override
    public void onLogoutButtonClicked() {
        pref.removeValues(Pref.KEY_IS_LOGGED_IN);
        view.showLoginScreen();
    }

    @Override
    public void initialize() {
        checkForUpdate();
    }

    @Override
    public void onNotificationRefreshed() {
        fetchNotificationCount();
    }

    private void fetchNotificationCount() {
        int notificationCount = notificationTable.getNumberOfNotificationRecord();
        view.updateNotificationCount(notificationCount);
        view.updateNotificationBadgeVisibility(notificationCount > 0);
    }

    private void checkForUpdate() {
//        long lastUpdateMillis = pref.getValue(Pref.KEY_LAST_UPDATE_DATE, 0L);
//        String dateTime = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(lastUpdateMillis));
//        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//        if (lastUpdateMillis == 0 ||
//                !TextUtils.equals(dateTime, currentDate)) {
//            //First time or different day
//            view.showAutoUpdateScreen(Flow.UPDATE);
//        }

        fetchUpdate(view.context());
    }
}
