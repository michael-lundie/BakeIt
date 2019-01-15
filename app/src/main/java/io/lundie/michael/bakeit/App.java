package io.lundie.michael.bakeit;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import io.lundie.michael.bakeit.injection.component.AppComponent;
import io.lundie.michael.bakeit.injection.component.DaggerAppComponent;

public class App extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> androidInjector;

    AppComponent daggerAppComponent;

    public Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        daggerAppComponent = DaggerAppComponent.builder().application(this).build();
        daggerAppComponent.inject(this);
        context = getApplicationContext();
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return androidInjector;
    }

    public AppComponent getDaggerAppComponent() {
        return daggerAppComponent;
    }
}
