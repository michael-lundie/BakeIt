package io.lundie.michael.bakeit.injection;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.lundie.michael.bakeit.injection.module.FragmentModule;
import io.lundie.michael.bakeit.ui.activities.MainActivity;

@Module
public abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract MainActivity bindMainActivity();

}