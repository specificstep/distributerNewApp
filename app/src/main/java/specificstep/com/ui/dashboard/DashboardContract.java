package specificstep.com.ui.dashboard;

import android.content.Context;

import java.math.BigDecimal;

import specificstep.com.ui.base.BasePresenter;
import specificstep.com.ui.base.BaseView;
import specificstep.com.ui.home.Flow;

interface DashboardContract {
    interface Presenter extends BasePresenter {

        void onAddRechargeButtonClicked();


        void onAcLedgerButtonClicked();



        void onAddUserButtonClicked();

        void onPurchaseUserButtonClicked();

        void onListUserButtonClicked();

        void onSearchTransactionButtonClicked();

        void onUpdateButtonClicked();

        void onChangePasswordButtonClicked();

        void onNotificationButtonClicked();

        void refreshBalance();

        void onLogoutButtonClicked();

        void initialize();

        void onNotificationRefreshed();
    }

    interface View extends BaseView<Presenter> {

        void showMainScreen(@Flow int update);

        void showLoginScreen();

        void registerNotificationReceiver(String action);

        void unRegisterNotificationReceiver();

        void updateNotificationCount(int notificationCount);

        void updateNotificationBadgeVisibility(boolean visible);

        void showBalanceInMenu(BigDecimal amount);

        Context context();

        void showErrorDialog(String errorMessage);

        void showInvalidAccessTokenPopup();

        void showAutoUpdateScreen(@Flow int update);
    }
}
