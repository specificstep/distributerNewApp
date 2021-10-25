package specificstep.com.ui.updateData;

import android.content.Context;
import androidx.fragment.app.FragmentActivity;

import specificstep.com.ui.base.BasePresenter;

public interface UpdateDataContract {

    interface Presenter extends BasePresenter {

        void initialize(FragmentActivity activity, boolean forceUpdate);

        void onUpdateDataButtonClicked(FragmentActivity activity);

        void onHomeButtonClicked();

        void onLogoutButtonClicked();
    }

    interface View {

        void goBack();

        void showLastUpdatedDate(String strDate);

        void hideLastUpdatedDateView();

        void hideUpdateDataButton();

        void showProgressBar();

        void showStatusBar();

        void hideLastUpdateDateView();

        void updateProgress(int progress);

        void disableDrawer();

        Context context();

        void updateStatusText(String status);

        void showHomeButton();

        void enableDrawer();

        void showSignInScreen();

        void showErrorDialog(String errorMessage);

        void showUpdateDataButton();

        void hideProgressBar();

        void showInvalidAccessTokenPopup();
    }
}
