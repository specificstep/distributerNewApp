package specificstep.com.ui.updateData;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import specificstep.com.GlobalClasses.AppController;
import specificstep.com.R;
import specificstep.com.ui.base.BaseFragment;
import specificstep.com.ui.home.HomeContract;
import specificstep.com.ui.signup.SignUpActivity;
import specificstep.com.ui.updateData.DaggerUpdateDataComponent;

public class UpdateDataFragment extends BaseFragment implements UpdateDataContract.View {

    private static final String KEY_FORCE_UPDATE = "force_update";
    @Inject
    UpdateDataContract.Presenter presenter;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.btn_update_data)
    Button updateDataButton;
    @BindView(R.id.btn_return_home)
    Button homeButton;
    @BindView(R.id.txt_Update_LastUpdateDate)
    TextView lastUpdateDateTextView;
    @BindView(R.id.textView)
    TextView updateStatusTextView;
    private boolean mIsInjected;
    private HomeContract.HomeDelegate homeDelegate;

    public static UpdateDataFragment getInstance(boolean forceUpdate) {
        UpdateDataFragment fragment = new UpdateDataFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_FORCE_UPDATE, forceUpdate);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsInjected = injectDependency();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_data, container, false);
        ButterKnife.bind(this, view);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return view;
    }

    @Override
    public void onDestroy() {
        presenter.destroy();
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(homeDelegate != null) {
            homeDelegate.setToolBarTitle(getString(R.string.update_data));
        }
        if (!mIsInjected) {
            mIsInjected = injectDependency();
        }

        boolean forceUpdate = (getArguments() != null && getArguments().containsKey(KEY_FORCE_UPDATE)) && getArguments().getBoolean(KEY_FORCE_UPDATE);

        presenter.initialize(getActivity(),forceUpdate);
    }

    boolean injectDependency() {
        try {
            DaggerUpdateDataComponent.builder()
                    .applicationComponent(((AppController) getActivity().getApplication()).getApplicationComponent())
                    .updateDataModule(new UpdateDataModule(this))
                    .build()
                    .inject(this);
            return true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
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
    public void onDetach() {
        homeDelegate = null;
        super.onDetach();
    }

    @OnClick({R.id.btn_update_data, R.id.btn_return_home})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update_data:
                presenter.onUpdateDataButtonClicked(getActivity());
                break;
            case R.id.btn_return_home:
                presenter.onHomeButtonClicked();
                break;
        }
    }

    @Override
    public void goBack() {
        homeDelegate.showHomeScreen();
    }

    @Override
    public void showLastUpdatedDate(String strDate) {
        lastUpdateDateTextView.setText(getString(R.string.last_update_format, strDate));
        lastUpdateDateTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLastUpdatedDateView() {
        lastUpdateDateTextView.setVisibility(View.GONE);
    }

    @Override
    public void hideUpdateDataButton() {
        updateDataButton.setVisibility(View.GONE);
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showStatusBar() {
        updateStatusTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLastUpdateDateView() {
        lastUpdateDateTextView.setVisibility(View.GONE);
    }

    @Override
    public void updateProgress(int progress) {
        progressBar.setProgress(progress);
    }

    @Override
    public void disableDrawer() {
        homeDelegate.disableDrawer();
    }

    @Override
    public Context context() {
        return getActivity();
    }

    @Override
    public void updateStatusText(String status) {
        updateStatusTextView.setText(status);
    }

    @Override
    public void showHomeButton() {
        homeButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void enableDrawer() {
        homeDelegate.enableDrawer();
    }

    @Override
    public void showSignInScreen() {
        Intent intent = new Intent(getActivity(), SignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().setResult(Activity.RESULT_CANCELED);
        getActivity().finish();

    }

    @Override
    public void showErrorDialog(String errorMessage) {
        showDialog(getString(R.string.info), errorMessage);
    }

    @Override
    public void showUpdateDataButton() {
        updateDataButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE
        );
    }
}
