package specificstep.com.ui.parentUser;

import dagger.Module;
import dagger.Provides;

@Module
class ParentUserModule {

    ParentUserContract.View view;

    public ParentUserModule(ParentUserContract.View view) {
        this.view = view;
    }

    @Provides
    ParentUserContract.View providesParentUserPresenterView() {
        return view;
    }

    @Provides
    ParentUserContract.Presenter providesParentUserPresenter(ParentUserPresenter presenter) {
        return presenter;
    }
}
