package specificstep.com.ui.home;

import android.text.TextUtils;

import java.math.BigDecimal;

import javax.inject.Inject;

import specificstep.com.Database.DatabaseHelper;
import specificstep.com.Database.NotificationTable;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.Models.User;
import specificstep.com.data.exceptions.InvalidAccessTokenException;
import specificstep.com.data.source.local.Pref;
import specificstep.com.exceptions.ErrorMessageFactory;
import specificstep.com.interactors.DefaultObserver;
import specificstep.com.interactors.exception.DefaultErrorBundle;
import specificstep.com.interactors.usecases.GetBalanceUseCase;

class HomePresenter implements HomeContract.Presenter {

    private HomeContract.View view;
    private Pref pref;
    private DatabaseHelper databaseHelper;
    private NotificationTable notificationTable;
    private GetBalanceUseCase getBalanceUseCase;

    @Inject
    public HomePresenter(HomeContract.View view,
                         Pref pref,
                         DatabaseHelper databaseHelper,
                         NotificationTable notificationTable,
                         GetBalanceUseCase getBalanceUseCase) {
        this.view = view;
        this.pref = pref;
        this.databaseHelper = databaseHelper;
        this.notificationTable = notificationTable;
        this.getBalanceUseCase = getBalanceUseCase;
    }

    @Override
    public void start() {
        registerNotificationCounterReceiver();
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
        getBalanceUseCase.dispose();
    }

    private void showErrorMessage(DefaultErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(this.view.context(),
                errorBundle.getException());
        this.view.showErrorDialog(errorMessage);
    }


    @Override
    public void initWithFlow(int flow) {
        fetchUserInfo();
        openRelatedScreen(flow);
        fetchNotificationCount();
    }

    private void fetchUserInfo() {
        User user = databaseHelper.getUserDetails();
        String mobile = pref.getValue(Pref.KEY_MOBILE, user.getUser_name());
        System.out.println("User Name: " + user.getName() + " Mobile No: " + mobile);
        view.updateNavigationHeader(mobile, user.getName());
    }

    @Override
    public void onNotificationRefreshed() {
        fetchNotificationCount();
    }

    @Override
    public void onAddBalanceSuccess() {
//        TODO Handle the flow
    }

    private void fetchNotificationCount() {
        int notificationCount = notificationTable.getNumberOfNotificationRecord();
        view.updateNotificationCount(notificationCount);
        view.updateNotificationBadgeVisibility(notificationCount > 0);
    }


    @Override
    public void fetchBalance() {
        String cachedBalance = pref.getValue(Pref.KEY_BALANCE, "");
        BigDecimal balance = null;
        if(!TextUtils.isEmpty(cachedBalance)) {
            balance = new BigDecimal(cachedBalance);
        }

        if(balance != null) {
            view.showBalance(balance);
        }
        getBalanceUseCase.execute(new DefaultObserver<BigDecimal>() {
            @Override
            public void onNext(BigDecimal value) {
                super.onNext(value);
                view.showBalance(value);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if(e instanceof InvalidAccessTokenException) {
                    view.showInvalidAccessTokenPopup();
                    return;
                }
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }
        }, null);
    }

    @Override
    public void onConfirmLogoutButtonClicked() {
        view.logoutUser();
        pref.removeValues(Pref.KEY_IS_LOGGED_IN);
    }

    @Override
    public void onLogoutButtonClicked() {
        view.showLogoutConfirmationPopup();
    }

    private void openRelatedScreen(@Flow int flow) {
        switch (flow) {
            case Flow.ADD_BALANCE:
                view.showAddBalanceScreen();
                view.selectMenuItemForFlow(Flow.ADD_BALANCE);
                break;
            case Flow.CASH_SUMMARY:
                view.showCashSummaryScreen();
                view.selectMenuItemForFlow(Flow.CASH_SUMMARY);
                break;
            case Flow.CASHBOOK:
                view.showCashBookScreen();
                break;

            case Flow.PURCHASE_USER:
                view.showPurchaseUser();
                break;

            case Flow.ADD_USER:
                view.showAddUser();
                break;

            case Flow.CHANGE_PASSWORD:
                view.showChangePasswordScreen();
                view.selectMenuItemForFlow(Flow.CHANGE_PASSWORD);
                break;
            case Flow.NOTIFICATION:
                view.showNotificationScreen();
                view.selectMenuItemForFlow(Flow.NOTIFICATION);
                break;
            case Flow.UPDATE:
                view.showUpdateScreen();
                view.selectMenuItemForFlow(Flow.UPDATE);
                break;
            case Flow.USER_LIST:
                view.showUserListScreen();
                break;
        }
    }
}
