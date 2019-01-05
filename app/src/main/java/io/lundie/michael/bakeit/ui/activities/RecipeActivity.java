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

import java.util.ArrayList;
import java.util.Set;

import javax.inject.Inject;

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

    public static final String LOG_TAG = LauncherActivity.class.getName();

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Inject
    ViewModelProvider.Factory recipesViewModelFactory;

    @Inject AppConstants appConstants;

    static boolean IS_LANDSCAPE;
    static boolean IS_TABLET;

    static int PRIMARY_FRAME = R.id.primary_content_frame;
    static int SECONDARY_FRAME = R.id.secondary_content_frame;

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
        //Configure Dagger 2 injection

        this.configureDagger();

        if (!IS_LANDSCAPE && !IS_TABLET) {
            // We don't need any view model observers if we are in tablet landscape mode, since the
            // recipe details fragment can update itself.
            this.configureViewModel();
        }


        if(savedInstanceState == null) {

            stepsFragment = new StepsFragment();

            Log.v(LOG_TAG, "Vids: Instance state is null");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.primary_content_frame,
                            stepsFragment, AppConstants.FRAGTAG_STEPS)
                    .commit();

            if(IS_TABLET && IS_LANDSCAPE) {
                detailsFragment = new StepDetailsFragment();
                replaceFragment(R.id.secondary_content_frame,
                        detailsFragment,
                        AppConstants.FRAGTAG_DETAILS);
            }
        } else {
            Log.v(LOG_TAG, "Vids: Will Resume from saved instance state.");
            detailsFragment = (StepDetailsFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, AppConstants.FRAGTAG_DETAILS);

            stepsFragment = (StepsFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, AppConstants.FRAGTAG_STEPS);

            if (IS_TABLET && IS_LANDSCAPE) {

                replaceFragment(R.id.primary_content_frame,
                        recreateFragment(stepsFragment),
                        AppConstants.FRAGTAG_STEPS);

                replaceFragment(R.id.secondary_content_frame,
                        recreateFragment(detailsFragment),
                        AppConstants.FRAGTAG_DETAILS);
            } else {

                Log.i(LOG_TAG, "Vids: Replace primary content frame");
                replaceFragment(R.id.primary_content_frame,
                        recreateFragment(detailsFragment),
                        AppConstants.FRAGTAG_DETAILS);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, AppConstants.FRAGTAG_DETAILS, detailsFragment);
        getSupportFragmentManager().putFragment(outState,  AppConstants.FRAGTAG_STEPS, stepsFragment);
    }

    @Override
    public void onBackPressed() {
        if ((IS_LANDSCAPE && IS_TABLET) ||
                stepsFragment != null && stepsFragment.isVisible()) {

            recipesViewModel.requestFragment(AppConstants.FRAGTAG_RECIPES);
            Log.v(LOG_TAG, "TEST: Clearing Activity TOP");
            Intent launcherIntent = new Intent(this, LauncherActivity.class);
            launcherIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(launcherIntent);
        } else {
            recipesViewModel.requestFragment(AppConstants.FRAGTAG_STEPS);
            updateFragmentDisplay(AppConstants.FRAGTAG_STEPS);
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

        recipesViewModel.getSelectedRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if (detailsFragment == null) {
                    detailsFragment = new StepDetailsFragment();
                }
                replaceFragment(PRIMARY_FRAME, detailsFragment, AppConstants.FRAGTAG_DETAILS);
            }
        });
    }

    private void updateFragmentDisplay(String fragmentTag) {

        if(fragmentTag.equals(AppConstants.FRAGTAG_STEPS)) {

            if (stepsFragment == null) {
                stepsFragment = new StepsFragment();
            }

            replaceFragment(R.id.primary_content_frame, stepsFragment, AppConstants.FRAGTAG_STEPS);


        } else
        if (fragmentTag.equals(AppConstants.FRAGTAG_DETAILS)) {
            if(detailsFragment == null) {
                detailsFragment = new StepDetailsFragment();
            }

            int contentFrame;

            if(IS_LANDSCAPE && IS_TABLET) {
                contentFrame = R.id.secondary_content_frame;
            } else {
                contentFrame = R.id.primary_content_frame;
            }
            replaceFragment(contentFrame, detailsFragment, AppConstants.FRAGTAG_DETAILS);
        }
    }

    /**
     * A simple method for creating a fragment transaction and committing it.
     * @param contentFrameID The desired content frame in which to place the fragment
     * @param fragment The desired fragment which we want to display.
     * @param tag The fragment tag which should match which fragment we want to display.
     */
    private void replaceFragment(int contentFrameID, Fragment fragment, String tag) {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(contentFrameID,
                        fragment, tag)
                //.addToBackStack(tag)
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