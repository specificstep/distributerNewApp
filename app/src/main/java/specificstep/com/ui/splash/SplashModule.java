package specificstep.com.ui.splash;

import dagger.Module;
import dagger.Provides;

@Module
public class SplashModule {

    private final SplashContract.View view;

    public SplashModule(SplashContract.View view) {
        this.view = view;
    }

    @Provides
    SplashContract.View providesSplashPresenterView() {
        return view;
    }
}
