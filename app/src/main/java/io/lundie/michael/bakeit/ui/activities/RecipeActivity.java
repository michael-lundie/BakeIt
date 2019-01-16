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

public class RecipeActivity extends AppCompatActivity
        implements HasSupportFragmentInjector {

    public static final String LOG_TAG = RecipeActivity.class.getName();

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Inject
    ViewModelProvider.Factory recipesViewModelFactory;

    @Inject AppConstants appConstants;

    @Inject DataUtils dataUtils;

    @Inject Prefs prefs;

    @BindView(R.id.send_to_widget_fab) FloatingActionButton sendToWidgetFab;

    static boolean IS_LANDSCAPE_TABLET;

    static int PRIMARY_FRAME = R.id.primary_content_frame;
    static int SECONDARY_FRAME = R.id.secondary_content_frame;

    String requestedFragment;

    RecipesViewModel recipesViewModel;

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


        if(savedInstanceState == null) {
            if(IS_LANDSCAPE_TABLET) {
                addFragment(SECONDARY_FRAME, setUpStepsFragment(), AppConstants.FRAGTAG_STEPS);
                addFragment(PRIMARY_FRAME, setUpDetailsFragment(), AppConstants.FRAGTAG_DETAILS);
            } else {
                addFragment(PRIMARY_FRAME, setUpStepsFragment(), AppConstants.FRAGTAG_STEPS);
            }
        } else {
            if(IS_LANDSCAPE_TABLET) {
                replaceFragment(SECONDARY_FRAME, setUpStepsFragment(), AppConstants.FRAGTAG_STEPS);
                Fragment detailsFragment = getSupportFragmentManager().findFragmentByTag(AppConstants.FRAGTAG_DETAILS);
                if(detailsFragment == null) {
                    replaceFragment(PRIMARY_FRAME, setUpDetailsFragment(), AppConstants.FRAGTAG_DETAILS);
                }
            }
        }

        sendToWidgetFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                broadcastRecipeToWidget();
            }
        });
    }

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

        recipesViewModel.fragmentRequestObserver().removeObservers(this);
        Fragment detailsFragment = getSupportFragmentManager().findFragmentByTag(AppConstants.FRAGTAG_DETAILS);
        if(detailsFragment != null) {
            getSupportFragmentManager().putFragment(outState, AppConstants.FRAGTAG_DETAILS, detailsFragment);
        }
    }

    private RecipePagerFragment setUpStepsFragment() {

        pagerFragment = (RecipePagerFragment) getSupportFragmentManager().findFragmentByTag(AppConstants.FRAGTAG_STEPS);
        if(pagerFragment == null) {
            pagerFragment = new RecipePagerFragment();
        } else {
            pagerFragment = (RecipePagerFragment) recreateFragment(pagerFragment);
        }
        return pagerFragment;
    }

    private StepDetailsFragment setUpDetailsFragment() {
        detailsFragment = (StepDetailsFragment) getSupportFragmentManager().findFragmentByTag(AppConstants.FRAGTAG_DETAILS);
        if(detailsFragment == null) {
            detailsFragment = new StepDetailsFragment();
        }
        return detailsFragment;
    }

    @Override
    public void onBackPressed() {

        int stackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        Fragment detailsFragment = getSupportFragmentManager().findFragmentByTag(AppConstants.FRAGTAG_DETAILS);
        Boolean isVisible = false;

        if(detailsFragment != null) {
            isVisible = detailsFragment.isVisible();
        }


        if(!IS_LANDSCAPE_TABLET && isVisible && stackEntryCount > 0) {
            getSupportFragmentManager().popBackStackImmediate(
                AppConstants.FRAGTAG_DETAILS, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
                    replaceFragment(PRIMARY_FRAME, setUpDetailsFragment(), AppConstants.FRAGTAG_DETAILS);
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