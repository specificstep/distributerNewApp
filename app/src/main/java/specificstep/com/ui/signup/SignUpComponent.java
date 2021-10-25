package specificstep.com.ui.signup;

import dagger.Component;
import specificstep.com.di.FragmentScoped;
import specificstep.com.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = SignUpPresenterModule.class)
public interface SignUpComponent {

    void inject(SignUpActivity signUpActivity);
}

