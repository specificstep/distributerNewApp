package specificstep.com.ui.dashboard;


import dagger.Module;
import dagger.Provides;

@Module
public class DashboardModule {

    private final DashboardContract.View view;

    public DashboardModule(DashboardContract.View view) {
        this.view = view;
    }

    @Provides
    DashboardContract.View providesDashboardPresenterView() {
        return this.view;
    }
}
