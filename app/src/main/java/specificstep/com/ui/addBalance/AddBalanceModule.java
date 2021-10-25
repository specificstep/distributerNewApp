package specificstep.com.ui.addBalance;

import dagger.Module;
import dagger.Provides;

@Module
public class AddBalanceModule {
    AddBalanceContract.View view;

    public AddBalanceModule(AddBalanceContract.View view) {
        this.view = view;
    }

    @Provides
    AddBalanceContract.View providesAddBalancePresenterView() {
        return view;
    }

    @Provides
    AddBalanceContract.Presenter providesAddBalancePresenter(AddBalancePresenter presenter) {
        return presenter;
    }
}
