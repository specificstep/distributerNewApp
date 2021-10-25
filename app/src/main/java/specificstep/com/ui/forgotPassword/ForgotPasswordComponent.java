package specificstep.com.ui.forgotPassword;

import dagger.Component;
import specificstep.com.di.FragmentScoped;
import specificstep.com.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = ForgotPasswordModule.class)
public interface ForgotPasswordComponent {
    void inject(ForgotPasswordActivity fragment);

}
