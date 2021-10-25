package specificstep.com.ui.userList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;
import specificstep.com.Adapters.ChildUserAdapter;
import specificstep.com.GlobalClasses.AppController;
import specificstep.com.GlobalClasses.TransparentProgressDialog;
import specificstep.com.Models.ChildUserModel;
import specificstep.com.R;
import specificstep.com.ui.base.BaseFragment;
import specificstep.com.ui.home.HomeContract;
import specificstep.com.ui.userList.DaggerUserListComponent;

public class UserListFragment extends BaseFragment implements UserListContract.View {

    @Inject
    UserListContract.Presenter presenter;
    @BindView(R.id.txt_ChildUser_MinAmount)
    TextView minAmountTextView;
    @BindView(R.id.txt_ChildUser_MaxAmount)
    TextView maxAmountTextView;
    @BindView(R.id.txt_ChildUser_Sorting)
    TextView sortingTextView;
    @BindView(R.id.ll_ChildUser_Search)
    View spinnerContainer;
    @BindView(R.id.spi_ChildUser_MinAmount)
    Spinner minAmountSpinner;
    @BindView(R.id.spi_ChildUser_MaxAmount)
    Spinner maxAmountSpinner;
    @BindView(R.id.spi_ChildUser_Sorting)
    Spinner sortingSpinner;
    @BindView(R.id.btn_ChildUser_Search)
    Button searchButton;
    @BindView(R.id.ll_ChildUser_Reset)
    View resetContainer;
    @BindView(R.id.btn_ChildUser_Reset)
    Button resetButton;
    @BindView(R.id.lst_ChildUser_ChildUserDetails)
    ListView userListView;
    /*@BindView(R.id.txt_ChildUser_NoMoreData)
    TextView emptyTextView;*/
    @BindView(R.id.imgChildUserNoData)
    ImageView imgNoData;
    private boolean mIsInjected;
    private HomeContract.HomeDelegate homeDelegate;
    private TransparentProgressDialog transparentProgressDialog;

    @OnClick({R.id.btn_ChildUser_Reset, R.id.btn_ChildUser_Search})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ChildUser_Reset:
                presenter.onResetButtonClicked();
                break;
            case R.id.btn_ChildUser_Search:
                presenter.onSearchButtonClicked();
                break;
        }
    }

    @OnItemSelected(R.id.spi_ChildUser_MinAmount)
    void onMinAmountSpinnerItemSelected(int position) {
        presenter.onMinAmountSelected(position);
    }

    @Override
    public void showAddBalanceScreen(ChildUserModel childUserModel) {
        homeDelegate.showAddBalanceScreenForUser(childUserModel);
    }

    @OnItemClick(R.id.lst_ChildUser_ChildUserDetails)
    void onUserListItemClick(int position) {
        presenter.onSelectedUser(((ChildUserAdapter)userListView.getAdapter()).getItem(position));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mIsInjected = injectDependency();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_user_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                presenter.onRefreshMenuClicked();
                return true;
        }
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeContract.HomeDelegate) {
            homeDelegate = (HomeContract.HomeDelegate) context;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.start();
    }

    @Override
    public void onStop() {
        presenter.stop();
        super.onStop();
    }

    @Override
    public void onDetach() {
        homeDelegate = null;
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_users, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(homeDelegate != null) {
            homeDelegate.setToolBarTitle(getString(R.string.list_users));
        }
        if (!mIsInjected) {
            mIsInjected = injectDependency();
        }
        presenter.initialize();
    }

    boolean injectDependency() {
        try {
            DaggerUserListComponent.builder()
                    .applicationComponent(((AppController) getActivity().getApplication()).getApplicationComponent())
                    .userListModule(new UserListModule(this))
                    .build()
                    .inject(this);
            return true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onViewDestroy();
    }

    @Override
    public void onDestroy() {
        presenter.destroy();
        super.onDestroy();
    }

    @Override
    public void hideLoadingView() {
        if (transparentProgressDialog != null) {
            transparentProgressDialog.dismiss();
            transparentProgressDialog = null;
        }
    }

    @Override
    public void showLoadingView() {
        hideLoadingView();
        transparentProgressDialog = new TransparentProgressDialog(getActivity(), R.drawable.fotterloading);

        if (!transparentProgressDialog.isShowing()) {
            transparentProgressDialog.show();
        }
    }

    @Override
    public void showSearchContainer() {
        spinnerContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSearchButton() {
        searchButton.setVisibility(View.VISIBLE);
    }

    @Override
    public Context context() {
        return getActivity();
    }

    @Override
    public void setMaxAmountAdapter(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.item_spinner_list_user_amount,
                data);
        maxAmountSpinner.setAdapter(adapter);
    }

    @Override
    public void setSortingAdapter(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.item_spinner_list_user_amount,
                data);
        sortingSpinner.setAdapter(adapter);
    }

    @Override
    public void selectMaxValueByPosition(int selectedMaxIndex) {
        maxAmountSpinner.setSelection(selectedMaxIndex);
    }

    @Override
    public String getSelectedMinValue() {
        return minAmountSpinner.getSelectedItem().toString();
    }

    @Override
    public String getSelectedMaxValue() {
        return maxAmountSpinner.getSelectedItem().toString();
    }

    @Override
    public String getSelectedSortingValue() {
        return sortingSpinner.getSelectedItem().toString();
    }

    @Override
    public void hideUserListView() {
        userListView.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyView() {
        imgNoData.setVisibility(View.VISIBLE);
    }

    @Override
    public void showUserListView() {
        userListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyView() {
        imgNoData.setVisibility(View.GONE);
    }

    @Override
    public void setMinAmountText(String text) {
        minAmountTextView.setText(text);
    }

    @Override
    public void setMaxAmountText(String text) {
        maxAmountTextView.setText(text);
    }

    @Override
    public void setSortingText(String text) {
        sortingTextView.setText(text);
    }

    @Override
    public void hideSearchContainer() {
        spinnerContainer.setVisibility(View.GONE);
    }

    @Override
    public void hideSearchButton() {
        searchButton.setVisibility(View.GONE);
    }

    @Override
    public void showResetContainer() {
        resetContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showErrorDialog(String errorMessage) {
        showDialog(getString(R.string.info), errorMessage);
    }

    @Override
    public void setUserListAdapter(List<ChildUserModel> userModels) {
        Collections.reverse(userModels);
        ChildUserAdapter adapter = new ChildUserAdapter(getActivity(), userModels);
        userListView.setAdapter(adapter);
    }

    @Override
    public void setMinAmountAdapter(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.item_spinner_list_user_amount,
                data);
        minAmountSpinner.setAdapter(adapter);
    }

    @Override
    public void hideResetContainer() {
        resetContainer.setVisibility(View.GONE);
    }

    /*@Override
    public boolean onBackPressed() {
        if (userListView.getVisibility() == View.INVISIBLE) {
            hideUserContainer();
            showUserListView();
            showAutoCompleteView();
            return false;
        } else {
            return true;
        }
    }*/

}
