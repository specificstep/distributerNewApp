package specificstep.com.ui.forgotOtpVarification;

import dagger.Module;
import dagger.Provides;

@Module
public class ForgotOtpVerificationPresenterModule {

    private ForgotOtpVerificationContract.View view;

    public ForgotOtpVerificationPresenterModule(ForgotOtpVerificationContract.View view) {
        this.view = view;
    }

    @Provides
    ForgotOtpVerificationContract.View providesForgotOtpVerificationContractView() {
        return view;
    }

}
