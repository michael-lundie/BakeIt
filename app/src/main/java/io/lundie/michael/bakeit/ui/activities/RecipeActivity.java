package io.lundie.michael.bakeit.ui.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.LayoutAnimationController;

import java.util.ArrayList;
import java.util.Set;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.lundie.michael.bakeit.App;
import io.lundie.michael.bakeit.R;
import io.lundie.michael.bakeit.datamodel.models.Recipe;
import io.lundie.michael.bakeit.datamodel.models.RecipeStep;
import io.lundie.michael.bakeit.ui.fragments.RecipesFragment;
import io.lundie.michael.bakeit.ui.fragments.StepDetailsFragment;
import io.lundie.michael.bakeit.ui.fragments.StepsFragment;
import io.lundie.michael.bakeit.utilities.AppConstants;
import io.lundie.michael.bakeit.viewmodel.RecipesViewModel;

public class RecipeActivity extends AppCompatActivity
        implements HasSupportFragmentInjector {

    public static final String LOG_TAG = RecipeActivity.class.getName();

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Inject
    ViewModelProvider.Factory recipesViewModelFactory;

    @Inject AppConstants appConstants;

    static boolean IS_LANDSCAPE;
    static boolean IS_TABLET;
    static boolean IS_LANDSCAPE_TABLET;

    static int PRIMARY_FRAME = R.id.primary_content_frame;
    static int SECONDARY_FRAME = R.id.secondary_content_frame;

    String requestedFragment;

    RecipesViewModel recipesViewModel;

    // Holding local references to our variables so we can access them easily.
    StepsFragment stepsFragment;
    StepDetailsFragment detailsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "Vids: On CREATE called in holding ACTIVITY");
        setContentView(R.layout.activity_recipe);
        IS_LANDSCAPE = getResources().getBoolean(R.bool.isLandscape);
        IS_TABLET = getResources().getBoolean(R.bool.isTablet);
        IS_LANDSCAPE_TABLET = getResources().getBoolean(R.bool.isLandscapeTablet);

        //Configure Dagger 2 injection
        this.configureDagger();
        this.configureViewModel();

        if(savedInstanceState == null) {

            addFragment(PRIMARY_FRAME, setUpStepsFragment(null), AppConstants.FRAGTAG_STEPS);

            if (IS_LANDSCAPE_TABLET) {
                addFragment(SECONDARY_FRAME, setUpDetailsFragment(null), AppConstants.FRAGTAG_DETAILS);
            }

        } else {

            if (!IS_LANDSCAPE_TABLET) {
                if (recipesViewModel != null && recipesViewModel.getRequestedFragment()
                        .equals(AppConstants.FRAGTAG_STEPS)) {
                    Log.i(LOG_TAG, "TEST: IS PORTRAIT: last fragment = STEPS" +
                    recipesViewModel.getRequestedFragment());

                } else {
                    Log.i(LOG_TAG, "TEST: Switching to portrait, attempting to recreate" +
                            "DETAILS fragment in PRIMARY frame");
                    removeFragment(SECONDARY_FRAME,
                                    getSupportFragmentManager().findFragmentByTag(AppConstants.FRAGTAG_DETAILS));


                    detailsFragment =
                            (StepDetailsFragment) recreateFragment(setUpDetailsFragment(null));
                    replaceFragment(PRIMARY_FRAME,
                                    detailsFragment,
                                    AppConstants.FRAGTAG_DETAILS);
                }
            } else {
                if (recipesViewModel != null
                        && recipesViewModel.getRequestedFragment().equals(AppConstants.FRAGTAG_DETAILS)) {

                    removeFragment(PRIMARY_FRAME,
                            getSupportFragmentManager().findFragmentByTag(AppConstants.FRAGTAG_DETAILS));

                    addFragment(PRIMARY_FRAME, setUpStepsFragment(null), AppConstants.FRAGTAG_STEPS);

                    detailsFragment =
                            (StepDetailsFragment) recreateFragment(setUpDetailsFragment(null));

                    replaceFragment(SECONDARY_FRAME,
                            detailsFragment,
                            AppConstants.FRAGTAG_DETAILS);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        recipesViewModel.fragmentRequestObserver().removeObservers(this);
        Fragment detailsFragment = getSupportFragmentManager().findFragmentByTag(AppConstants.FRAGTAG_DETAILS);
        if(detailsFragment != null) {
            getSupportFragmentManager().putFragment(outState, AppConstants.FRAGTAG_DETAILS, detailsFragment);
        }
    }

    private StepsFragment setUpStepsFragment(@Nullable StepsFragment fragment) {
        StepsFragment stepsFragment = fragment;
        if (stepsFragment == null) {
                stepsFragment = (StepsFragment) getSupportFragmentManager().findFragmentByTag(AppConstants.FRAGTAG_STEPS);
                if(stepsFragment == null) {
                    stepsFragment = new StepsFragment();
                }
        }
        return stepsFragment;
    }

    private StepDetailsFragment setUpDetailsFragment(@Nullable StepDetailsFragment fragment) {
        StepDetailsFragment detailsFragment = fragment;
        if (detailsFragment == null) {
            detailsFragment = (StepDetailsFragment) getSupportFragmentManager().findFragmentByTag(AppConstants.FRAGTAG_DETAILS);
            if(detailsFragment == null) {
                detailsFragment = new StepDetailsFragment();
            }
        }
        return detailsFragment;
    }

    @Override
    public void onBackPressed() {
        recipesViewModel.resetRecipeSteps();

        Log.v(LOG_TAG, "TEST: ############ Backstack count: " + getSupportFragmentManager().getBackStackEntryCount());
        Fragment detailsFragment = getSupportFragmentManager().findFragmentByTag(AppConstants.FRAGTAG_DETAILS);
        if(!IS_LANDSCAPE_TABLET && detailsFragment != null && detailsFragment.isVisible()) {
            Log.v(LOG_TAG, "TEST: ######## Popping backstack immediately! ######### ");
            getSupportFragmentManager().popBackStackImmediate(
                    AppConstants.FRAGTAG_DETAILS, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            super.onBackPressed();
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

        if (!IS_LANDSCAPE_TABLET) {
            configureObservers();
        }
    }

    private void configureObservers() {
        recipesViewModel.fragmentRequestObserver().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String fragmentRequestTag) {
                requestedFragment = fragmentRequestTag;
                if(fragmentRequestTag != null && fragmentRequestTag.equals(AppConstants.FRAGTAG_DETAILS)) {
                    replaceFragment(PRIMARY_FRAME, setUpDetailsFragment(null), AppConstants.FRAGTAG_DETAILS);
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

    private void removeFragment(int contentFrameID, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(fragment)
                .commit();
    }
    // Note: There must be a better way to do this - it's really slow.
    // (Or I must optimise the rate at which the fragment can be recreated.
    private Fragment recreateFragment(Fragment fragment)
    {
        try {
            Fragment.SavedState savedState = getSupportFragmentManager().saveFragmentInstanceState(fragment);
            Fragment newInstance = fragment.getClass().newInstance();
            newInstance.setInitialSavedState(savedState);

            return newInstance;
        }
        catch (Exception e) // InstantiationException, IllegalAccessException
        {
            throw new RuntimeException("Cannot reinstantiate fragment " + fragment.getClass().getName(), e);
        }
    }
}