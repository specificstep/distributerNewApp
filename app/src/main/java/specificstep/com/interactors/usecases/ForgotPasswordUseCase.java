package specificstep.com.interactors.usecases;

import com.google.common.base.Preconditions;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.data.entity.BaseResponse;
import specificstep.com.interactors.executor.PostExecutionThread;
import specificstep.com.interactors.executor.ThreadExecutor;
import specificstep.com.interactors.repositories.UserRepository;

public class ForgotPasswordUseCase extends UseCase<BaseResponse, ForgotPasswordUseCase.Params> {

    private UserRepository userRepository;

    @Inject
    protected ForgotPasswordUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<BaseResponse> buildUseCaseObservable(Params params) {
        Preconditions.checkNotNull(params);
        return userRepository.forgotpassword(params.userName, params.mac_address, params.otp_code, params.app);
    }

    public static final class Params {
        private String userName;
        private String mac_address;
        private String otp_code;
        private String app;

        Params(String userName, String mac_address, String otp_code, String app) {
            this.userName = userName;
            this.mac_address = mac_address;
            this.otp_code = otp_code;
            this.app = app;
        }

        public static Params toParams(String userName, String mac_address, String otp_code, String app) {
            return new Params(userName, mac_address, otp_code, app);
        }
    }
}
