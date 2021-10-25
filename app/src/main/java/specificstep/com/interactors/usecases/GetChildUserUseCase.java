package specificstep.com.interactors.usecases;

import com.google.common.base.Preconditions;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.Models.UserList;
import specificstep.com.interactors.executor.PostExecutionThread;
import specificstep.com.interactors.executor.ThreadExecutor;
import specificstep.com.interactors.repositories.UserRepository;

public class GetChildUserUseCase extends UseCase<List<UserList>, GetChildUserUseCase.Params> {

    private UserRepository userRepository;

    @Inject
    protected GetChildUserUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<List<UserList>> buildUseCaseObservable(Params params) {
        Preconditions.checkNotNull(params);
        return userRepository.getChildUsers(params.userName,params.userType);
    }

    public static final class Params {
        private String userName;
        private int userType;

        Params(String userName, int userType) {
            this.userName = userName;
            this.userType = userType;
        }

        public static GetChildUserUseCase.Params toParams(String userName, int userType) {
            return new GetChildUserUseCase.Params(userName, userType);
        }
    }

}
