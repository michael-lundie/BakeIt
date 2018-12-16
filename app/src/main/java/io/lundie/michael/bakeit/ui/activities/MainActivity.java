package io.lundie.michael.bakeit.ui.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.lundie.michael.bakeit.App;
import io.lundie.michael.bakeit.R;
import io.lundie.michael.bakeit.datamodel.models.Recipe;
import io.lundie.michael.bakeit.ui.fragments.OnFragmentNavigationListener;
import io.lundie.michael.bakeit.ui.fragments.RecipesFragment;
import io.lundie.michael.bakeit.ui.fragments.StepsFragment;
import io.lundie.michael.bakeit.utilities.AppConstants;
import io.lundie.michael.bakeit.viewmodel.RecipesViewModel;

public class MainActivity extends AppCompatActivity
        implements HasSupportFragmentInjector {

    public static final String LOG_TAG = MainActivity.class.getName();

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Inject
    ViewModelProvider.Factory recipesViewModelFactory;

    @Inject AppConstants appConstants;

    RecipesViewModel recipesViewModel;

    String previousFragment;
    String currentFragment;

    RecipesFragment recipesFragment;
    StepsFragment stepsFragment;

    ArrayList<Recipe> mRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        int count = getSupportFragmentManager().getBackStackEntryCount();
        Log.i(LOG_TAG, "Fragment Backstack count is: " + count);
        if (count != 0) {
            getSupportFragmentManager().popBackStack();
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

        if (currentFragment == null) {
            if(recipesFragment == null) {
                recipesFragment = new RecipesFragment();
            }
            currentFragment = AppConstants.FRAGTAG_RECIPES;
        }

        recipesViewModel.fragmentRequestObserver().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String fragmentTag) {

                if (fragmentTag != null) {
                    fragmentDisplayManager(fragmentTag);
                } else {
                    fragmentDisplayManager(currentFragment);
                }
            }
        });
    }

    private void fragmentDisplayManager(String fragmentTag) {
        Log.i(LOG_TAG, "TEST: Fragment Tag is: " + fragmentTag);

        FragmentManager supportFragmentManager = getSupportFragmentManager();

        if(fragmentTag.equals(AppConstants.FRAGTAG_STEPS))
        {
            if(getResources().getBoolean(R.bool.isLandscapeTablet)) {

            }
            previousFragment = AppConstants.FRAGTAG_RECIPES;
            if(stepsFragment == null) {
                stepsFragment = new StepsFragment();
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.primary_content_frame,
                            stepsFragment, AppConstants.FRAGTAG_STEPS)
                    .addToBackStack(AppConstants.FRAGTAG_STEPS)
                    .commit();
        }
        else if (fragmentTag.equals(AppConstants.FRAGTAG_DETAILS))
        {

        }
    }
}