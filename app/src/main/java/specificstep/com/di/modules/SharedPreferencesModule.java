package specificstep.com.di.modules;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import specificstep.com.data.source.local.Pref;

@Module(includes = ApplicationModule.class)
public class SharedPreferencesModule {

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences(Pref.PREF_FILE, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    Pref providePref(SharedPreferences sharedPreferences) {
        return new Pref(sharedPreferences);
    }
}
