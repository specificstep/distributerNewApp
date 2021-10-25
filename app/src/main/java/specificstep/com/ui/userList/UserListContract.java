package specificstep.com.ui.userList;

import android.content.Context;

import java.util.List;

import specificstep.com.Models.ChildUserModel;
import specificstep.com.ui.base.BasePresenter;

public interface UserListContract {

    interface Presenter extends BasePresenter {

        void initialize();

        void onResetButtonClicked();

        void onSearchButtonClicked();

        void onMinAmountSelected(int position);

        void onRefreshMenuClicked();

        void onSelectedUser(ChildUserModel childUserModel);

        void onViewDestroy();
    }

    interface View {
        void hideLoadingView();
        void showLoadingView();

        void showSearchContainer();

        void showSearchButton();

        void hideResetContainer();

        Context context();

        void setMinAmountAdapter(String[] data);

        void setMaxAmountAdapter(String[] data);

        void setSortingAdapter(String[] data);

        void selectMaxValueByPosition(int selectedMaxIndex);

        String getSelectedMinValue();

        String getSelectedMaxValue();

        String getSelectedSortingValue();

        void hideUserListView();

        void showEmptyView();

        void showUserListView();

        void hideEmptyView();

        void setUserListAdapter(List<ChildUserModel> userModels);

        void setMinAmountText(String text);

        void setMaxAmountText(String text);

        void setSortingText(String text);

        void hideSearchContainer();

        void hideSearchButton();

        void showResetContainer();

        void showErrorDialog(String errorMessage);

        void showAddBalanceScreen(ChildUserModel childUserModel);

        void showInvalidAccessTokenPopup();
    }
}
