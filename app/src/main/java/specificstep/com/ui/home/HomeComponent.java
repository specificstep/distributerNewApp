package specificstep.com.ui.home;

import dagger.Component;
import specificstep.com.di.FragmentScoped;
import specificstep.com.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = HomeModule.class)
public interface HomeComponent {

    void inject(HomeActivity homeActivity);
}
