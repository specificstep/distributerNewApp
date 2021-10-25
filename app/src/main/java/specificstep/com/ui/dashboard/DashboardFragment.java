package specificstep.com.ui.dashboard;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.GlobalClasses.MyLocation;
import specificstep.com.R;
import specificstep.com.ui.base.BaseFragment;
import specificstep.com.ui.home.Flow;
import specificstep.com.ui.home.HomeActivity;
import specificstep.com.ui.signup.SignUpActivity;
import specificstep.com.utility.Utility;

public class DashboardFragment extends BaseFragment implements DashboardContract.View, LocationListener {

    @BindView(R.id.notification_badge)
    TextView notificationBadgeTextView;


    @BindView(R.id.ac_ledger_view)
    FrameLayout ac_ledger_view;


    private DashboardContract.Presenter presenter;
    private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            presenter.onNotificationRefreshed();
        }
    };
    private MenuItem balanceMenuItem;
    private static final int MY_PERMISSION_LOCATION = 1;
    MyLocation myLocation = new MyLocation();

    @Override
    public void setPresenter(DashboardContract.Presenter presenter)
    {
        this.presenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
    public void onDestroy() {
        presenter.destroy();
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, view);
        myLocation.getLocation(getActivity(), locationResult);
        marshmallowGPSPremissionCheck();
        ac_ledger_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // HomeActivity mActivity = new HomeActivity();
                //  (HomeActivity)getActivity().showAcLedgerFragment();

                Intent i = new Intent(getActivity(),HomeActivity.class);
                i.putExtra("from_last","dashbordfrag_to_acledgerfrag");
                startActivity(i);
            }
        });
        return view;
    }

    public MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {

        @Override
        public void gotLocation(Location location) {
            // TODO Auto-generated method stub
            try {
                double Longitude = location.getLongitude();
                double Latitude = location.getLatitude();
                Constants.Lati = Latitude + "";
                Constants.Long = Longitude + "";

                System.out.println("Got Location : Longitude: " + Longitude
                        + " Latitude: " + Latitude);
            } catch (Exception e) {
                System.out.println("Location permission denied. " + e.toString());
            }
        }
    };

    private void marshmallowGPSPremissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && getActivity().checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && getActivity().checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_LOCATION);
        } else {
            //   gps functions.
        }

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter.initialize();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_balance, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        balanceMenuItem = menu.findItem(R.id.action_balance_menu_main);
    }

    @OnClick({R.id.add_recharge_view, R.id.user_list_view, R.id.search_transaction_view,
            R.id.update_button, R.id.change_password_button, R.id.notification_button,
            R.id.logout_button ,
            R.id.add_user_view,
            R.id.pourchase_user_view,
            R.id.ac_ledger_view})

    void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_recharge_view:
                presenter.onAddRechargeButtonClicked();
                break;

            case R.id.ac_ledger_view:
                /*HomeActivity mActivity = new HomeActivity();
                mActivity.showAcLedgerFragment();
                break;*/
                /*Intent i1 = new Intent(getActivity(),HomeActivity.class);
                i1.putExtra("from_last","dashbordfrag_to_acledgerfrag");
                startActivity(i1);*/
                presenter.onAcLedgerButtonClicked();
                break;

            case R.id.add_user_view:
                //presenter.onListUserButtonClicked();
                Intent i = new Intent(getActivity(),HomeActivity.class);
                i.putExtra("from_last","dashbordfrag_to_adduserfrag");
                startActivity(i);
                break;
            case R.id.pourchase_user_view:
                Intent i2 = new Intent(getActivity(),HomeActivity.class);
                i2.putExtra("from_last","dashbordfrag_to_purchasefrag");
                startActivity(i2);
                break;
            case R.id.user_list_view:
                presenter.onListUserButtonClicked();
                break;
            case R.id.search_transaction_view:
                presenter.onSearchTransactionButtonClicked();
                break;
            case R.id.update_button:
                presenter.onUpdateButtonClicked();
                break;
            case R.id.change_password_button:
                presenter.onChangePasswordButtonClicked();
                break;
            case R.id.notification_button:
                presenter.onNotificationButtonClicked();
                break;
            case R.id.logout_button:
                presenter.onLogoutButtonClicked();
                break;
        }
    }

    @Override
    public void showMainScreen(@Flow int update) {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.putExtra(HomeActivity.EXTRA_FLOW, update);
        startActivity(intent);
    }

    @Override
    public void showAutoUpdateScreen(@Flow int update) {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.putExtra(HomeActivity.EXTRA_FLOW, update);
        intent.putExtra(HomeActivity.EXTRA_AUTO_UPDATE, true);
        startActivity(intent);
    }

    @Override
    public void showLoginScreen() {
        Intent intent = new Intent(getActivity(), SignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void registerNotificationReceiver(String action) {
        IntentFilter intentFilter = new IntentFilter(action);
        getActivity().registerReceiver(mNotificationReceiver, intentFilter);
    }

    @Override
    public void unRegisterNotificationReceiver() {
        getActivity().unregisterReceiver(mNotificationReceiver);
    }

    @Override
    public void updateNotificationCount(int notificationCount) {
        notificationBadgeTextView.setText(String.valueOf(notificationCount));
    }

    @Override
    public void updateNotificationBadgeVisibility(boolean visible) {
        notificationBadgeTextView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showBalanceInMenu(BigDecimal amount) {
        if(balanceMenuItem != null) {
            balanceMenuItem.setTitleCondensed(getResources().getString(R.string.currency_format, Utility.formatBigDecimalToString(amount)));
        }
    }

    @Override
    public Context context() {
        return getActivity();
    }

    @Override
    public void showErrorDialog(String errorMessage) {
        showDialog(getString(R.string.info), errorMessage);
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("Latitude: " + location.getLatitude());
        System.out.println("Longitude: " + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
