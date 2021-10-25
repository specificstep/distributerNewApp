package specificstep.com.ui.home;

import android.content.Context;

import java.math.BigDecimal;

import specificstep.com.Models.ChildUserModel;
import specificstep.com.ui.base.BasePresenter;

public interface HomeContract {

    interface Presenter extends BasePresenter {

        void initWithFlow(int flow);

        void fetchBalance();

        void onNotificationRefreshed();

        void onAddBalanceSuccess();

        void onLogoutButtonClicked();

        void onConfirmLogoutButtonClicked();
    }

    interface View {

        void showAddBalanceScreen();

        void showCashSummaryScreen();

        void showCashBookScreen();

        void showAddUser();

        void showPurchaseUser();

        void showChangePasswordScreen();

        void showDMTTransactionListScreen();

        void showNotificationScreen();

        void showUpdateScreen();

        void showUserListScreen();

        void updateNavigationHeader(String userName, String name);

        void unRegisterNotificationReceiver();

        void registerNotificationReceiver(String action);

        Context context();

        void showErrorDialog(String errorMessage);

        void updateNotificationCount(int notificationCount);

        void updateNotificationBadgeVisibility(boolean isVisible);

        void showBalance(BigDecimal balance);

        void showInvalidAccessTokenPopup();

        void showLogoutConfirmationPopup();

        void logoutUser();

        void selectMenuItemForFlow(@Flow int flow);

        void showPaymentRequestScreen();
    }

    interface HomeDelegate {

        void onAddBalanceCompleted();

        void showHomeScreen();

        void disableDrawer();

        void enableDrawer();

        void updateNotificationCount(int count);

        void showAddBalanceScreenForUser(ChildUserModel childUserModel);

        void setToolBarTitle(String title);
    }
}
