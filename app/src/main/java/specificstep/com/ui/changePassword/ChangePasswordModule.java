package specificstep.com.ui.changePassword;

import dagger.Module;
import dagger.Provides;

@Module
class ChangePasswordModule {

    private final ChangePasswordContract.View view;

    ChangePasswordModule(ChangePasswordContract.View view) {
        this.view = view;
    }

    @Provides
    ChangePasswordContract.View providesChangePasswordView() {
        return view;
    }

    @Provides
    ChangePasswordContract.Presenter providesChangePasswordPresenter(ChangePasswordPresenter presenter) {
        return presenter;
    }

}
