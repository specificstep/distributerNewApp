package specificstep.com.interactors.usecases;

import com.google.common.base.Preconditions;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.data.entity.ForgotPasswordResponse;
import specificstep.com.interactors.executor.PostExecutionThread;
import specificstep.com.interactors.executor.ThreadExecutor;
import specificstep.com.interactors.repositories.UserRepository;

public class ForgotOtpVerifyUseCase  extends UseCase<ForgotPasswordResponse, ForgotOtpVerifyUseCase.Params> {

    private UserRepository userRepository;

    @Inject
    protected ForgotOtpVerifyUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<ForgotPasswordResponse> buildUseCaseObservable(ForgotOtpVerifyUseCase.Params params) {
        Preconditions.checkNotNull(params);
        return userRepository.verifyForgotOTP(params.username, params.otp_code, params.mac_address, params.app, params.forgot_otp);
    }

    public static final class Params {
        private String username;
        private String otp_code;
        private String mac_address;
        private String app;
        private String forgot_otp;

        Params(String username, String otp_code, String mac_address, String app, String forgot_otp) {
            this.username = username;
            this.otp_code = otp_code;
            this.mac_address = mac_address;
            this.app = app;
            this.forgot_otp = forgot_otp;
        }

        public static ForgotOtpVerifyUseCase.Params toParams(String username, String otp_code, String mac_address, String app, String forgot_otp) {
            return new ForgotOtpVerifyUseCase.Params(username, otp_code, mac_address, app, forgot_otp);
        }
    }

}
