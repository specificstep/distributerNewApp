package specificstep.com.interactors.usecases;

import com.google.common.base.Preconditions;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.data.entity.BaseResponse;
import specificstep.com.interactors.executor.PostExecutionThread;
import specificstep.com.interactors.executor.ThreadExecutor;
import specificstep.com.interactors.repositories.UserRepository;

public class LoginUseCase extends UseCase<BaseResponse, LoginUseCase.Params> {

    private UserRepository userRepository;

    @Inject
    protected LoginUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<BaseResponse> buildUseCaseObservable(Params params) {
        Preconditions.checkNotNull(params);
        return userRepository.login(params.userName, params.password, params.userType, params.rememberPassword, params.mac_address, params.otp_code, params.app);
    }

    public static final class Params {
        private String userName;
        private String password;
        private int userType;
        private boolean rememberPassword;
        private String mac_address;
        private String otp_code;
        private String app;

        Params(String userName, String password, int userType, boolean rememberPassword, String mac_address, String otp_code, String app) {
            this.userName = userName;
            this.userType = userType;
            this.password = password;
            this.rememberPassword = rememberPassword;
            this.mac_address = mac_address;
            this.otp_code = otp_code;
            this.app = app;
        }

        public static Params toParams(String userName, String password, int userType, boolean rememberPassword, String mac_address, String otp_code, String app) {
            return new Params(userName, password, userType, rememberPassword,mac_address,otp_code,app);
        }
    }
}
