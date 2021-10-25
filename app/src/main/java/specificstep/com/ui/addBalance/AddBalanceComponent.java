package specificstep.com.ui.addBalance;

import dagger.Component;
import specificstep.com.di.FragmentScoped;
import specificstep.com.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = AddBalanceModule.class)
public interface AddBalanceComponent {
    void inject(AddBalanceFragment fragment);
}
