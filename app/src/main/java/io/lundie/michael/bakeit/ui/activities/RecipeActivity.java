package io.lundie.michael.bakeit.ui.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.LayoutAnimationController;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.lundie.michael.bakeit.App;
import io.lundie.michael.bakeit.R;
import io.lundie.michael.bakeit.datamodel.models.Ingredient;
import io.lundie.michael.bakeit.datamodel.models.Recipe;
import io.lundie.michael.bakeit.datamodel.models.RecipeStep;
import io.lundie.michael.bakeit.ui.fragments.RecipePagerFragment;
import io.lundie.michael.bakeit.ui.fragments.RecipesFragment;
import io.lundie.michael.bakeit.ui.fragments.StepDetailsFragment;
import io.lundie.michael.bakeit.ui.fragments.StepsFragment;
import io.lundie.michael.bakeit.ui.fragments.utils.DataUtils;
import io.lundie.michael.bakeit.utilities.AppConstants;
import io.lundie.michael.bakeit.utilities.Prefs;
import io.lundie.michael.bakeit.viewmodel.RecipesViewModel;
import io.lundie.michael.bakeit.widget.IngredientsWidgetProvider;

/**
 * Activity class responsible for several child fragments. Note we are using Live Data observers
 * to drive communication between fragments, so our life cycle is a little different than usual.
 */
