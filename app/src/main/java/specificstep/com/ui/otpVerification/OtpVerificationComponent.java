package specificstep.com.ui.otpVerification;

import dagger.Component;
import specificstep.com.di.FragmentScoped;
import specificstep.com.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = OtpVerificationPresenterModule.class)
public interface OtpVerificationComponent {
    void inject(OtpVerificationActivity activity);
}
