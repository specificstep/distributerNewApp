package specificstep.com.interactors.usecases;

import com.google.common.base.Preconditions;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.data.entity.BaseResponse;
import specificstep.com.interactors.executor.PostExecutionThread;
import specificstep.com.interactors.executor.ThreadExecutor;
import specificstep.com.interactors.repositories.UserRepository;

public class ChangePasswordUseCase extends UseCase<BaseResponse, ChangePasswordUseCase.Params> {

    private UserRepository userRepository;

    @Inject
    protected ChangePasswordUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<BaseResponse> buildUseCaseObservable(Params params) {
        Preconditions.checkNotNull(params);
        return userRepository.changePassword(params.password, params.oldPassword);
    }

    public static final class Params {
        private String password;
        private String oldPassword;

        Params(String password, String oldPassword) {
            this.password = password;
            this.oldPassword = oldPassword;
        }

        public static Params toParams(String password, String oldPassword) {
            return new Params(password, oldPassword);
        }
    }
}
