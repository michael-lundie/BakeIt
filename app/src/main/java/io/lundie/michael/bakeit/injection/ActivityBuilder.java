package io.lundie.michael.bakeit.injection;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.lundie.michael.bakeit.ui.activities.MainActivity;

@Module
public abstract class ActivityBuilder {
    @ContributesAndroidInjector()
    abstract MainActivity bindMainActivity();

}