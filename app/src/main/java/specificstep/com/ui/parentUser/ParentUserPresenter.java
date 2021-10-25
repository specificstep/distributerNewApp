package specificstep.com.ui.parentUser;

import android.text.TextUtils;

import javax.inject.Inject;

import specificstep.com.Models.ParentUser;
import specificstep.com.R;
import specificstep.com.data.exceptions.InvalidAccessTokenException;
import specificstep.com.exceptions.ErrorMessageFactory;
import specificstep.com.interactors.DefaultObserver;
import specificstep.com.interactors.exception.DefaultErrorBundle;
import specificstep.com.interactors.usecases.GetParentUserUseCase;

public class ParentUserPresenter implements ParentUserContract.Presenter {

    private final ParentUserContract.View view;
    private final GetParentUserUseCase getParentUserUseCase;

    @Inject
    public ParentUserPresenter(ParentUserContract.View view, GetParentUserUseCase getParentUserUseCase) {
        this.view = view;
        this.getParentUserUseCase = getParentUserUseCase;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {
        if (getParentUserUseCase != null) {
            getParentUserUseCase.dispose();
        }
    }

    @Override
    public void initialize() {
        getParentUsers();
    }

    private void getParentUsers() {
        view.showLoadingView();
        getParentUserUseCase.execute(new DefaultObserver<ParentUser>() {
            @Override
            public void onNext(ParentUser value) {
                super.onNext(value);
                view.hideLoadingView();
                onParentUserReceived(value);
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
        }, null);

    }

    private void onParentUserReceived(ParentUser value) {
        if(value.getParentUsers() != null && value.getParentUsers().size() > 0) {
            view.showListContainer();
            view.hideNoDataLabel();
            view.setUpAdapter(value.getParentUsers());
        }else {
            view.hideListContainer();
            view.showNoDataLabel();
        }

        view.setFirmName(view.context().getString(R.string.firm_name_format, value.getFirmName()));
        if (!TextUtils.isEmpty(value.getFirstName()) && !TextUtils.isEmpty(value.getLastName())) {
            view.setName(view.context().getString(R.string.name_format, value.getFirstName() + " " + value.getLastName()));
        } else {
            view.setName(view.context().getString(R.string.name_format, " - "));
        }
        view.setMobileNumber(view.context().getString(R.string.mobile_number_format, value.getPhoneNumber()));
        view.setUserType(view.context().getString(R.string.user_type_format, value.getUserType()));


    }

    private void showErrorMessage(DefaultErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(this.view.context(),
                errorBundle.getException());
        this.view.showInfoDialog(errorMessage);
    }
}
