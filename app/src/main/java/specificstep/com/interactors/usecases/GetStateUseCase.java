package specificstep.com.interactors.usecases;

import com.google.common.base.Preconditions;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.Models.State;
import specificstep.com.interactors.executor.PostExecutionThread;
import specificstep.com.interactors.executor.ThreadExecutor;
import specificstep.com.interactors.repositories.UserRepository;

public class GetStateUseCase extends UseCase<List<State>, GetStateUseCase.Params> {

    private UserRepository userRepository;

    @Inject
    protected GetStateUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<List<State>> buildUseCaseObservable(Params params) {
        Preconditions.checkNotNull(params);
        return userRepository.getStates(params.serviceType);
    }

    public static final class Params {
        private int serviceType;

        Params(int serviceType) {
            this.serviceType = serviceType;
        }

        public static Params toParams(int serviceType) {
            return new Params(serviceType);
        }
    }
}
