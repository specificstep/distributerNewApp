package specificstep.com.ui.updateData;

import dagger.Module;
import dagger.Provides;

@Module
public class UpdateDataModule {

    private final UpdateDataContract.View view;

    public UpdateDataModule(UpdateDataContract.View view) {
        this.view = view;
    }

    @Provides
    UpdateDataContract.View providesUpdateDataPresenterView() {
        return view;
    }

    @Provides
    UpdateDataContract.Presenter providesUpdateDataPresenter(UpdateDataPresenter presenter) {
        return presenter;
    }
}
