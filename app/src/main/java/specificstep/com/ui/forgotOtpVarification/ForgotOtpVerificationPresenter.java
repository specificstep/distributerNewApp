package specificstep.com.ui.forgotOtpVarification;


import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import specificstep.com.Database.DatabaseHelper;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.Models.Color;
import specificstep.com.Models.Company;
import specificstep.com.Models.Product;
import specificstep.com.Models.State;
import specificstep.com.Models.User;
import specificstep.com.R;
import specificstep.com.data.entity.BaseResponse;
import specificstep.com.data.entity.ForgotPasswordResponse;
import specificstep.com.data.utils.ServiceType;
import specificstep.com.exceptions.ErrorMessageFactory;
import specificstep.com.interactors.DefaultObserver;
import specificstep.com.interactors.exception.DefaultErrorBundle;
import specificstep.com.interactors.usecases.ForgotOtpVerifyUseCase;
import specificstep.com.interactors.usecases.GetCompanyUseCase;
import specificstep.com.interactors.usecases.GetProductUseCase;
import specificstep.com.interactors.usecases.GetSettingsUseCase;
import specificstep.com.interactors.usecases.GetStateUseCase;
import specificstep.com.interactors.usecases.SignUpUseCase;

public class ForgotOtpVerificationPresenter implements ForgotOtpVerificationContract.Presenter {

    private static final long TIMER_INTERVAL = 1000;
    private static final long TIMER_DURATION = 1000 * 60 * 2;
    private ForgotOtpVerificationContract.View view;
    private boolean isOtpReceived;
    private final SignUpUseCase signUpUseCase;
    private final ForgotOtpVerifyUseCase otpVerifyUseCase;
    private final GetCompanyUseCase getCompanyUseCase;
    private final GetProductUseCase getProductUseCase;
    private final GetStateUseCase getStateUseCase;
    private final GetSettingsUseCase getSettingsUseCase;
    private DatabaseHelper databaseHelper;
    private ArrayList<User> userArrayList;
    private String strMacAddress, strUserName, strOtpCode, strRegistrationDateTime;

    private CountDownTimer countDownTimer = new CountDownTimer(TIMER_DURATION, TIMER_INTERVAL) {
        @Override
        public void onTick(long durationInMillis) {
            long seconds = durationInMillis / 1000;
            String timeFormat = String.format(Locale.getDefault(), "%02d:%02d",
                    (seconds % 3600) / 60, (seconds % 60));
            Log.d(getClass().getSimpleName(), "Remaining time: " + timeFormat);
            view.updateCountDownTime(timeFormat);
        }

        @Override
        public void onFinish() {
            onCountDownFinished();
        }
    };

    @Inject
    public ForgotOtpVerificationPresenter(ForgotOtpVerificationContract.View view,
                                    SignUpUseCase signUpUseCase,
                                    GetCompanyUseCase getCompanyUseCase,
                                    GetProductUseCase getProductUseCase,
                                    GetStateUseCase getStateUseCase,
                                    GetSettingsUseCase getSettingsUseCase,
                                          ForgotOtpVerifyUseCase otpVerifyUseCase) {
        this.view = view;
        this.signUpUseCase = signUpUseCase;
        this.otpVerifyUseCase = otpVerifyUseCase;
        this.getCompanyUseCase = getCompanyUseCase;
        this.getProductUseCase = getProductUseCase;
        this.getStateUseCase = getStateUseCase;
        this.getSettingsUseCase = getSettingsUseCase;
    }

    @Inject
    void setupListeners() {
        view.setPresenter(this);
    }

    private void onCountDownFinished() {
        view.hideCountDownTimer();
        view.enableResendButton();
//        view.disableOtpEditText();
    }

    @Override
    public void start() {
        startCountDownTimer();
    }

    @Override
    public void stop() {
        countDownTimer.cancel();
    }

    @Override
    public void destroy() {
        countDownTimer.cancel();
        countDownTimer = null;

        signUpUseCase.dispose();
        otpVerifyUseCase.dispose();
        getCompanyUseCase.dispose();
        getProductUseCase.dispose();
        getStateUseCase.dispose();
        getSettingsUseCase.dispose();

    }

    private void verifyOtp(Context context, String forgot_otp) {
        String otp = view.getOtp();
        if (Strings.isNullOrEmpty(otp)) {
            view.showErrorMessage(view.context().getString(R.string.enter_opt));
            return;
        }
        callVerifyOtpAPI(forgot_otp, view.getUserName(),context);
    }

