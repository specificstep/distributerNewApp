package specificstep.com.ui.changePassword;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.text.TextUtils;

import javax.inject.Inject;

import specificstep.com.GlobalClasses.Constants;
import specificstep.com.GlobalClasses.EncryptionUtil;
import specificstep.com.R;
import specificstep.com.data.entity.BaseResponse;
import specificstep.com.data.exceptions.InvalidAccessTokenException;
import specificstep.com.data.source.local.Pref;
import specificstep.com.exceptions.ErrorMessageFactory;
import specificstep.com.interactors.DefaultObserver;
import specificstep.com.interactors.exception.DefaultErrorBundle;
import specificstep.com.interactors.usecases.ChangePasswordUseCase;
import specificstep.com.utility.NotificationUtil;

class ChangePasswordPresenter implements ChangePasswordContract.Presenter {

    private final ChangePasswordContract.View view;
    private final Pref pref;
    private final EncryptionUtil encryptionUtil;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final NotificationUtil notificationUtil;

    private boolean isOldPasswordVisible, isNewPasswordVisible, isConfirmPasswordVisible;

    @Inject
    ChangePasswordPresenter(
            ChangePasswordContract.View view,
            Pref pref,
            EncryptionUtil encryptionUtil,
            ChangePasswordUseCase changePasswordUseCase, NotificationUtil notificationUtil) {
        this.view = view;
        this.pref = pref;
        this.encryptionUtil = encryptionUtil;
        this.changePasswordUseCase = changePasswordUseCase;
        this.notificationUtil = notificationUtil;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {
        changePasswordUseCase.dispose();
    }

    @Override
    public void initialize() {
        if (pref.getValue(Pref.KEY_REMEMBER_PASSWORD, false)) {
            String oldPassword = encryptionUtil.decrypt(pref.getValue(Pref.KEY_PASSWORD, ""));
            view.setOldPassword(oldPassword);
            view.showShowHideOldPasswordImageView();
        } else {
            view.setOldPassword("");
            view.hideShowHideOldPasswordImageView();
        }
    }

    private void hideOldPassword() {
        view.hideOldPassword();
        view.updateToggleOldPasswordIcon(R.drawable.ic_hide_pwd);
    }

    private void showOldPassword() {
        view.showOldPassword();
        view.updateToggleOldPasswordIcon(R.drawable.ic_show_pwd);
    }

    @Override
    public void onChangePasswordButtonClicked(FragmentActivity activity) {
        if (checkValidations()) {
            callChangePasswordAPI(activity);
        }
    }

    private void callChangePasswordAPI(final FragmentActivity activity) {
        view.showProgressIndicator();
        changePasswordUseCase.execute(
                new DefaultObserver<BaseResponse>() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onNext(BaseResponse value) {
                        super.onNext(value);
                        view.hideProgressIndicator();
                        notificationUtil.sendNotification(
                                        activity,Constants.changeAppName(activity),
                                        view.context().getString(R.string.password_change_success_message));
                        view.completeChangePassword();
                        view.showLoginScreen();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.hideProgressIndicator();
                        if(e instanceof InvalidAccessTokenException) {
                            view.showInvalidAccessTokenPopup();
                            return;
                        }
                        showErrorMessage(new DefaultErrorBundle((Exception) e));
                    }
                }, ChangePasswordUseCase.Params.toParams(view.getNewPassword(), view.getOldPassword()));
    }

    private void showErrorMessage(DefaultErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(this.view.context(),
                errorBundle.getException());
        this.view.showErrorDialog(errorMessage);
    }

    private boolean checkValidations() {
        String oldPassword = view.getOldPassword();
        String newPassword = view.getNewPassword();
        String confirmPassword = view.getConfirmPassword();
        // check empty
        if (oldPassword.length() == 0 || TextUtils.isEmpty(oldPassword)) {
            view.showError(view.context().getString(R.string.enter_old_password));
            return false;
        } else if (newPassword.length() == 0 || TextUtils.isEmpty(newPassword)) {
            view.showError(view.context().getString(R.string.enter_new_password));
            return false;
        } else if (confirmPassword.length() == 0 || TextUtils.isEmpty(confirmPassword)) {
            view.showError(view.context().getString(R.string.enter_confirm_password));
            return false;
        } else if (!TextUtils.equals(newPassword, confirmPassword)) {
            view.showError(view.context().getString(R.string.password_not_match));
            return false;
        } else if(TextUtils.equals(newPassword, oldPassword)) {
            view.showError(view.context().getString(R.string.error_message_enter_different_new_password));
            return false;
        }
        return true;
    }

    @Override
    public void toggleOldPasswordVisibility() {
        isOldPasswordVisible = !isOldPasswordVisible;
        if (isOldPasswordVisible) {
            showOldPassword();
        } else {
            hideOldPassword();
        }
    }

    @Override
    public void toggleNewPasswordVisibility() {
        isNewPasswordVisible = !isNewPasswordVisible;
        if (isNewPasswordVisible) {
            showNewPassword();
        } else {
            hideNewPassword();
        }
    }

    private void hideNewPassword() {
        view.hideNewPassword();
        view.updateToggleNewPasswordIcon(R.drawable.ic_hide_pwd);
    }

    private void showNewPassword() {
        view.showNewPassword();
        view.updateToggleNewPasswordIcon(R.drawable.ic_show_pwd);
    }

    @Override
    public void toggleConfirmPasswordVisibility() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
        if (isConfirmPasswordVisible) {
            showConfirmPassword();
        } else {
            hideConfirmPassword();
        }
    }

    private void hideConfirmPassword() {
        view.hideConfirmPassword();
        view.updateToggleConfirmPasswordIcon(R.drawable.ic_hide_pwd);
    }

    private void showConfirmPassword() {
        view.showConfirmPassword();
        view.updateToggleConfirmPasswordIcon(R.drawable.ic_show_pwd);
    }

    @Override
    public void onOldPasswordTextChanged(CharSequence password) {
        if (password.toString().isEmpty()) {
            view.hideShowHideOldPasswordImageView();
            view.showOldPasswordIcon(R.drawable.ic_pwd);
        } else {
            view.showOldPasswordIcon(R.drawable.ic_unlock);
            view.showShowHideOldPasswordImageView();
        }
    }

    @Override
    public void onNewPasswordTextChanged(CharSequence password) {
        if (password.toString().isEmpty()) {
            view.hideShowHideNewPasswordImageView();
            view.showNewPasswordIcon(R.drawable.ic_pwd);
        } else {
            view.showNewPasswordIcon(R.drawable.ic_unlock);
            view.showShowHideNewPasswordImageView();
        }
    }

    @Override
    public void onConfirmPasswordTextChanged(CharSequence password) {
        if (password.toString().isEmpty()) {
            view.hideShowHideConfirmPasswordImageView();
            view.showConfirmPasswordIcon(R.drawable.ic_pwd);
        } else {
            view.showConfirmPasswordIcon(R.drawable.ic_unlock);
            view.showShowHideConfirmPasswordImageView();
        }
    }
}
