package io.lundie.michael.bakeit.ui.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;
import io.lundie.michael.bakeit.R;
import io.lundie.michael.bakeit.datamodel.models.Recipe;
import io.lundie.michael.bakeit.datamodel.models.RecipeStep;
import io.lundie.michael.bakeit.ui.adapters.StepsViewAdapter;
import io.lundie.michael.bakeit.ui.views.RecyclerViewWithSetEmpty;
import io.lundie.michael.bakeit.utilities.AppConstants;
import io.lundie.michael.bakeit.viewmodel.RecipesViewModel;

public class StepDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = StepDetailsFragment.class.getName();

    // Setting up some static variables
    private static boolean IS_LANDSCAPE_TABLET;
    private static boolean IS_TABLET;


    @Inject
    ViewModelProvider.Factory recipesViewModelFactory;

    RecipesViewModel recipesViewModel;

    RecipeStep recipeStep;

    int totalSteps;

    @BindView(R.id.detail_test_tv) TextView testTextTv;
    @BindView(R.id.previous_step_btn) Button previousStepBtn;
    @BindView(R.id.next_step_btn) Button nextStepBtn;


    public StepDetailsFragment() { /* Required empty public constructor for fragment classes. */ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        //TODO: Add interaction listener, so any interaction with details set's it as default frag
        int currentStepID = recipeStep.getId();


        switch (view.getId()) {
            case R.id.previous_step_btn:
                if (currentStepID != 0) {




                    recipesViewModel.requestFragment(AppConstants.FRAGTAG_DETAILS);
                    recipesViewModel.selectRecipeStep(currentStepID -1);
                }
                break;
            case R.id.next_step_btn:
                if (currentStepID != recipesViewModel.getNumberOfSteps() -1) {
                    recipesViewModel.requestFragment(AppConstants.FRAGTAG_DETAILS);
                    recipesViewModel.selectRecipeStep(currentStepID +1);
                }
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Checking the current orientation and device
        IS_LANDSCAPE_TABLET = getResources().getBoolean(R.bool.isLandscapeTablet);
        IS_TABLET = getResources().getBoolean(R.bool.isTablet);

        // Inflate the layout for this fragment
        View listFragmentView =  inflater.inflate(R.layout.fragment_details, container, false);

        // Time to butter some toast... Bind view references with butterknife library.
        ButterKnife.bind(this, listFragmentView);

        if (savedInstanceState != null) {
            // Get recipe from saved instance if parcel exists.
            recipeStep = savedInstanceState.getParcelable("mRecipeStep");
        }

        previousStepBtn.setOnClickListener(this);
        nextStepBtn.setOnClickListener(this);

        // Return the layout for this fragment
        return listFragmentView;
    }

    @Override
    public void onPause() {
        super.onPause();
        //TODO: Resolve any onPause stuff here.
        Log.v(LOG_TAG, "TEST: ON PAUSE CALLED");
    }

    @Override
    public void onResume() {
        super.onResume();
        if(recipesViewModel == null) {
            this.configureViewModel();
            getTotalSteps();
        }
        // If we paused this activity we can be pretty sure our observer was destroyed.
        // We must restart our observer.
        this.configureObservers();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.configureDagger();

        if(recipesViewModel == null) {
            this.configureViewModel();
            this.configureObservers();
            getTotalSteps();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (recipeStep != null){
            outState.putParcelable("mRecipeStep", recipeStep);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * A simple helper method to configure our view model.
     * Let's return two observables. One, which accesses our data. The other returns network status.
     */
    private void configureViewModel() {

        // Get our view model provider.
        recipesViewModel = ViewModelProviders.of(getActivity(),
                recipesViewModelFactory).get(RecipesViewModel.class);

    }

    private void configureObservers() {
        recipesViewModel.getSelectedRecipeStep().removeObservers(this);

        recipesViewModel.getSelectedRecipeStep().observe(this, new Observer<RecipeStep>() {
            @Override
            public void onChanged(@Nullable RecipeStep selectedRecipeStep) {

                if(selectedRecipeStep != null) {
                    Log.v(LOG_TAG, "Observer Changed: " +selectedRecipeStep.getDescription());
                    recipeStep = selectedRecipeStep;
                    testTextTv.setText(selectedRecipeStep.getShortDescription());
                }
            }
        });

    }

    private void getTotalSteps() {

        Recipe currentRecipe = recipesViewModel.getSelectedRecipe().getValue();

        if(currentRecipe != null) {
            totalSteps = currentRecipe.getRecipeSteps().size();
        }

    }

    /**
     * A simple helper method for setting up dagger with this fragment.
     */
    private void configureDagger(){
        AndroidSupportInjection.inject(this);
    }

}