package injection.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.lundie.michael.bakeit.ui.fragments.IngredientsFragment;
import io.lundie.michael.bakeit.ui.fragments.RecipesFragment;
import io.lundie.michael.bakeit.ui.fragments.StepDetailsFragment;
import io.lundie.michael.bakeit.ui.fragments.StepsFragment;

@Module
public abstract class FragmentModule {
    @ContributesAndroidInjector
    abstract RecipesFragment contributeRecipesFragment();

    @ContributesAndroidInjector
    abstract StepsFragment contributeStepsFragment();

    @ContributesAndroidInjector(modules = IngredientsViewAdapterModule.class )
    abstract IngredientsFragment contributeIngredientsFragment();

    @ContributesAndroidInjector
    abstract StepDetailsFragment contributeStepDetailsFragment();
}
