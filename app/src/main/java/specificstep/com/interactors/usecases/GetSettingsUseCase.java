package specificstep.com.interactors.usecases;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.Models.Color;
import specificstep.com.interactors.executor.PostExecutionThread;
import specificstep.com.interactors.executor.ThreadExecutor;
import specificstep.com.interactors.repositories.UserRepository;

public class GetSettingsUseCase extends UseCase<List<Color>, Void> {

    private UserRepository userRepository;

    @Inject
    protected GetSettingsUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<List<Color>> buildUseCaseObservable(Void aVoid) {
        return userRepository.getSettings();
    }
}