public class RecipeActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    private static final String LOG_TAG = RecipeActivity.class.getName();

    @Inject DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
    @Inject ViewModelProvider.Factory recipesViewModelFactory;
    @Inject AppConstants appConstants;
    @Inject DataUtils dataUtils;
    @Inject Prefs prefs;

    @BindView(R.id.send_to_widget_fab) FloatingActionButton sendToWidgetFab;

    private static boolean IS_LANDSCAPE_TABLET;
    private static int PRIMARY_FRAME = R.id.primary_content_frame;
    private static int SECONDARY_FRAME = R.id.secondary_content_frame;
    private static boolean PREVIOUS_SCREEN_STATE;

    private RecipesViewModel recipesViewModel;
    private RecipeStep mCurrentRecipeStep;
    private Recipe mCurrentRecipe;

    // Holding local references to our variables so we can access them easily.
    RecipePagerFragment pagerFragment;
    StepDetailsFragment detailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);

        IS_LANDSCAPE_TABLET = getResources().getBoolean(R.bool.isLandscapeTablet);

        //Configure Dagger 2 injection
        this.configureDagger();
        this.configureViewModel();
        recipesViewModel.getSelectedRecipeStep().removeObservers(this);

        if(savedInstanceState == null) {
            // Initialise fragments
            setUpFragments();
        } else {
            //We have a saved instance state. Let's deal with this appropriately.
            // Get the previous rotation state
            PREVIOUS_SCREEN_STATE = savedInstanceState.getBoolean("mPreviousViewState");

            //Restore our view model
            mCurrentRecipeStep = savedInstanceState.getParcelable("mRecipeStep");
            if (mCurrentRecipeStep != null) {
                recipesViewModel.selectRecipeStep(mCurrentRecipeStep);
            }

            mCurrentRecipe = savedInstanceState.getParcelable("mRecipe");
            if (mCurrentRecipe != null) {
                recipesViewModel.selectRecipeItem(mCurrentRecipe);
            }
            //Resuming from saved instance / Rotation
            restoreFragments();
        }

        if (!IS_LANDSCAPE_TABLET) {
            this.configureFragmentObserver();
        }

        this.configureObservers();

        sendToWidgetFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                broadcastRecipeToWidget();
            }
        });
    }

    /**
     * Simple method for initializing fragments
     */
    private void setUpFragments() {
        if(IS_LANDSCAPE_TABLET) {
            addFragment(SECONDARY_FRAME, setUpStepsFragment(), AppConstants.FRAGTAG_STEPS);
            addFragment(PRIMARY_FRAME, setUpDetailsFragment(), AppConstants.FRAGTAG_DETAILS);
        } else {
            addFragment(PRIMARY_FRAME, setUpStepsFragment(), AppConstants.FRAGTAG_STEPS);
        }
    }

    /**
     * Method for restoring fragments from various states.
     */
    private void restoreFragments() {
        if(IS_LANDSCAPE_TABLET && PREVIOUS_SCREEN_STATE != IS_LANDSCAPE_TABLET) {
            boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate (AppConstants.FRAGTAG_STEPS, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            if(!fragmentPopped) {
                Log.v(LOG_TAG, "Main view restored successfully");
            }
            replaceFragment(SECONDARY_FRAME, setUpStepsFragment(), AppConstants.FRAGTAG_STEPS);
            Fragment detailsFragment = getSupportFragmentManager().findFragmentByTag(AppConstants.FRAGTAG_DETAILS);
            if(detailsFragment == null) {
                replaceFragment(PRIMARY_FRAME, setUpDetailsFragment(), AppConstants.FRAGTAG_DETAILS);
            }
        }
    }

    /**
     * Method used for broadcasting new data to an associated widget
     */
    private void broadcastRecipeToWidget() {
        Recipe recipe = recipesViewModel.getSelectedRecipe().getValue();
        if(recipe != null) {
            Intent widgetIntent = new Intent(getApplicationContext(), IngredientsWidgetProvider.class);
            widgetIntent.setAction(IngredientsWidgetProvider.ACTION_UPDATE_WIDGET_INGREDIENTS);
            widgetIntent.putExtra(IngredientsWidgetProvider.RECIPE_DATA, recipe);
            this.sendBroadcast(widgetIntent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("mPreviousViewState", IS_LANDSCAPE_TABLET);

        if (mCurrentRecipeStep != null){
            outState.putParcelable("mRecipeStep", mCurrentRecipeStep);

        }
        if (mCurrentRecipe != null) {
            outState.putParcelable("mRecipe", mCurrentRecipe);
        }

        recipesViewModel.fragmentRequestObserver().removeObservers(this);
        Fragment detailsFragment = getSupportFragmentManager().findFragmentByTag(AppConstants.FRAGTAG_DETAILS);

        if(detailsFragment != null) {
            getSupportFragmentManager().putFragment(outState, AppConstants.FRAGTAG_DETAILS, detailsFragment);
        }
    }

    /**
     * Method for setting up our pager fragment
     * @return returns RecipePagerFragment
     */
    private RecipePagerFragment setUpStepsFragment() {
        pagerFragment = (RecipePagerFragment) getSupportFragmentManager().findFragmentByTag(AppConstants.FRAGTAG_STEPS);
        if(pagerFragment == null) {
            pagerFragment = new RecipePagerFragment();
        } else {
            pagerFragment = (RecipePagerFragment) recreateFragment(pagerFragment);
        }
        return pagerFragment;
    }

    /**
     * Method for setting up our step details fragment
     * @return returns StepDetailsFragment
     */
    private StepDetailsFragment setUpDetailsFragment() {
        detailsFragment = (StepDetailsFragment) getSupportFragmentManager().findFragmentByTag(AppConstants.FRAGTAG_DETAILS);
        if(detailsFragment == null) {
            detailsFragment = new StepDetailsFragment();
        }
        return detailsFragment;
    }

    @Override
    public void onBackPressed() {
        Fragment detailsFragment = getSupportFragmentManager().findFragmentByTag(AppConstants.FRAGTAG_DETAILS);
        Boolean isVisible = false;

        if(detailsFragment != null) {
            isVisible = detailsFragment.isVisible();
        }
        if(!IS_LANDSCAPE_TABLET && isVisible) {
            replaceFragment(PRIMARY_FRAME, setUpStepsFragment(), AppConstants.FRAGTAG_STEPS);
        } else {
            finish();
        }
    }

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    private void configureDagger(){
        AndroidInjection.inject(this);
    }

    private void configureViewModel() {

        // Get our view model provider.
        recipesViewModel = ViewModelProviders.of(this,
                recipesViewModelFactory).get(RecipesViewModel.class);
    }

    private void configureFragmentObserver() {
        recipesViewModel.fragmentRequestObserver().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String fragmentRequestTag) {
                if(fragmentRequestTag != null && fragmentRequestTag.equals(AppConstants.FRAGTAG_DETAILS)) {
                    replaceFragment(PRIMARY_FRAME, setUpDetailsFragment(), AppConstants.FRAGTAG_DETAILS);
                }
            }
        });
    }

    private void configureObservers() {
        recipesViewModel.getSelectedRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if(recipe != null) {
                    mCurrentRecipe = recipe;
                }
            }
        });
        recipesViewModel.getSelectedRecipeStep().observe(this, new Observer<RecipeStep>() {
            @Override
            public void onChanged(@Nullable RecipeStep recipeStep) {
                if(recipeStep != null) {
                    mCurrentRecipeStep = recipeStep;
                }
            }
        });
    }

    private void addFragment(int contentFrameID, Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(contentFrameID,
                        fragment, tag)
                .commit();
    }

    /**
     * A simple method for creating a fragment transaction and committing it.
     * @param contentFrameID The desired content frame in which to place the fragment
     * @param fragment The desired fragment which we want to display.
     * @param tag The fragment tag which should match which fragment we want to display.
     */
    private void replaceFragment(int contentFrameID, Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(contentFrameID, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    // Note: There must be a better way to do this - it's really slow.
    // (Or I must optimise the rate at which the fragment can be recreated.
    private Fragment recreateFragment(Fragment f)
    {
        try {
            Fragment.SavedState savedState = getSupportFragmentManager().saveFragmentInstanceState(f);

            Fragment newInstance = f.getClass().newInstance();
            newInstance.setInitialSavedState(savedState);

            return newInstance;
        }
        catch (Exception e) // InstantiationException, IllegalAccessException
        {
            throw new RuntimeException("Cannot reinstantiate fragment " + f.getClass().getName(), e);
        }
    }
}