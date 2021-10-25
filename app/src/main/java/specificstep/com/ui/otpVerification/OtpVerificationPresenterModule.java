package specificstep.com.ui.otpVerification;

import dagger.Module;
import dagger.Provides;

@Module
public class OtpVerificationPresenterModule {

    private OtpVerificationContract.View view;

    public OtpVerificationPresenterModule(OtpVerificationContract.View view) {
        this.view = view;
    }

    @Provides
    OtpVerificationContract.View providesOtpVerificationContractView() {
        return view;
    }
}
