package specificstep.com.ui.cashSummary;

import dagger.Component;
import specificstep.com.di.FragmentScoped;
import specificstep.com.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = CashSummaryModule.class)
public interface CashSummaryComponent {
    void inject(CashSummaryFragment fragment);
}
