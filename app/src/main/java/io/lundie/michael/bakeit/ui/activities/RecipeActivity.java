package io.lundie.michael.bakeit.ui.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
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

    RecipesViewModel recipesViewModel;

    String previousFragment;
    String currentFragment;

    StepsFragment stepsFragment;
    StepDetailsFragment detailsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        //Configure Dagger 2 injection
        this.configureDagger();

        this.configureViewModel();

        if(savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.primary_content_frame,
                            new RecipesFragment(), AppConstants.FRAGTAG_RECIPES)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getResources().getBoolean(R.bool.isLandscapeTablet) ||
                stepsFragment != null && stepsFragment.isVisible()) {

            recipesViewModel.requestFragment(AppConstants.FRAGTAG_RECIPES);
            Log.v(LOG_TAG, "TEST: Clearing Activity TOP");
            Intent launcherIntent = new Intent(this, LauncherActivity.class);
            launcherIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(launcherIntent);
        } else {
            recipesViewModel.requestFragment(AppConstants.FRAGTAG_STEPS);
            setupFragmentDisplay(AppConstants.FRAGTAG_STEPS);
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

        setupFragmentDisplay(AppConstants.FRAGTAG_STEPS);

        if(getResources().getBoolean(R.bool.isLandscapeTablet)) {
            /*TODO; Set up details to automatically load the first step on initialisation unless
             otherwise requested by user. */
            setupFragmentDisplay(AppConstants.FRAGTAG_DETAILS);
        }

        recipesViewModel.fragmentRequestObserver().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String fragmentTag) {

                if (fragmentTag != null) {
                    setupFragmentDisplay(fragmentTag);
                } else {
                    setupFragmentDisplay(currentFragment);
                }
            }
        });

    }

    private void setupFragmentDisplay(String fragmentTag) {
        Log.i(LOG_TAG, "TEST: Fragment Tag is: " + fragmentTag);

        if(fragmentTag.equals(AppConstants.FRAGTAG_STEPS))
        {
            previousFragment = AppConstants.FRAGTAG_RECIPES;

            if(stepsFragment == null) {
                stepsFragment = new StepsFragment();
            }

            replaceFragment(R.id.primary_content_frame, stepsFragment,
                    AppConstants.FRAGTAG_STEPS);
        }
        else if (fragmentTag.equals(AppConstants.FRAGTAG_DETAILS))
        {

            if(detailsFragment == null) {
                detailsFragment = new StepDetailsFragment();
            }

            if(!getResources().getBoolean(R.bool.isLandscapeTablet)) {

                replaceFragment(R.id.primary_content_frame, detailsFragment,
                        AppConstants.FRAGTAG_DETAILS);

            } else {

                previousFragment = AppConstants.FRAGTAG_STEPS;

                replaceFragment(R.id.secondary_content_frame, detailsFragment,
                        AppConstants.FRAGTAG_DETAILS);
            }
        }
    }

    /**
     * A simple method for creating a fragment transaction and committing it.
     * @param contentFrame The desired content frame in which to place the fragment
     * @param fragment The desired fragment which we want to display.
     * @param tag The fragment tag which should match which fragment we want to display.
     */
    private void replaceFragment(int contentFrame, Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(contentFrame,
                        fragment, tag)
                .addToBackStack(tag)
                .commit();
    }
}