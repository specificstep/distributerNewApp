package specificstep.com.ui.signup;

import android.util.Patterns;

import com.google.common.base.Strings;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;

import javax.inject.Inject;

import specificstep.com.GlobalClasses.Constants;
import specificstep.com.R;
import specificstep.com.data.entity.AutoOtpEntity;
import specificstep.com.data.entity.BaseResponse;
import specificstep.com.data.net.RestApiImpl;
import specificstep.com.exceptions.ErrorMessageFactory;
import specificstep.com.interactors.DefaultObserver;
import specificstep.com.interactors.exception.DefaultErrorBundle;
import specificstep.com.interactors.usecases.AutoOtpUseCase;
import specificstep.com.interactors.usecases.OtpVerifyUseCase;
import specificstep.com.interactors.usecases.SignUpUseCase;

class SignUpPresenter implements SignUpContract.Presenter {

    private SignUpContract.View view;
    private final SignUpUseCase signUpUseCase;
    private final AutoOtpUseCase autoOtpUseCase;
    private final OtpVerifyUseCase otpVerifyUseCase;

    @Inject
    SignUpPresenter(SignUpContract.View view, SignUpUseCase signUpUseCase, AutoOtpUseCase autoOtpUseCase, OtpVerifyUseCase otpVerifyUseCase) {
        this.view = view;
        this.signUpUseCase = signUpUseCase;
        this.autoOtpUseCase = autoOtpUseCase;
        this.otpVerifyUseCase = otpVerifyUseCase;
    }

    @Inject
    void setupListeners() {
        view.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {
        if (signUpUseCase != null) {
            signUpUseCase.dispose();
        }
    }

    @Override
    public void register(String userName) {
        if (userName.isEmpty()) {
            view.showError(view.context().getString(R.string.message_enter_username));
            return;
        } else if (Strings.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken())) {
            view.showInternetNotAvailableDialog();
            return;
        }
        callSignUpAPI(userName);
    }

    @Override
    public void makeAutoOtpCall(final String userName) {
        view.setProgressIndicator();
        autoOtpUseCase.execute(new DefaultObserver<List<AutoOtpEntity>>() {

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideProgressIndicator();
                /*if(e instanceof InvalidUserNameException) {
                    //showInValidUserNameError(userName);
                    return;
                }*/
                view.showErrorDialog(String.valueOf(e.getMessage()));
                //showErrorMessage(new DefaultErrorBundle((Exception) e));
            }

            @Override
            public void onNext(List<AutoOtpEntity> value) {
                super.onNext(value);
                view.hideProgressIndicator();
                value = RestApiImpl.summaryEntities;
                if(value.get(0).getSkip_otp().equals("1")) {
                    verifyOtp(value.get(0).getDefault_otp(), userName);
                } else {
                    view.showOtpScreen(userName);
                }
            }

        }, AutoOtpUseCase.Params.toParams(userName, Integer.parseInt(Constants.LOGIN_TYPE_DISTRIBUTER)));
    }

    private void verifyOtp(String otp, String userName) {
        if (Strings.isNullOrEmpty(otp)) {
            view.showErrorDialog(view.context().getString(R.string.enter_opt));
            return;
        }
        callVerifyOtpAPI(otp, userName);
    }

    private void callVerifyOtpAPI(String otp, final String userName) {
        view.setProgressIndicator();
        otpVerifyUseCase.execute(new DefaultObserver<BaseResponse>() {

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideProgressIndicator();
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }

            @Override
            public void onNext(BaseResponse value) {
                super.onNext(value);
                onOtpVerificationCompleted(userName);
            }

        }, OtpVerifyUseCase.Params.toParams(userName, otp, Integer.parseInt(Constants.LOGIN_TYPE_DISTRIBUTER)));
    }

    private void onOtpVerificationCompleted(String userName) {
        //getMobileCompany();
        view.showLoginScreen(userName);
    }

    private void callSignUpAPI(final String userName) {
        view.setProgressIndicator();
        signUpUseCase.execute(new DefaultObserver<BaseResponse>() {

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.hideProgressIndicator();
                /*if(e instanceof InvalidUserNameException) {
                    //showInValidUserNameError(userName);
                    return;
                }*/
                view.showErrorDialog(String.valueOf(e.getMessage()));
                //showErrorMessage(new DefaultErrorBundle((Exception) e));
            }

            @Override
            public void onNext(BaseResponse value) {
                super.onNext(value);
                view.hideProgressIndicator();
                view.showVerifyRegistrationScreen(userName);
            }

        }, SignUpUseCase.Params.toParams(userName, Integer.parseInt(Constants.LOGIN_TYPE_DISTRIBUTER)));
    }

    private void showInValidUserNameError(String userName) {
        if (Patterns.EMAIL_ADDRESS.matcher(userName).matches()) {
            view.showErrorDialog(view.context().getString(R.string.invalid_email_address));
        }else if(userName.matches("[0-9]+")) {
            view.showErrorDialog(view.context().getString(R.string.invalid_phone_number));
        }else {
            view.showErrorDialog(view.context().getString(R.string.invalid_username));
        }
    }

    private void showErrorMessage(DefaultErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(this.view.context(),
                errorBundle.getException());
        this.view.showErrorDialog(errorMessage);
    }




}
