package specificstep.com.ui.updateData;

import dagger.Component;
import specificstep.com.di.FragmentScoped;
import specificstep.com.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = UpdateDataModule.class)
public interface UpdateDataComponent {
    void inject(UpdateDataFragment fragment);
}
