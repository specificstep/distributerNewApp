package specificstep.com.ui.userList;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import specificstep.com.Database.ChildUserTable;
import specificstep.com.Database.DatabaseHelper;
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
import specificstep.com.interactors.usecases.GetChildUserUseCase;

public class UserListPresenter implements UserListContract.Presenter {

    private final UserListContract.View view;
    private final Pref pref;
    private final ChildUserTable childUserTable;
    private final GetChildUserUseCase childUserUseCase;
    private DisposableObserver<List<ChildUserModel>> childUserObserver;
    private boolean isFirstTime = true;
    String minValue,maxValue,sortingValue;
    List<ChildUserModel> call;
    private DatabaseHelper databaseHelper;
    private ArrayList<User> userArrayList;

    @Inject
    public UserListPresenter(UserListContract.View view, Pref pref, ChildUserTable childUserTable, GetChildUserUseCase childUserUseCase) {
        this.view = view;
        this.pref = pref;
        this.childUserTable = childUserTable;
        this.childUserUseCase = childUserUseCase;
    }


    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void onViewDestroy() {
        isFirstTime = true;
    }

    @Override
    public void destroy() {
        if (childUserObserver != null && !childUserObserver.isDisposed()) {
            childUserObserver.dispose();
        }
        childUserUseCase.dispose();
        isFirstTime = true;
    }

    @Override
    public void initialize() {
        view.showSearchContainer();
        view.showSearchButton();
        view.hideResetContainer();
        initMinValue();
        initMaxValue();
        initSortingValue();
    }

    @Override
    public void onResetButtonClicked() {
        isFirstTime = true;
//        saveDefaultSearchCriteria();
        view.setMinAmountText(view.context().getString(R.string.min_amount));
        view.setMaxAmountText(view.context().getString(R.string.max_amount));
        view.setSortingText(view.context().getString(R.string.sorting));
        initialize();
        fetchAndShowChildUsers();
    }

    @Override
    public void onSearchButtonClicked() {
        String minAmount = view.getSelectedMinValue();
        String maxAmount = view.getSelectedMaxValue();
        String sorting = view.getSelectedSortingValue();

        String[] minArray = view.context().getResources().getStringArray(R.array.min_amount);
        String[] maxArray = view.context().getResources().getStringArray(R.array.max_amount);
        String VALUE_MIN_AMOUNT = minArray[0];
        String VALUE_MAX_AMOUNT = maxArray[maxArray.length - 1];


        if (TextUtils.equals(minAmount, VALUE_MIN_AMOUNT)) {
            view.setMinAmountText(view.context().getString(R.string.min_format, minAmount));
        } else {
            view.setMinAmountText(view.context().getString(R.string.min_format_with_amount, minAmount));
        }

        if (TextUtils.equals(maxAmount, VALUE_MAX_AMOUNT)) {
            view.setMaxAmountText(view.context().getString(R.string.max_format, maxAmount));
        } else {
            view.setMaxAmountText(view.context().getString(R.string.max_format_with_amount, maxAmount));
        }

        view.setSortingText(view.context().getString(R.string.sort_format, sorting));
        view.hideSearchContainer();
        view.hideSearchButton();
        view.showResetContainer();
        fetchAndShowChildUsers();
    }

