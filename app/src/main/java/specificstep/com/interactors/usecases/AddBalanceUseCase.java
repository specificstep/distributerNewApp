package specificstep.com.interactors.usecases;

import com.google.common.base.Preconditions;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.interactors.executor.PostExecutionThread;
import specificstep.com.interactors.executor.ThreadExecutor;
import specificstep.com.interactors.repositories.UserRepository;

public class AddBalanceUseCase extends UseCase<String, AddBalanceUseCase.Params> {

    private UserRepository userRepository;

    @Inject
    protected AddBalanceUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<String> buildUseCaseObservable(Params params) {
        Preconditions.checkNotNull(params);
        return userRepository.addBalance(params.userId, params.amount, params.lati, params.lng);
    }

    public static final class Params {
        private String userId;
        private String amount;
        private String lati;
        private String lng;

        Params(String userId, String amount, String lati, String lng) {
            this.userId = userId;
            this.amount = amount;
            this.lati = lati;
            this.lng = lng;
        }

        public static Params toParams(String userName, String amount, String lati, String lng) {
            return new Params(userName, amount, lati, lng);
        }
    }
}
