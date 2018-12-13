package io.lundie.michael.bakeit.injection.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.lundie.michael.bakeit.ui.fragments.RecipesFragment;

@Module
public abstract class FragmentModule {
    @ContributesAndroidInjector
    abstract RecipesFragment contributeRecipesFragment();
}