    @Override
    public void onMinAmountSelected(int position) {
        String[] maxArray = view.context().getResources().getStringArray(R.array.max_amount);

        int[] minArrayValues = view.context().getResources().getIntArray(R.array.min_amount_value);


        String savedMaxValue = pref.getValue(Pref.KEY_MAX_AMOUNT_FILTER_VALUE, maxArray[maxArray.length - 1]);
        if (isFirstTime) {
            savedMaxValue = maxArray[maxArray.length - 1];
        }
        ArrayList<String> maxData = new ArrayList<>();

        int minValue = minArrayValues[position];
        int currentValue = minValue;
        if (position == 0) {
            //Add 500 if MIN is selected
            maxData.add(String.valueOf(minValue));
        }
        int maxAmountLimit = 100000;
        do {
            currentValue *= 2;
            if(currentValue > maxAmountLimit) {
                maxData.add(String.valueOf(maxAmountLimit));
                maxData.add(maxArray[maxArray.length - 1]);
            }else {
                maxData.add(String.valueOf(currentValue));
            }
        } while (currentValue < maxAmountLimit);

        if (maxData.size() > 0) {
            view.setMaxAmountAdapter(maxData.toArray(new String[maxData.size()]));
        }
        int selectedMaxIndex = maxData.size() - 1;
        for (int i = 0; i < maxData.size(); i++) {
            if (maxData.get(i).equals(savedMaxValue)) {
                selectedMaxIndex = i;
                break;
            }
        }

        view.selectMaxValueByPosition(selectedMaxIndex);
        if (isFirstTime) {
            isFirstTime = false;
            fetchAndShowChildUsers();
        }
    }

