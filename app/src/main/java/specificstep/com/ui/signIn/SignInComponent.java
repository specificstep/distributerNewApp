package specificstep.com.ui.signIn;


import dagger.Component;
import specificstep.com.di.FragmentScoped;
import specificstep.com.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = SignInPresenterModule.class)
public interface SignInComponent {

    void inject(SignInActivity signInActivity);
}
