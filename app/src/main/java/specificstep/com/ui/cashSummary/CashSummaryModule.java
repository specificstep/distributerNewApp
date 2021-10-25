package specificstep.com.ui.cashSummary;

import dagger.Module;
import dagger.Provides;

@Module
class CashSummaryModule {

    private CashSummaryContract.View view;

    public CashSummaryModule(CashSummaryContract.View view) {
        this.view = view;
    }

    @Provides
    CashSummaryContract.View providesCashSummaryPresenterView() {
        return view;
    }

    @Provides
    CashSummaryContract.Presenter providesCashSummaryPresenter(CashSummaryPresenter presenter) {
        return presenter;
    }
}
