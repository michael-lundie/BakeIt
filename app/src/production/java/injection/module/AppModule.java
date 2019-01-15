package injection.module;

import android.app.Application;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.lundie.michael.bakeit.datamodel.RecipeRepository;
import io.lundie.michael.bakeit.datamodel.RecipeRepositoryMain;
import io.lundie.michael.bakeit.utilities.AssetProvider;
import io.lundie.michael.bakeit.utilities.Prefs;
import io.lundie.michael.bakeit.utilities.SimpleLruCache;

/**
 * Primary AppModule allowing various classes and methods to be injected into our app.
 * This is my first time to use dagger and I'm still learning the ins and outs. Any help/crit
 * is greatly encouraged.
 * Tutorial followed from:
 * https://blog.mindorks.com/the-new-dagger-2-android-injector-cbe7d55afa6a
 */
@Module(includes = {ViewModelModule.class,
                    AppConstantsModule.class,
                    SharedPreferencesModule.class,
                    DataUtilsModule.class})
public class AppModule {

    private static final String LOG_TAG = AppModule.class.getSimpleName();

    // Provides AssetProvider
    @Provides
    @Singleton
    AssetProvider localFileProvider(Application application) {
        return new AssetProvider(application);
    }

    // Preference Injection
    @Provides
    @Singleton
    Prefs providePrefs(SharedPreferences sharedPrefs) {
        return new Prefs(sharedPrefs);
    }

    // Provides GSON
    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }

    // Repo Injection
    @Provides
    @Singleton
    RecipeRepository provideRecipeRepository(Gson gson, AssetProvider assetProvider, SimpleLruCache lruCache) {
        return new RecipeRepositoryMain(gson, assetProvider, lruCache);
    }

    // LRU Cache instance injection
    @Provides
    @Singleton
    SimpleLruCache lruCache() {
        return new SimpleLruCache();
    }
}