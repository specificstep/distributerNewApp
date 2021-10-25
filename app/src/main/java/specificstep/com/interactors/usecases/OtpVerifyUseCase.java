package specificstep.com.interactors.usecases;

import com.google.common.base.Preconditions;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.data.entity.BaseResponse;
import specificstep.com.interactors.executor.PostExecutionThread;
import specificstep.com.interactors.executor.ThreadExecutor;
import specificstep.com.interactors.repositories.UserRepository;

public class OtpVerifyUseCase extends UseCase<BaseResponse, OtpVerifyUseCase.Params> {

    private UserRepository userRepository;

    @Inject
    protected OtpVerifyUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<BaseResponse> buildUseCaseObservable(Params params) {
        Preconditions.checkNotNull(params);
        return userRepository.verifyOTP(params.userName, params.otp, params.userType);
    }

    public static final class Params {
        private String userName;
        private String otp;
        private int userType;

        Params(String userName, String otp, int userType) {
            this.userName = userName;
            this.otp = otp;
            this.userType = userType;
        }

        public static Params toParams(String userName, String otp, int userType) {
            return new Params(userName, otp, userType);
        }
    }
}
