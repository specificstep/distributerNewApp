package specificstep.com.ui.forgotPassword;

import dagger.Module;
import dagger.Provides;

@Module
public class ForgotPasswordModule {

    private final ForgotPasswordContract.View view;

    ForgotPasswordModule(ForgotPasswordContract.View view) {
        this.view = view;
    }

    @Provides
    ForgotPasswordContract.View providesForgotPasswordView() {
        return view;
    }

    @Provides
    ForgotPasswordContract.Presenter providesForgotPasswordPresenter(ForgotPasswordPresenter presenter) {
        return presenter;
    }

}
