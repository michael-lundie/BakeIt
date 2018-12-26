package io.lundie.michael.bakeit.injection;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.lundie.michael.bakeit.injection.module.FragmentModule;
import io.lundie.michael.bakeit.ui.activities.LauncherActivity;
import io.lundie.michael.bakeit.ui.activities.RecipeActivity;

@Module
public abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract LauncherActivity bindMainActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract RecipeActivity bindRecipeActivity();
}