package injection.module;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.lundie.michael.bakeit.ui.fragments.utils.DataUtils;

/**
 * Returns new singleton instance of the AppConstants method
 * (allowing access of constants defined in XML, requiring access via context)
 */
@Module
public class DataUtilsModule {

    @Provides
    @Singleton
    DataUtils provideDataUtils(Application application) {
        return new DataUtils(application);
    }
}