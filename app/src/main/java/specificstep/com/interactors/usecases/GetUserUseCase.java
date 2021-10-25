package specificstep.com.interactors.usecases;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.Models.ChildUserModel;
import specificstep.com.interactors.executor.PostExecutionThread;
import specificstep.com.interactors.executor.ThreadExecutor;
import specificstep.com.interactors.repositories.UserRepository;

public class GetUserUseCase extends UseCase<ChildUserModel, GetUserUseCase.Params> {

    private UserRepository userRepository;

    @Inject
    protected GetUserUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<ChildUserModel> buildUseCaseObservable(GetUserUseCase.Params params) {
        return userRepository.getChildUser(params.email);
    }

    public static final class Params {
        private String email;

        Params(String email) {
            this.email = email;
        }

        public static GetUserUseCase.Params toParams(String email) {
            return new GetUserUseCase.Params(email);
        }
    }
}
