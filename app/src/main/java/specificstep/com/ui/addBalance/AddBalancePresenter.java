package specificstep.com.ui.addBalance;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.text.TextUtils;

import com.google.common.base.Strings;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import specificstep.com.Database.ChildUserTable;
import specificstep.com.Database.DatabaseHelper;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.Models.ChildUserModel;
import specificstep.com.Models.User;
import specificstep.com.Models.UserList;
import specificstep.com.R;
import specificstep.com.data.exceptions.InvalidAccessTokenException;
import specificstep.com.data.source.local.Pref;
import specificstep.com.data.utils.UserType;
import specificstep.com.exceptions.ErrorMessageFactory;
import specificstep.com.interactors.DefaultObserver;
import specificstep.com.interactors.exception.DefaultErrorBundle;
import specificstep.com.interactors.usecases.AddBalanceUseCase;
import specificstep.com.interactors.usecases.GetBalanceUseCase;
import specificstep.com.interactors.usecases.GetChildUserUseCase;
import specificstep.com.interactors.usecases.GetUserUseCase;
import specificstep.com.utility.NotificationUtil;
import specificstep.com.utility.Utility;

class AddBalancePresenter implements AddBalanceContract.Presenter {

    private static final String TAG = AddBalancePresenter.class.getSimpleName();
    private final NotificationUtil notificationUtils;
    private final ChildUserTable childUserTable;
    private final Pref pref;
    private AddBalanceContract.View view;
    private DatabaseHelper databaseHelper;
    private GetBalanceUseCase getBalanceUseCase;
    private GetUserUseCase getUserUseCase;
    private final GetChildUserUseCase childUserUseCase;
    private AddBalanceUseCase addBalanceUseCase;
    private BigDecimal currentBalance;
    private ChildUserModel mChildUserModel;
    private CharSequence searchTerm;
    private ArrayList<User> userArrayList;

    @Inject
    AddBalancePresenter(AddBalanceContract.View view,
                        DatabaseHelper databaseHelper,
                        GetBalanceUseCase getBalanceUseCase,
                        GetUserUseCase getUserUseCase,
                        AddBalanceUseCase addBalanceUseCase,
                        NotificationUtil notificationUtils,
                        ChildUserTable childUserTable,
                        Pref pref, GetChildUserUseCase childUserUseCase) {
        this.view = view;
        this.databaseHelper = databaseHelper;
        this.getBalanceUseCase = getBalanceUseCase;
        this.getUserUseCase = getUserUseCase;
        this.addBalanceUseCase = addBalanceUseCase;
        this.notificationUtils = notificationUtils;
        this.childUserTable = childUserTable;
        this.pref = pref;
        this.childUserUseCase = childUserUseCase;
    }

