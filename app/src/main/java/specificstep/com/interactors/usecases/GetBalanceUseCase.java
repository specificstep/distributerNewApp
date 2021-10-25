package specificstep.com.interactors.usecases;

import java.math.BigDecimal;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.interactors.executor.PostExecutionThread;
import specificstep.com.interactors.executor.ThreadExecutor;
import specificstep.com.interactors.repositories.UserRepository;

public class GetBalanceUseCase extends UseCase<BigDecimal, Void> {

    private UserRepository userRepository;

    @Inject
    protected GetBalanceUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<BigDecimal> buildUseCaseObservable(Void aVoid) {
        return userRepository.getBalance();
    }
}
