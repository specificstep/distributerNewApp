package specificstep.com.interactors.executor;


import java.util.concurrent.Executor;

import specificstep.com.interactors.usecases.UseCase;

/**
 * Executor implementation can be based on different frameworks or techniques of asynchronous
 * execution, but every implementation will execute the
 * {@link UseCase} out of the UI thread.
 */
public interface ThreadExecutor extends Executor {
}