    @Override
    public void start() {
        fetchBalance(false);
    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {
        addBalanceUseCase.dispose();
        getBalanceUseCase.dispose();
        getUserUseCase.dispose();
        childUserUseCase.dispose();
    }

    @Override
    public void initialize() {
        fetchAndShowUsersFromLocalStorage(null);
    }

    @Override
    public void loadUserDetailsByPhoneNumber(String phoneNumber, String firmName) {
        view.setAutoCompleteText(firmName + " - " + phoneNumber);
        UserList userList = databaseHelper.getUserListDetailsByPhoneNumber(phoneNumber);
        onUserSelected(userList);
    }

    @Override
    public void onSearchUserTextChanged(CharSequence text) {
        this.searchTerm = text;
        //Filter data
        fetchAndShowUsersFromLocalStorage(text);
    }

    private void fetchUsersFromServer() {
        userArrayList = databaseHelper.getUserDetail();
        childUserUseCase.execute(new DefaultObserver<List<UserList>>() {
            @Override
            public void onNext(List<UserList> value) {
                super.onNext(value);
                view.hideLoadingView();
                fetchAndShowUsersFromLocalStorage(searchTerm);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideLoadingView();
                if(e instanceof InvalidAccessTokenException) {
                    view.showInvalidAccessTokenPopup();
                    return;
                }
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }

        }, GetChildUserUseCase.Params.toParams(userArrayList.get(0).getUser_name(), UserType.DISTRIBUTOR.getType()));
    }

    private void fetchAndShowUsersFromLocalStorage(final CharSequence searchTerm) {
        Observable.fromCallable(new Callable<List<ChildUserModel>>() {
            @Override
            public List<ChildUserModel> call() throws Exception {
                return getUsersFromLocalStorage(searchTerm);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(new DefaultObserver<List<ChildUserModel>>() {
                    @Override
                    public void onNext(List<ChildUserModel> value) {
                        super.onNext(value);
                        onChildUserReceived(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    private void onChildUserReceived(List<ChildUserModel> value) {
        userArrayList = databaseHelper.getUserDetail();
        List<ChildUserModel> data = new ArrayList<>();
        if (value != null && value.size() > 0) {
            view.setUserListAdapter(data);
            view.showUserListView();
            view.hideNoData();
            for (int i=0;i<value.size();i++) {
                System.out.println("Login UserId: " + userArrayList.get(0).getUser_id());
                System.out.println("Parent UserId: " + value.get(i).getParentUserId());
                if(value.get(i).getParentUserId().equals(userArrayList.get(0).getUser_id())) {
                    data.add(value.get(i));
                }
            }
        } else {
            view.hideUserListView();
            view.showNoData();
        }
    }

    @Override
    public void onSelectedUser(ChildUserModel childUserModel) {
        fetchUserDetails(childUserModel.getEmail());
    }

    private List<ChildUserModel> getUsersFromLocalStorage(CharSequence searchTerm) {
        if (!TextUtils.isEmpty(searchTerm)) {
            String whereClause = "upper(" + ChildUserTable.KEY_FIRMNAME + ") like \'" + searchTerm.toString().toUpperCase() + "%\'" +
                    " OR upper(" + ChildUserTable.KEY_EMAIL + ") like \'" + searchTerm.toString().toUpperCase() + "%\'" +
                    " OR upper(" + ChildUserTable.KEY_USERNAME + ") like \'" + searchTerm.toString().toUpperCase() + "%\'" +
                    " OR upper(" + ChildUserTable.KEY_PHONENO + ") like \'" + searchTerm.toString().toUpperCase() + "%\'";
            return childUserTable.select_Data(whereClause);
        } else {
            return childUserTable.select_Data();
        }
    }

    @Override
    public void onUserSelected(UserList userList) {
        fetchUserDetails(userList.getEmail());
    }

    private void fetchUserDetails(String email) {
        view.showLoadingView();
        getUserUseCase.execute(new DefaultObserver<ChildUserModel>() {
            @Override
            public void onNext(ChildUserModel value) {
                super.onNext(value);
                onGetUserSuccess(value);
                view.hideLoadingView();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideLoadingView();
                if (e instanceof InvalidAccessTokenException) {
                    view.showInvalidAccessTokenPopup();
                    return;
                }
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }
        }, GetUserUseCase.Params.toParams(email));
    }

    private void onGetUserSuccess(ChildUserModel userModel) {
        this.mChildUserModel = userModel;
        view.showUserName(userModel.getUserName());
        view.showFirmName(userModel.getFirmName());
        view.showEmail(userModel.getEmail());
        view.showPhoneNumber(userModel.getPhoneNo());
        view.showAmount(userModel.getBalance());
        view.showTotalAmount(Utility.formatBigDecimalToString(new BigDecimal(userModel.getBalance())));
        view.showUserContainer();
        view.hideAutoCompleteView();
        view.hideUserListView();
    }

    @Override
    public void refreshBalance() {
        fetchBalance(false);
    }

    private void fetchBalance(final boolean proceedNext) {
        String cachedBalance = pref.getValue(Pref.KEY_BALANCE, "");
        if(!TextUtils.isEmpty(cachedBalance)) {
            AddBalancePresenter.this.currentBalance = new BigDecimal(cachedBalance);
        }

        if(AddBalancePresenter.this.currentBalance != null) {
            view.showBalance(AddBalancePresenter.this.currentBalance);
        }
        getBalanceUseCase.execute(new DefaultObserver<BigDecimal>() {
            @Override
            public void onNext(BigDecimal value) {
                super.onNext(value);
                AddBalancePresenter.this.currentBalance = value;
                view.showBalance(value);
                if (proceedNext) {
                    onRechargeButtonClicked();
                }
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


    @Override
    public void onRechargeButtonClicked() {
        if (checkValidations()) {
            view.showConfirmRechargePopup(
                    view.getUserName(),
                    view.getFirmName(),
                    view.getPhone(),
                    view.getEmail(),
                    view.getCurrentBalance(),
                    view.getRechargeBalance(),
                    view.getTotalAmount());
        }
    }

    @Override
    public void onConfirmRechargeButtonClicked(FragmentActivity activity) {
        if (Strings.isNullOrEmpty(mChildUserModel.getId()) || "0".equals(mChildUserModel.getId())) {
            view.goBack();
            return;
        }
        proceedToRecharge(activity);
    }

    @Override
    public void onRechargeAmountChanged(CharSequence text) {
        try {
            BigDecimal totalBigDecimal = new BigDecimal(view.getCurrentBalance()).add(new BigDecimal(text.toString()));
            view.showTotalAmount(String.valueOf(totalBigDecimal.floatValue()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void proceedToRecharge(final FragmentActivity activity) {
        view.showLoadingView();
        addBalanceUseCase.execute(
                new DefaultObserver<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onNext(String value) {
                        super.onNext(value);
                        view.hideLoadingView();
                        onAddBalanceSuccess(activity,value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.hideLoadingView();
                        if (e instanceof InvalidAccessTokenException) {
                            view.showInvalidAccessTokenPopup();
                            return;
                        }
                        showErrorMessage(new DefaultErrorBundle((Exception) e));
                    }
                },
                AddBalanceUseCase.Params.toParams(
                        mChildUserModel.getId(),
                        view.getRechargeBalance(),Constants.Lati,Constants.Long));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void onAddBalanceSuccess(FragmentActivity activity, String message) {
        fetchBalance(false);
        fetchUsersFromServer();
        String userName = view.getUserName();
        String phone = view.getPhone();
        String totalAmount = view.getTotalAmount();
        String rechargeBalance = view.getRechargeBalance();
        String notificationMessage = message + "\n" +
                view.context().getString(R.string.user_name_format, userName) + "\n" +
                view.context().getString(R.string.mobile_number_format, phone) + "\n" +
                view.context().getString(R.string.balance_format_format, view.context().getString(R.string.currency_format, totalAmount));

        notificationUtils.sendNotification(activity,view.context().getString(R.string.add_balance), notificationMessage);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT, Locale.getDefault());

        String popupMessage = message + "\n" +
//                view.context().getString(R.string.user_name_format, userName) + "\n" +
                view.context().getString(R.string.mobile_number_format, phone) + "\n" +
                view.context().getString(
                        R.string.amount_format,
                        view.context().getString(
                                R.string.currency_format,
                                rechargeBalance)) + "\n" +
                view.context().getString(R.string.remaining_balance_format_format,
                        view.context().getString(
                                R.string.currency_format,
                                currentBalance.subtract(new BigDecimal(rechargeBalance)).toString())) + "\n" +
                view.context().getString(R.string.date_time_format, simpleDateFormat.format(new Date()));

        view.showAddBalanceSuccessPopup(popupMessage);

    }

    private boolean checkValidations() {
        if (TextUtils.isEmpty(view.getUserEnteredAmount())) {
            view.showToastMessage(view.context().getString(R.string.enter_amount));
            return false;
        } else if (isBalanceLargerThanCurrentBalance()) {
            view.showWarningDialog(view.context().getString(R.string.error_message_add_balance_more_than_current));
            return false;
        } else if (currentBalance.floatValue() <= 0) {
            view.showWarningDialog(view.context().getString(R.string.enter_valid_amount));
            return false;
        } else if(new BigDecimal(view.getUserEnteredAmount().toString()).floatValue() == 0) {
            view.showWarningDialog(view.context().getString(R.string.enter_valid_amount));
            return false;
        }
        return true;
    }

    private boolean isBalanceLargerThanCurrentBalance() {
        return new BigDecimal(view.getRechargeBalance()).compareTo(currentBalance) > 0;
    }

    private void showErrorMessage(DefaultErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(this.view.context(),
                errorBundle.getException());
        this.view.showInfoDialog(errorMessage);
    }
}
