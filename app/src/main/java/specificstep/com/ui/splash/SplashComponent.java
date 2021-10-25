package specificstep.com.ui.splash;

import dagger.Component;
import specificstep.com.di.FragmentScoped;
import specificstep.com.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = SplashModule.class)
public interface SplashComponent {

    void inject(SplashActivity splashActivity);
}
