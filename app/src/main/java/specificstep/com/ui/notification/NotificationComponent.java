package specificstep.com.ui.notification;

import dagger.Component;
import specificstep.com.di.FragmentScoped;
import specificstep.com.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = NotificationModule.class)
public interface NotificationComponent {
    void inject(NotificationFragment fragment);
}
