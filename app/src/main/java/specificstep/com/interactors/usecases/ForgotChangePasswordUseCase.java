package specificstep.com.interactors.usecases;

import com.google.common.base.Preconditions;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.data.entity.BaseResponse;
import specificstep.com.interactors.executor.PostExecutionThread;
import specificstep.com.interactors.executor.ThreadExecutor;
import specificstep.com.interactors.repositories.UserRepository;

public class ForgotChangePasswordUseCase extends UseCase<BaseResponse, ForgotChangePasswordUseCase.Params> {

    private UserRepository userRepository;

    @Inject
    protected ForgotChangePasswordUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<BaseResponse> buildUseCaseObservable(Params params) {
        Preconditions.checkNotNull(params);
        return userRepository.forgotchnagepassword(params.username,params.otp_code,params.mac_address,params.app,params.forgot_otp,params.password, params.oldPassword);
    }

    public static final class Params {
        private String username;
        private String otp_code;
        private String mac_address;
        private String app;
        private String forgot_otp;
        private String password;
        private String oldPassword;

        Params(String username, String otp_code, String mac_address, String app, String forgot_otp, String password, String oldPassword) {
            this.username = username;
            this.otp_code = otp_code;
            this.mac_address = mac_address;
            this.app = app;
            this.forgot_otp = forgot_otp;
            this.password = password;
            this.oldPassword = oldPassword;
        }

        public static Params toParams(String username, String otp_code, String mac_address, String app, String forgot_otp, String password, String oldPassword) {
            return new Params(username, otp_code, mac_address, app, forgot_otp, password, oldPassword);
        }
    }
}
