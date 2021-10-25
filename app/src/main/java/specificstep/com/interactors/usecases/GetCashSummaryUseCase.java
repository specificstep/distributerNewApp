package specificstep.com.interactors.usecases;

import com.google.common.base.Preconditions;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.Models.CashSummaryModel;
import specificstep.com.interactors.executor.PostExecutionThread;
import specificstep.com.interactors.executor.ThreadExecutor;
import specificstep.com.interactors.repositories.UserRepository;

public class GetCashSummaryUseCase extends UseCase<List<CashSummaryModel>, GetCashSummaryUseCase.Params> {

    private UserRepository userRepository;

    @Inject
    protected GetCashSummaryUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<List<CashSummaryModel>> buildUseCaseObservable(Params params) {
        Preconditions.checkNotNull(params);
        return userRepository.getCashSummary(params.userId, params.userType, params.fromDate, params.toDate);
    }

    public static final class Params {
        private String userId;
        private int userType;
        private String fromDate, toDate;

        Params(String userId, int userType, String fromDate, String toDate) {
            this.userId = userId;
            this.userType = userType;
            this.fromDate = fromDate;
            this.toDate = toDate;
        }

        public static Params toParams(String userName, int userType, String fromDate, String toDate) {
            return new Params(userName, userType, fromDate, toDate);
        }
    }
}
