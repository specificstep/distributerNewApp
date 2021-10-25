package specificstep.com.ui.signIn;

import android.content.Context;
import android.text.TextUtils;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import specificstep.com.Database.DatabaseHelper;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.GlobalClasses.EncryptionUtil;
import specificstep.com.Models.User;
import specificstep.com.Models.UserList;
import specificstep.com.R;
import specificstep.com.data.entity.BaseResponse;
import specificstep.com.data.exceptions.SignInException;
import specificstep.com.data.source.local.Pref;
import specificstep.com.data.utils.UserType;
import specificstep.com.exceptions.ErrorMessageFactory;
import specificstep.com.interactors.DefaultObserver;
import specificstep.com.interactors.exception.DefaultErrorBundle;
import specificstep.com.interactors.usecases.GetChildUserUseCase;
import specificstep.com.interactors.usecases.LoginUseCase;

public class SignInPresenter implements SignInContract.Presenter {

    private static final int MAX_RETRY = 3;
    private final SignInContract.View view;
    private final LoginUseCase loginUseCase;
    private final GetChildUserUseCase childUserUseCase;
    private final EncryptionUtil encryptionUtil;
    private final Pref pref;
    private boolean isPasswordVisible;
    private int noOfLoginRetry;
    private DatabaseHelper databaseHelper;
    private ArrayList<User> userArrayList;

    @Inject
    SignInPresenter(SignInContract.View view,
                    LoginUseCase loginUseCase,
                    GetChildUserUseCase childUserUseCase,
                    EncryptionUtil encryptionUtil, Pref pref) {
        this.view = view;
        this.loginUseCase = loginUseCase;
        this.childUserUseCase = childUserUseCase;
        this.encryptionUtil = encryptionUtil;
        this.pref = pref;
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
        if (loginUseCase != null) {
            loginUseCase.dispose();
        }
        if (childUserUseCase != null) {
            childUserUseCase.dispose();
        }
    }

    @Override
    public void onPasswordChanged(String password) {
        if (password.isEmpty()) {
            view.hidePasswordToggle();
            view.showPasswordIcon(R.drawable.ic_pwd);
        } else {
            view.showPasswordToggle();
            view.showPasswordIcon(R.drawable.ic_unlock);
        }
    }

    @Override
    public void onLoginButtonClicked(Context fragment) {
        if (checkValidations()) {
            doLogin(fragment);
        }
    }

    private void doLogin(Context context) {
        view.setProgressIndicator();
        databaseHelper = new DatabaseHelper(context);
        userArrayList = databaseHelper.getUserDetail();
        loginUseCase.execute(
                new DefaultObserver<BaseResponse>() {
                    @Override
                    public void onNext(BaseResponse value) {
                        super.onNext(value);
                        getChildUsers(view.getUserName(),UserType.DISTRIBUTOR.getType());
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.hideProgressIndicator();
                        if (e instanceof SignInException) {
                            noOfLoginRetry++;

                            if (noOfLoginRetry < 3) {
                                view.showRemainingSignInRetryPopup(MAX_RETRY - noOfLoginRetry);
                            } else {
                                view.showMaxLoginRetryPopup(MAX_RETRY);
                            }
                            return;
                        }
                        showErrorMessage(new DefaultErrorBundle((Exception) e));
                    }
                },
                LoginUseCase.Params.toParams(view.getUserName(), view.getPassword(), UserType.DISTRIBUTOR.getType(), view.isRememberPassword(), userArrayList.get(0).getDevice_id(), userArrayList.get(0).getOtp_code(), Constants.APP_VERSION));
    }

    @Override
    public void onMaxRetryPopupOkButtonClicked() {
        pref.removeValues(
                Pref.KEY_USER_ID,
                Pref.KEY_REMEMBER_PASSWORD,
                Pref.KEY_PASSWORD,
                Pref.KEY_IS_OTP_VERIFIED,
                Pref.KEY_MAX_AMOUNT_FILTER_VALUE,
                Pref.KEY_MIN_AMOUNT_FILTER_VALUE,
                Pref.KEY_OPT_CODE,
                Pref.KEY_SORTING_FILTER_VALUE,
                Pref.KEY_USER_NAME,
                Pref.KEY_USER_TYPE);
        view.showSignUpScreen();

    }

    private void getChildUsers(String userName, int userType) {
        childUserUseCase.execute(new DefaultObserver<List<UserList>>() {
            @Override
            public void onNext(List<UserList> value) {
                super.onNext(value);
                view.hideProgressIndicator();
                view.showMainScreen();
            }

            @Override
            public void onError(Throwable e) {
                //super.onError(e);
                view.hideProgressIndicator();
                //@kns.p
                //showErrorMessage(new DefaultErrorBundle((Exception) e));
                view.showMainScreen();
            }
        }, GetChildUserUseCase.Params.toParams(userName,userType));
    }

    private void showErrorMessage(DefaultErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(this.view.context(),
                errorBundle.getException());
        this.view.showErrorDialog(errorMessage);
    }

    private boolean checkValidations() {
        String userName = view.getUserName();
        String password = view.getPassword();

        if (userName.isEmpty()) {
            view.showErrorMessage(view.context().getString(R.string.enter_user_name));
            return false;
        } else if (password.isEmpty()) {
            view.showErrorMessage(view.context().getString(R.string.enter_password));
            return false;
        }
        return true;
    }

    @Override
    public void onRememberMeToggleChanged(boolean checked) {
        if (checked) {
            view.showConfirmRememberPopup();
        }
    }

    @Override
    public void onPasswordToggleVisible() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            showPassword();
        } else {
            hidePassword();
        }
    }

    @Override
    public void initialize(String userName) {
        if (TextUtils.isEmpty(userName) && pref.contains(Pref.KEY_USER_NAME)) {
            view.autoFillUserName(pref.getValue(Pref.KEY_USER_NAME, ""));
        }
        if(pref.getValue(Pref.KEY_REMEMBER_PASSWORD, false)) {
            view.selectRememberPasswordCheckBox();
            String encryptedPassword = pref.getValue(Pref.KEY_PASSWORD, "");
            if(!Strings.isNullOrEmpty(encryptedPassword)) {
                view.autoFillPassword(encryptionUtil.decrypt(encryptedPassword));
            }
        }
    }

    private void showPassword() {
        view.showPassword();
        view.showPasswordVisibilityIcon(R.drawable.ic_show_pwd);
    }

    private void hidePassword() {
        view.hidePassword();
        view.showPasswordVisibilityIcon(R.drawable.ic_hide_pwd);
    }
}