    @Override
    public void onRefreshMenuClicked() {
        view.showLoadingView();
        databaseHelper = new DatabaseHelper(view.context());
        userArrayList = databaseHelper.getUserDetail();
        childUserUseCase.execute(new DefaultObserver<List<UserList>>() {
            @Override
            public void onNext(List<UserList> value) {
                super.onNext(value);
                view.hideLoadingView();
                fetchAndShowChildUsers();
                for(int i=0;i<call.size();i++) {
                    System.out.println("fetch user data: " + call.get(i).getFirmName());
                }
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

    private void showErrorMessage(DefaultErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(this.view.context(),
                errorBundle.getException());
        this.view.showErrorDialog(errorMessage);
    }

    private void fetchAndShowChildUsers() {
        minValue = view.getSelectedMinValue();
        maxValue = view.getSelectedMaxValue();
        sortingValue = view.getSelectedSortingValue();

        childUserObserver = Observable.fromCallable(new Callable<List<ChildUserModel>>() {
            @Override
            public List<ChildUserModel> call() throws Exception {
                call = fetchChilds(minValue, maxValue, sortingValue);
                return fetchChilds(minValue, maxValue, sortingValue);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<ChildUserModel>>() {
                    @Override
                    public void onNext(List<ChildUserModel> value) {
                        onChildUserReceived(value);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void onChildUserReceived(List<ChildUserModel> value) {
        if (value.size() > 0) {
            saveCurrentSearchCriteria();
            showUserListContainer();
            //view.setUserListAdapter(value);

            databaseHelper = new DatabaseHelper(view.context());
            userArrayList = databaseHelper.getUserDetail();
            List<ChildUserModel> data = new ArrayList<>();
            if (value != null && value.size() > 0) {
                for (int i=0;i<value.size();i++) {
                    System.out.println("Login UserId: " + userArrayList.get(0).getUser_id());
                    System.out.println("Parent UserId: " + value.get(i).getParentUserId());
                    if(value.get(i).getParentUserId().equals(userArrayList.get(0).getUser_id())) {
                        data.add(value.get(i));
                    }
                }
                view.setUserListAdapter(data);
            }


        } else {
            hideUserListContainer();
            saveDefaultSearchCriteria();
        }
    }

    private void saveDefaultSearchCriteria() {
        String[] minArray = view.context().getResources().getStringArray(R.array.min_amount);
        String[] maxArray = view.context().getResources().getStringArray(R.array.max_amount);
        String VALUE_MIN_AMOUNT = minArray[0];
        String VALUE_MAX_AMOUNT = maxArray[maxArray.length - 1];

        pref.setValue(Pref.KEY_MIN_AMOUNT_FILTER_VALUE, VALUE_MIN_AMOUNT);
        pref.setValue(Pref.KEY_MAX_AMOUNT_FILTER_VALUE, VALUE_MAX_AMOUNT);
        pref.setValue(Pref.KEY_SORTING_FILTER_VALUE, OrderBy.NAME.getTitle());
    }

    @Override
    public void onSelectedUser(ChildUserModel childUserModel) {
        view.showAddBalanceScreen(childUserModel);
    }

    private void hideUserListContainer() {
        view.hideUserListView();
        view.showEmptyView();
    }

    private void showUserListContainer() {
        view.showUserListView();
        view.hideEmptyView();
    }

    private void saveCurrentSearchCriteria() {
        final String minValue = view.getSelectedMinValue();
        final String maxValue = view.getSelectedMaxValue();
        final String sortingValue = view.getSelectedSortingValue();

        pref.setValue(Pref.KEY_MIN_AMOUNT_FILTER_VALUE, minValue);
        pref.setValue(Pref.KEY_MAX_AMOUNT_FILTER_VALUE, maxValue);
        pref.setValue(Pref.KEY_SORTING_FILTER_VALUE, sortingValue);
    }

    private List<ChildUserModel> fetchChilds(String minAmount, String maxAmount, String orderBy) {
        String[] minArray = view.context().getResources().getStringArray(R.array.min_amount);
        String[] maxArray = view.context().getResources().getStringArray(R.array.max_amount);

        String VALUE_MIN_AMOUNT = minArray[0];
        String VALUE_MAX_AMOUNT = maxArray[maxArray.length - 1];

        if (minAmount == null || TextUtils.isEmpty(minAmount)) {
            minAmount = VALUE_MIN_AMOUNT;
        }
        if (maxAmount == null || TextUtils.isEmpty(maxAmount)) {
            maxAmount = VALUE_MAX_AMOUNT;
        }

        if (TextUtils.equals(orderBy, OrderBy.NAME.getTitle())) {
            orderBy = ChildUserTable.KEY_FIRMNAME + " COLLATE NOCASE";
        } else if (TextUtils.equals(orderBy, OrderBy.LOW_TO_HIGH.getTitle())) {
            orderBy = ChildUserTable.KEY_BALANCE + " ASC";
        } else if (TextUtils.equals(orderBy, OrderBy.HIGH_TO_LOW.getTitle())) {
            orderBy = ChildUserTable.KEY_BALANCE + " DESC";
        } else {
            orderBy = ChildUserTable.KEY_FIRMNAME + " COLLATE NOCASE";
        }

        String whereClause = "";
        ArrayList<ChildUserModel> childUserModels;
        if (TextUtils.equals(minAmount, VALUE_MIN_AMOUNT) && TextUtils.equals(maxAmount, VALUE_MAX_AMOUNT)) {
            //childUserModels = childUserTable.selectData_OrderByBalance(orderBy);
            childUserModels = childUserTable.select_Data();
        } else if (TextUtils.equals(minAmount, VALUE_MIN_AMOUNT)) {
            whereClause = ChildUserTable.KEY_BALANCE + " BETWEEN 0 AND " + maxAmount
                    + " ORDER BY " + orderBy;
            childUserModels = childUserTable.select_Data(whereClause);
        } else if (TextUtils.equals(maxAmount, VALUE_MAX_AMOUNT)) {
            whereClause = ChildUserTable.KEY_BALANCE + " >= " + minAmount
                    + " ORDER BY " + orderBy;
            childUserModels = childUserTable.select_Data(whereClause);
        } else {
            whereClause = ChildUserTable.KEY_BALANCE + " BETWEEN " + minAmount + " AND " + maxAmount
                    + " ORDER BY " + orderBy;
            childUserModels = childUserTable.select_Data(whereClause);
        }
        return childUserModels;
    }

    private void initSortingValue() {
        String[] data = view.context().getResources().getStringArray(R.array.sorting);
        view.setSortingAdapter(data);
    }

    private void initMaxValue() {
        String[] data = view.context().getResources().getStringArray(R.array.max_amount);
        view.setMaxAmountAdapter(data);
    }

    private void initMinValue() {
        String[] data = view.context().getResources().getStringArray(R.array.min_amount);
        view.setMinAmountAdapter(data);
    }

    enum OrderBy {
        NAME("Name"), LOW_TO_HIGH("Low to High"), HIGH_TO_LOW("High to Low");
        private String title;

        OrderBy(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }
}
