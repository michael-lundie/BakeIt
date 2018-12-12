package io.lundie.michael.bakeit.injection.component;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import io.lundie.michael.bakeit.App;
import io.lundie.michael.bakeit.injection.ActivityBuilder;
import io.lundie.michael.bakeit.injection.module.AppModule;

/**
 * App component interface for dagger injections methods.
 */
@Singleton
@Component(modules = {  AndroidInjectionModule.class,
                        AppModule.class,
                        ActivityBuilder.class  })
public interface AppComponent {

    @Component.Builder
    interface Builder {
        AppComponent build();

        @BindsInstance
        Builder application(Application application);
    }
    void inject(App app);
}