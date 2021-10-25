package specificstep.com.interactors.usecases;

import com.google.common.base.Preconditions;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.Models.Company;
import specificstep.com.interactors.executor.PostExecutionThread;
import specificstep.com.interactors.executor.ThreadExecutor;
import specificstep.com.interactors.repositories.UserRepository;

public class GetCompanyUseCase extends UseCase<List<Company>, GetCompanyUseCase.Params> {

    private UserRepository userRepository;

    @Inject
    protected GetCompanyUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<List<Company>> buildUseCaseObservable(Params params) {
        Preconditions.checkNotNull(params);
        return userRepository.getCompanyList(params.serviceType);
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
