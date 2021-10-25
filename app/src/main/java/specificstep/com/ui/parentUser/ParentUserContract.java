package specificstep.com.ui.parentUser;


import android.content.Context;

import java.util.List;

import specificstep.com.Models.ParentUserModel;
import specificstep.com.ui.base.BasePresenter;

public interface ParentUserContract {

    interface Presenter extends BasePresenter {

        void initialize();
    }

    interface View {

        void showLoadingView();

        void hideLoadingView();

        Context context();

        void showInfoDialog(String errorMessage);

        void setUpAdapter(List<ParentUserModel> parentUsers);

        void setFirmName(String firmName);

        void setName(String name);

        void setMobileNumber(String mobile);

        void setUserType(String userType);

        void showListContainer();

        void hideListContainer();

        void hideNoDataLabel();

        void showNoDataLabel();

        void showInvalidAccessTokenPopup();
    }
}
