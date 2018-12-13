package io.lundie.michael.bakeit.injection.module;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.lundie.michael.bakeit.utilities.AppConstants;

/**
 * Returns new singleton instance of the AppConstants method
 * (allowing access of constants defined in XML, requiring access via context)
 */
@Module
public class AppConstantsModule {

    @Provides
    @Singleton
    AppConstants provideAppConstants(Application application) {
        return new AppConstants(application);
    }
}