package io.lundie.michael.bakeit.injection.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.lundie.michael.bakeit.ui.fragments.RecipesFragment;
import io.lundie.michael.bakeit.ui.fragments.StepDetailsFragment;
import io.lundie.michael.bakeit.ui.fragments.StepsFragment;

@Module
public abstract class FragmentModule {
    @ContributesAndroidInjector
    abstract RecipesFragment contributeRecipesFragment();

    @ContributesAndroidInjector
    abstract StepsFragment contributeStepsFragment();

    @ContributesAndroidInjector
    abstract StepDetailsFragment contributeStepDetailsFragment();
}
