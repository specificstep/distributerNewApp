package specificstep.com.interactors.usecases;

import com.google.common.base.Preconditions;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.data.entity.AutoOtpEntity;
import specificstep.com.interactors.executor.PostExecutionThread;
import specificstep.com.interactors.executor.ThreadExecutor;
import specificstep.com.interactors.repositories.UserRepository;

public class AutoOtpUseCase extends UseCase<List<AutoOtpEntity>, AutoOtpUseCase.Params> {

    private UserRepository userRepository;

    @Inject
    protected AutoOtpUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<List<AutoOtpEntity>> buildUseCaseObservable(AutoOtpUseCase.Params params) {
        Preconditions.checkNotNull(params);
        return userRepository.autoOtp(params.userName, params.userType);
    }

    public static final class Params {

        private String userName;
        private  int userType;

        Params(String userName, int userType) {
            this.userName = userName;
            this.userType = userType;
        }

        public static AutoOtpUseCase.Params toParams(String userName, int userType) {
            return new AutoOtpUseCase.Params(userName, userType);
        }
    }

}
