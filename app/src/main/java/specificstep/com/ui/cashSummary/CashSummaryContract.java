package specificstep.com.ui.cashSummary;

import android.content.Context;

import java.util.Calendar;
import java.util.List;

import specificstep.com.Models.CashSummaryModel;
import specificstep.com.ui.base.BasePresenter;

public interface CashSummaryContract {

    interface Presenter extends BasePresenter {

        void initialize();

        void onSearchButtonClicked(String userType);

        void onResetButtonClicked();

        void onFromDateEditTextClicked();

        void onToDateEditTextClicked();

        void onFromDateSelected(int year, int month, int dayOfMonth);

        void onToDateSelected(int year, int month, int dayOfMonth);

        void onResellerUserRadioButtonChecked(String userType);

        void onDealerUserRadioButtonChecked(String userType);

        void onRetailerUserRadioButtonChecked(String userType);

        void onSelfRadioButtonChecked();

        void reloadCashSummary(String userType);

    }

    interface View {

        void hideResellerRadioButton();

        void showResellerRadioButton();

        void updateDatePicker(int year, int month, int dayOfMonth);

        void updateFromDate(String strDate);

        void updateToDate(String strDate);

        void showDatePickerForFromDate(Calendar calendar);

        void showToDateDatePickerDialog(Calendar calendar, long maxTimeMillis);

        void hideUserListSpinner();

        void showUserListSpinner();

        Context context();

        void setUserSpinnerAdapter(List<String> data);

        void showSearchWidgetContainer();

        void hideResetViewContainer();

        void showToastMessage(String msg);

        String getSelectedUserType();

        boolean isResellerSelected();

        boolean isDealerSelected();

        boolean isRetailerSelected();

        int getSelectedUserTypePosition();

        void setListAdapter(List<CashSummaryModel> summaryModels, String userType);

        void updateDetailText(String text);

        void showResetViewContainer();

        void hideSearchWidgetContainer();

        void showSearchResultContainer();

        void showProgressIndicator();

        void showErrorDialog(String message);

        void hideProgressIndicator();

        void showInvalidAccessTokenPopup();
    }
}
