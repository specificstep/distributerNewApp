package specificstep.com.ui.home;

import dagger.Module;
import dagger.Provides;

@Module
class HomeModule {

    private HomeContract.View view;

    public HomeModule(HomeContract.View view) {
        this.view = view;
    }

    @Provides
    HomeContract.View providesHomePresenterView() {
        return view;
    }

    @Provides
    HomeContract.Presenter providePresenter(HomePresenter presenter) {
        return presenter;
    }
}
