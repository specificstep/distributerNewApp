package specificstep.com.ui.parentUser;

import dagger.Component;
import specificstep.com.di.FragmentScoped;
import specificstep.com.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = ParentUserModule.class)
public interface ParentUserComponent {
    void inject(ParentUserFragment fragment);
}
