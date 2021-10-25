package specificstep.com.di.components;


import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import specificstep.com.Database.ChildUserTable;
import specificstep.com.Database.NotificationTable;
import specificstep.com.GlobalClasses.AppController;
import specificstep.com.GlobalClasses.EncryptionUtil;
import specificstep.com.data.source.local.Pref;
import specificstep.com.di.modules.ApplicationModule;
import specificstep.com.di.modules.SharedPreferencesModule;
import specificstep.com.interactors.executor.PostExecutionThread;
import specificstep.com.interactors.executor.ThreadExecutor;
import specificstep.com.interactors.repositories.UserRepository;
import specificstep.com.utility.NotificationUtil;

/**
 * A component whose lifetime is the life of the application.
 */
@Singleton // Constraints this component to one-per-application or unscoped bindings.
@Component(modules = {
        ApplicationModule.class,
        SharedPreferencesModule.class
})
public interface ApplicationComponent {

    //Exposed to sub-graphs.
    Context context();
    ThreadExecutor threadExecutor();
    PostExecutionThread postExecutionThread();
    UserRepository userRepository();
    Pref pref();
    NotificationTable notificationTable();
    EncryptionUtil encryptionUtils();
    ChildUserTable childUserTable();
    NotificationUtil notificationUtil();

    void inject(AppController appController);
}
