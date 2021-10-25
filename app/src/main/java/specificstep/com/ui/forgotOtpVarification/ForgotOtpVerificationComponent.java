package specificstep.com.ui.forgotOtpVarification;

import dagger.Component;
import specificstep.com.di.FragmentScoped;
import specificstep.com.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = ForgotOtpVerificationPresenterModule.class)

public interface ForgotOtpVerificationComponent {
    void inject(ForgotOtpVarificationActivity activity);
}
