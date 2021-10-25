package specificstep.com.ui.signup;

import android.os.Bundle;

import javax.inject.Inject;

import specificstep.com.GlobalClasses.AppController;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.R;
import specificstep.com.ui.base.ToolBarActivity;
import specificstep.com.ui.signup.DaggerSignUpComponent;

public class SignUpActivity extends ToolBarActivity<SignUpFragment> {

    @Inject
    SignUpPresenter signUpPresenter;

    @Override
    public SignUpFragment getFragmentContent() {
        return new SignUpFragment();
    }

    @Override
    public void injectDependencies(SignUpFragment fragment) {

        DaggerSignUpComponent.builder()
                .applicationComponent(((AppController) getApplication()).getApplicationComponent())
                .signUpPresenterModule(new SignUpPresenterModule(fragment))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setLogo(Constants.changeActionbarLogo(SignUpActivity.this));
        toolbar.setTitle(R.string.register_app_title);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
