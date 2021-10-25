package specificstep.com.ui.splash;

import javax.inject.Inject;

import specificstep.com.GlobalClasses.AppController;
import specificstep.com.ui.base.BaseFullScreenActivity;
import specificstep.com.ui.splash.DaggerSplashComponent;

public class SplashActivity extends BaseFullScreenActivity<SplashFragment> {

    @Inject
    SplashPresenter presenter;

    @Override
    public SplashFragment getFragmentContent() {
        return new SplashFragment();
    }

    @Override
    public void injectDependencies(SplashFragment fragment) {
        DaggerSplashComponent.builder()
                .applicationComponent(((AppController)getApplication()).getApplicationComponent())
                .splashModule(new SplashModule(fragment))
                .build().inject(this);
    }

}
