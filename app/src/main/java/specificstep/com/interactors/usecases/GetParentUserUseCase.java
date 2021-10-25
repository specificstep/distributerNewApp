package specificstep.com.interactors.usecases;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.Models.ParentUser;
import specificstep.com.interactors.executor.PostExecutionThread;
import specificstep.com.interactors.executor.ThreadExecutor;
import specificstep.com.interactors.repositories.UserRepository;

public class GetParentUserUseCase extends UseCase<ParentUser, Void> {

    private UserRepository userRepository;

    @Inject
    protected GetParentUserUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<ParentUser> buildUseCaseObservable(Void aVoid) {
        return userRepository.getParentUsers();
    }
}