    private void callVerifyOtpAPI(final String otp, String userName, final Context context) {
        view.setProgressIndicator();
        databaseHelper = new DatabaseHelper(context);
        userArrayList = databaseHelper.getUserDetail();
        strMacAddress = userArrayList.get(0).getDevice_id();
        strUserName = userArrayList.get(0).getUser_name();
        strOtpCode = userArrayList.get(0).getOtp_code();
        otpVerifyUseCase.execute(new DefaultObserver<ForgotPasswordResponse>() {

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideProgressIndicator();
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }

            @Override
            public void onNext(ForgotPasswordResponse value) {
                super.onNext(value);
                if(value.getStatus() == 1) {
                    Toast.makeText(context,value.getMessage(), Toast.LENGTH_SHORT).show();
                   // Toast.makeText(context,"kns >> "+value.getPassword(),Toast.LENGTH_LONG).show();

                    //onOtpVerificationCompleted(otp,String.valueOf(value.getPassword()));
                    view.showForgotPasswordScreen(otp, String.valueOf(value.getPassword()));
                } else {
                    Toast.makeText(context,value.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }, ForgotOtpVerifyUseCase.Params.toParams(strUserName, strOtpCode, strMacAddress, Constants.APP_VERSION, otp));
    }

    private void onOtpVerificationCompleted(String otp, String password) {
        getMobileCompany(otp,password);
    }

    private void getMobileCompany(final String otp, final String password) {
        getCompanyUseCase.execute(new DefaultObserver<List<Company>>() {

            @Override
            public void onNext(List<Company> value) {
                super.onNext(value);
                getDTHCompany(otp,password);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideProgressIndicator();
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }
        }, GetCompanyUseCase.Params.toParams(ServiceType.MOBILE.getType()));
    }

    private void getDTHCompany(final String otp, final String password) {
        getCompanyUseCase.execute(new DefaultObserver<List<Company>>() {

            @Override
            public void onNext(List<Company> value) {
                super.onNext(value);
                getMobileProducts(otp,password);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideProgressIndicator();
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }
        }, GetCompanyUseCase.Params.toParams(ServiceType.DTH.getType()));
    }

    private void getMobileProducts(final String otp, final String password) {
        getProductUseCase.execute(new DefaultObserver<List<Product>>() {

            @Override
            public void onNext(List<Product> value) {
                super.onNext(value);
                getDTHProducts(otp,password);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideProgressIndicator();
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }
        }, GetProductUseCase.Params.toParams(ServiceType.MOBILE.getType()));
    }

    private void getDTHProducts(final String otp, final String password) {
        getProductUseCase.execute(new DefaultObserver<List<Product>>() {

            @Override
            public void onNext(List<Product> value) {
                super.onNext(value);
                getStates(otp,password);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideProgressIndicator();
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }
        }, GetProductUseCase.Params.toParams(ServiceType.DTH.getType()));
    }

    private void getStates(final String otp, final String password) {
        getStateUseCase.execute(new DefaultObserver<List<State>>() {

            @Override
            public void onNext(List<State> value) {
                super.onNext(value);
                getSettings(otp,password);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideProgressIndicator();
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }
        }, GetStateUseCase.Params.toParams(ServiceType.MOBILE.getType()));
    }

    private void getSettings(final String otp, final String password) {
        getSettingsUseCase.execute(new DefaultObserver<List<Color>>() {

            @Override
            public void onNext(List<Color> value) {
                super.onNext(value);
                view.hideProgressIndicator();
                view.showForgotPasswordScreen(otp,password);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideProgressIndicator();
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }
        }, null);
    }

    @Override
    public void onVerifyOtpButtonClicked(Context context, String forgot_otp) {
        verifyOtp(context, forgot_otp);
    }

    @Override
    public void onResendOtpButtonClicked() {
        callSignUpAPI(view.getUserName());
    }

    private void callSignUpAPI(final String userName) {
        view.setProgressIndicator();
        signUpUseCase.execute(new DefaultObserver<BaseResponse>() {

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideProgressIndicator();
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }

            @Override
            public void onNext(BaseResponse value) {
                super.onNext(value);
                view.hideProgressIndicator();
                onSignUpSuccess();
            }

        }, SignUpUseCase.Params.toParams(userName, Integer.parseInt(Constants.LOGIN_TYPE_DISTRIBUTER)));
    }

    private void onSignUpSuccess() {
//        view.enableOtpEditText();
        view.disableResendOtpButton();
        startCountDownTimer();
    }

    private void startCountDownTimer() {
        countDownTimer.start();
        view.showCountDownTimer();
    }

    private void showErrorMessage(DefaultErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(this.view.context(),
                errorBundle.getException());
        this.view.showErrorDialog(errorMessage);
    }

}
