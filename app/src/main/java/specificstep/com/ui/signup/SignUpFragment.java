package specificstep.com.ui.signup;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.GlobalClasses.MyLocation;
import specificstep.com.GlobalClasses.TransparentProgressDialog;
import specificstep.com.R;
import specificstep.com.ui.base.BaseFragment;
import specificstep.com.ui.otpVerification.OtpVerificationActivity;
import specificstep.com.ui.signIn.SignInActivity;
import specificstep.com.utility.PermissionUtils;
import specificstep.com.utility.Utility;

public class SignUpFragment extends BaseFragment implements SignUpContract.View, LocationListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 100;
    @BindView(R.id.edt_uname_or_email_act_reg)
    EditText userNameEditText;
    private SignUpContract.Presenter presenter;
    private TransparentProgressDialog transparentProgressDialog;

    private Handler handler;
    private static final int MY_PERMISSION_LOCATION = 1;
    MyLocation myLocation = new MyLocation();


    public SignUpFragment() {
        // Required empty public constructor
    }

    @OnClick(R.id.btn_reg_app_act_reg)
    void onRegisterButtonClick() {
        presenter.register(userNameEditText.getText().toString().trim());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.bind(this, view);
        myLocation.getLocation(getActivity(), locationResult);
        marshmallowGPSPremissionCheck();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };
        userNameEditText.setFilters(new InputFilter[]{filter});
        readPhoneState();
    }

    @Override
    public void onDestroy() {
        presenter.destroy();
        super.onDestroy();
    }

    public void readPhoneState() {
        if(PermissionUtils.hasPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)) return;
        if(PermissionUtils.shouldShowRequestPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            PermissionUtils.requestPermission(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    @Override
    public void setProgressIndicator() {
        if (transparentProgressDialog != null) {
            transparentProgressDialog.dismiss();
            transparentProgressDialog = null;
        }
        transparentProgressDialog = new TransparentProgressDialog(getActivity(), R.drawable.fotterloading);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!transparentProgressDialog.isShowing()) {
                    try {
                        transparentProgressDialog.show();
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new Handler();
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
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showError(String errorMsg) {
        Utility.toast(getActivity(), errorMsg);
    }

    @Override
    public Context context() {
        return getActivity();
    }

    @Override
    public void showInternetNotAvailableDialog() {
        showDialog(getString(R.string.error), getString(R.string.message_no_intenet));
    }

    @Override
    public void hideProgressIndicator() {
        if (transparentProgressDialog != null) {
            transparentProgressDialog.dismiss();
            transparentProgressDialog = null;
        }
    }

    @Override
    public void showErrorDialog(String errorMessage) {
        showDialog(getString(R.string.info), errorMessage);
    }

    @Override
    public void showVerifyRegistrationScreen(String userName) {

        presenter.makeAutoOtpCall(userName);

    }

    @Override
    public void showOtpScreen(String userName) {
        Intent intent = new Intent(getActivity(), OtpVerificationActivity.class);
        intent.putExtra(OtpVerificationActivity.EXTRA_USERNAME, userName);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void setPresenter(SignUpContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showLoginScreen(String userName) {
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        intent.putExtra(SignInActivity.EXTRA_USER_NAME, userName);
        startActivity(intent);
        getActivity().finish();
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
