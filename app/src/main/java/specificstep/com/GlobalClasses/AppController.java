package specificstep.com.GlobalClasses;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import specificstep.com.di.components.ApplicationComponent;
import specificstep.com.di.components.DaggerApplicationComponent;
import specificstep.com.di.modules.ApplicationModule;
import specificstep.com.utility.NotificationUtil;

/**
 * Created by admin1 on 21/3/16.
 */

public class AppController extends Application {

    @Inject
    NotificationUtil notificationUtil;
    private ApplicationComponent applicationComponent;

    public NotificationUtil getNotificationUtil() {
        return notificationUtil;
    }
    public static AppController instance;

    @Override
    public void onCreate() {
        super.onCreate();
        /*report crash if any issues with app */
        Fabric.with(this, new Crashlytics());

        applicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this))
                .build();
        applicationComponent.inject(this);
        instance = this;
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static AppController getInstance() {
        return instance;
    }


}
