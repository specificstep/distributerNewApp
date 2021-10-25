package specificstep.com.ui.cashSummary;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import specificstep.com.Database.DatabaseHelper;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.Models.CashSummaryModel;
import specificstep.com.Models.User;
import specificstep.com.Models.UserList;
import specificstep.com.R;
import specificstep.com.data.exceptions.InvalidAccessTokenException;
import specificstep.com.data.source.local.Pref;
import specificstep.com.data.utils.UserType;
import specificstep.com.exceptions.ErrorMessageFactory;
import specificstep.com.interactors.DefaultObserver;
import specificstep.com.interactors.exception.DefaultErrorBundle;
import specificstep.com.interactors.usecases.GetCashSummaryUseCase;

public class CashSummaryPresenter implements CashSummaryContract.Presenter {

    private final CashSummaryContract.View view;
    private final Pref pref;
    private final DatabaseHelper databaseHelper;
    private final SimpleDateFormat simpleDateFormat;
    private final GetCashSummaryUseCase cashSummaryUseCase;
    private Calendar fromDateCalendar, toDateCalendar;
    private ArrayList<UserList> userListArrayList;
    private StringBuilder stringBuilder = new StringBuilder();
    private ArrayList<User> userArrayList;

    @Inject
    CashSummaryPresenter(
            CashSummaryContract.View view,
            Pref pref,
            DatabaseHelper databaseHelper,
            GetCashSummaryUseCase cashSummaryUseCase) {
        this.view = view;
        this.pref = pref;
        this.databaseHelper = databaseHelper;
        this.cashSummaryUseCase = cashSummaryUseCase;
        simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
        fromDateCalendar = Calendar.getInstance();
        toDateCalendar = Calendar.getInstance();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {
        cashSummaryUseCase.dispose();
    }

    @Override
    public void initialize() {
        updateResellerRadioButtonVisibility();
        updateCurrentDate();
        updateFromDate();
        updateToDate();
    }

    private void updateToDate() {
        view.updateToDate(simpleDateFormat.format(toDateCalendar.getTime()));
    }

    private void updateFromDate() {
        view.updateFromDate(simpleDateFormat.format(fromDateCalendar.getTime()));
    }

    private void updateCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        view.updateDatePicker(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void updateResellerRadioButtonVisibility() {
        if (pref.getValue(Pref.KEY_USER_TYPE, 0) == UserType.RESELLER.getType()) {
            view.hideResellerRadioButton();
        } else {
            view.showResellerRadioButton();
        }
    }

    @Override
    public void onSearchButtonClicked(String userType) {
        if (checkValidations()) {
            getCashSummary(userType);
        }
    }

    private void getCashSummary(final String userType1) {
        SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fromDate = serverDateFormat.format(fromDateCalendar.getTime());
        String toDate = serverDateFormat.format(toDateCalendar.getTime());
        stringBuilder = new StringBuilder();
        stringBuilder.append(view.context().getString(R.string.from_label) + " " + simpleDateFormat.format(fromDateCalendar.getTime()) + "   |   ");
        stringBuilder.append(view.context().getString(R.string.to_label) + " " + simpleDateFormat.format(toDateCalendar.getTime()) + "\n");

        System.out.println("Selected User Type: " + CashSummaryFragment.selectedUserType);

        String selectedUserType = view.getSelectedUserType();
        int userType;
        boolean isAllSelected = view.context().getString(R.string.all).equals(selectedUserType);
        if (view.isResellerSelected()) {
            stringBuilder.append(view.context().getString(R.string.user_type_format, /*view.context().getString(R.string.reseller)*/CashSummaryFragment.selectedUserType) + "\n");
            if (isAllSelected) {
                stringBuilder.append(view.context().getString(R.string.user_all));
            }
            userType = UserType.RESELLER.getType();
        } else if (view.isDealerSelected()) {
            stringBuilder.append(view.context().getString(R.string.user_type_format, /*view.context().getString(R.string.dealer)*/CashSummaryFragment.selectedUserType) + "\n");
            if (isAllSelected) {
                stringBuilder.append(view.context().getString(R.string.user_all));
            }
            userType = UserType.DEALER.getType();
        } else if (view.isRetailerSelected()) {
            stringBuilder.append(view.context().getString(R.string.user_type_format, /*view.context().getString(R.string.retailer)*/CashSummaryFragment.selectedUserType) + "\n");
            if (isAllSelected) {
                stringBuilder.append(view.context().getString(R.string.user_all));
            }
            userType = UserType.RETAILER.getType();
        } else {
            stringBuilder.append(view.context().getString(R.string.user_type_format, /*view.context().getString(R.string.self)*/CashSummaryFragment.selectedUserType) + "\n");
            if (isAllSelected) {
                stringBuilder.append(view.context().getString(R.string.user_self));
            }
            userType = UserType.SELF.getType();
        }
        String selectedUserId = "";
        int selectedUserTypePosition = 0;
        if(userType != 0) {
            selectedUserTypePosition = view.getSelectedUserTypePosition();
//        if(userListArrayList.size() <= selectedUserTypePosition) {
//            return;
//        }
                userListArrayList = databaseHelper.getUserListDetailsByType(userType1);
                List<UserList> data = new ArrayList<UserList>();
                for (UserList userList : userListArrayList) {
                    if (userList.getParentUserId().equals(userArrayList.get(0).getUser_id())) {
                        data.add(userList);
                    }
                }
                if (isAllSelected) {
                    selectedUserId = selectedUserType;
                } else {
                    UserList userList = data.get(selectedUserTypePosition);
                    selectedUserId = userList.getUser_id();
                }
                System.out.println("Selected UserID: " + selectedUserId);

        } else {
            selectedUserId = "All";
        }
        if(userType != 0) {
            stringBuilder.append(view.context().getString(R.string.user_format, selectedUserType));
        }

        view.showProgressIndicator();

            cashSummaryUseCase.execute(
                    new DefaultObserver<List<CashSummaryModel>>() {
                        @Override
                        public void onNext(List<CashSummaryModel> value) {
                            super.onNext(value);
                            onCashSummaryReceived(value, userType1);
                            view.hideProgressIndicator();
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            view.hideProgressIndicator();
                            if (e instanceof InvalidAccessTokenException) {
                                view.showInvalidAccessTokenPopup();
                                return;
                            }
                            showErrorMessage(new DefaultErrorBundle((Exception) e));
                        }
                    },
                    GetCashSummaryUseCase.Params.toParams(selectedUserId, userType, fromDate, toDate));

    }

    private void showErrorMessage(DefaultErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(this.view.context(),
                errorBundle.getException());
        this.view.showErrorDialog(errorMessage);
    }

    private void onCashSummaryReceived(List<CashSummaryModel> summaryModels, String userType) {
        view.showSearchResultContainer();
        view.setListAdapter(summaryModels,userType);
        view.updateDetailText(stringBuilder.toString());
        view.showResetViewContainer();
        view.hideSearchWidgetContainer();
    }

    private boolean checkValidations() {
        if (fromDateCalendar.get(Calendar.MONTH) != toDateCalendar.get(Calendar.MONTH)) {
            view.showToastMessage(view.context().getString(R.string.message_select_same_months));
            return false;
        }
        return true;
    }

    @Override
    public void onResetButtonClicked() {
        view.showSearchWidgetContainer();
        view.hideResetViewContainer();
    }

    @Override
    public void onFromDateEditTextClicked() {
        view.showDatePickerForFromDate(fromDateCalendar);
    }

    @Override
    public void onToDateEditTextClicked() {
        Calendar currentCalendar = Calendar.getInstance();
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int currentDayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);

        currentCalendar.set(Calendar.MONTH, fromDateCalendar.get(Calendar.MONTH));

        int monthOfYear = fromDateCalendar.get(Calendar.MONTH);
        int lastDayOfMonth = fromDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (monthOfYear == currentMonth) {
            if (currentDayOfMonth < lastDayOfMonth) {
                // if current date is less then last day of month then set current date in to calender
                currentCalendar.set(Calendar.DAY_OF_MONTH, currentDayOfMonth);
            } else {
                // else set last day of month in to calender
                currentCalendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
            }
        } else {
            currentCalendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
        }

        long maxTimeMillis = currentCalendar.getTimeInMillis();
        view.showToDateDatePickerDialog(fromDateCalendar, maxTimeMillis);

    }

    @Override
    public void onFromDateSelected(int year, int month, int dayOfMonth) {
        fromDateCalendar.set(year, month, dayOfMonth);
        updateFromDate();
        toDateCalendar.set(Calendar.YEAR, year);
        toDateCalendar.set(Calendar.MONTH, month);

        int lastDayOfMonth = fromDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);


        Calendar currentCalendar = Calendar.getInstance();
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int currentDayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);
        if (currentMonth == month) {
            if (currentDayOfMonth < lastDayOfMonth) {
                // if current date is less then last day of month then set current date in to calender
                toDateCalendar.set(Calendar.DAY_OF_MONTH, currentDayOfMonth);
            } else {
                // else set last day of month in to calender
                toDateCalendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
            }
        } else {
            toDateCalendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
        }
        updateToDate();
    }

    @Override
    public void onToDateSelected(int year, int month, int dayOfMonth) {
        toDateCalendar.set(year, month, dayOfMonth);
        updateToDate();
    }

    @Override
    public void onResellerUserRadioButtonChecked(String userType) {
        List<UserList> userListArrayList = databaseHelper.getUserListDetailsByType(userType);
        view.showUserListSpinner();
        prepareUserListSpinnerData(userListArrayList);
    }

    @Override
    public void onDealerUserRadioButtonChecked(String userType) {
        List<UserList> userListArrayList = databaseHelper.getUserListDetailsByType(userType);
        view.showUserListSpinner();
        prepareUserListSpinnerData(userListArrayList);
    }

    @Override
    public void onRetailerUserRadioButtonChecked(String userType) {
        userListArrayList = databaseHelper.getUserListDetailsByType(userType);
        view.showUserListSpinner();
        prepareUserListSpinnerData(userListArrayList);
    }

    private void prepareUserListSpinnerData(List<UserList> userListArrayList) {
        userArrayList = databaseHelper.getUserDetail();
        List<String> data = new ArrayList<>();
        //data.add(view.context().getString(R.string.all));
        for (UserList userList : userListArrayList) {
            System.out.println("Login UserId: " + userArrayList.get(0).getUser_id());
            System.out.println("Parent UserId: " + userList.getParentUserId());
            if(userList.getParentUserId().equals(userArrayList.get(0).getUser_id())) {
                data.add(userList.getUser_name() + " - " + userList.getPhone_no());
            }
        }
        view.setUserSpinnerAdapter(data);
    }

    @Override
    public void onSelfRadioButtonChecked() {
        userListArrayList = databaseHelper.getUserListDetailsById();
        System.out.println("User Array: " + userListArrayList);
        view.hideUserListSpinner();
        prepareUserListSpinnerData(userListArrayList);
    }

    @Override
    public void reloadCashSummary(String userType) {
        getCashSummary(userType);
    }
}
