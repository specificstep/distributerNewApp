package specificstep.com.ui.dashboard;

import dagger.Component;
import specificstep.com.di.FragmentScoped;
import specificstep.com.di.components.ApplicationComponent;

@FragmentScoped
@Component(modules = DashboardModule.class, dependencies = ApplicationComponent.class)
public interface DashboardComponent {
    void inject(DashboardActivity activity);
}
