package specificstep.com.ui.changePassword;

import dagger.Component;
import specificstep.com.di.FragmentScoped;
import specificstep.com.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = ChangePasswordModule.class)
public interface ChangePasswordComponent {
    void inject(ChangePasswordFragment fragment);
}
