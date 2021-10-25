package specificstep.com.ui.signup;

import dagger.Module;
import dagger.Provides;

@Module
public class SignUpPresenterModule {

    private final SignUpContract.View view;

    public SignUpPresenterModule(SignUpContract.View view) {
        this.view = view;
    }

    @Provides
    SignUpContract.View provideSignUpContractView() {
        return view;
    }
}
