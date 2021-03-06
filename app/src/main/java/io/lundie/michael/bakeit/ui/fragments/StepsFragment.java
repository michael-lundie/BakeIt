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

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;
import io.lundie.michael.bakeit.R;
import io.lundie.michael.bakeit.datamodel.models.Recipe;
import io.lundie.michael.bakeit.datamodel.models.RecipeStep;
import io.lundie.michael.bakeit.ui.adapters.StepsViewAdapter;
import io.lundie.michael.bakeit.ui.fragments.utils.DataUtils;
import io.lundie.michael.bakeit.ui.views.RecyclerViewWithSetEmpty;
import io.lundie.michael.bakeit.utilities.AppConstants;
import io.lundie.michael.bakeit.viewmodel.RecipesViewModel;

public class StepsFragment extends Fragment {

    private static final String LOG_TAG = StepsFragment.class.getName();

    @Inject ViewModelProvider.Factory recipesViewModelFactory;
    @Inject DataUtils dataUtils;

    private static boolean IS_LANDSCAPE_TABLET;

    private RecipesViewModel recipesViewModel;
    private StepsViewAdapter mAdapter;
    private ArrayList<RecipeStep> mRecipeSteps;
    private Recipe mCurrentRecipe;

    private ArrayList<Boolean> setStepBackgroundBooleans;

    @BindView(R.id.steps_list_rv) RecyclerViewWithSetEmpty mRecyclerView;

    int currentStep = 0;

    public StepsFragment() { /* Required empty public constructor for fragment classes. */ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        IS_LANDSCAPE_TABLET = getResources().getBoolean(R.bool.isLandscapeTablet);

        // Inflate the layout for this fragment
        View listFragmentView =  inflater.inflate(R.layout.fragment_steps, container, false);

        // Time to butter some toast... Bind view references with butterknife library.
        ButterKnife.bind(this, listFragmentView);

            if (savedInstanceState != null) {
                mRecipeSteps = savedInstanceState.getParcelableArrayList("mRecipeSteps");
                mCurrentRecipe = savedInstanceState.getParcelable("mCurrentRecipe");
            } else {
                mRecipeSteps = new ArrayList<>();
        }

        mAdapter = new StepsViewAdapter(mRecipeSteps, setStepBackgroundBooleans,
                new StepsViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecipeStep recipeStepItem) {
                recipesViewModel.requestFragment(AppConstants.FRAGTAG_DETAILS);
                recipesViewModel.selectRecipeStep(recipeStepItem);
            }
        });

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        mRecyclerView.setAdapter(mAdapter);

        // Return the layout for this fragment
        return listFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.configureDagger();
        if(recipesViewModel == null) {
            this.configureViewModel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.configureViewModel();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecipeSteps != null){
            outState.putParcelableArrayList("mRecipeSteps", mRecipeSteps);
        }
        if(mCurrentRecipe != null){
            outState.putParcelable("mCurrentRecipe", mCurrentRecipe);
        }
    }

    /**
     * A simple helper method to configure our view model.
     * Let's return two observables. One, which accesses our data. The other returns network status.
     */
    private void configureViewModel() {

        // Get our view model provider.
        recipesViewModel = ViewModelProviders.of(getActivity(),
                recipesViewModelFactory).get(RecipesViewModel.class);

        recipesViewModel.getRecipes().removeObservers(this);

        if(mCurrentRecipe == null) {
            mCurrentRecipe = recipesViewModel.getSelectedRecipe().getValue();
        }
        if(mCurrentRecipe != null) {

            String title = getString(R.string.app_name);
            getActivity().setTitle(title  + " " + mCurrentRecipe.getName());
            ArrayList<RecipeStep> recipeSteps = (ArrayList<RecipeStep>)mCurrentRecipe.getRecipeSteps();

            Log.v(LOG_TAG, "RESTORE: " +recipeSteps);

            if(!recipeSteps.isEmpty()) {
                mRecipeSteps = recipeSteps;

                RecipeStep recipeStep = recipesViewModel.getSelectedRecipeStep().getValue();
                Log.v(LOG_TAG, "RESTORE: " +recipeStep);

                if(!IS_LANDSCAPE_TABLET) {
                    mAdapter.setStepsList(recipeSteps);
                } else {

                    currentStep = recipeStep.getStepNumber();

                    setStepBackgroundBooleans = new ArrayList<Boolean>();

                    while(setStepBackgroundBooleans.size() < recipeSteps.size())
                        setStepBackgroundBooleans.add(Boolean.FALSE);

                    setStepBackgroundBooleans.set(currentStep, Boolean.TRUE);

                    mAdapter.setStepsList(recipeSteps, setStepBackgroundBooleans);
                }
            }
        }

        if(IS_LANDSCAPE_TABLET) {
            recipesViewModel.getSelectedRecipeStep().observe(this, new Observer<RecipeStep>() {
                @Override
                public void onChanged(@Nullable RecipeStep recipeStep) {
                    if (recipeStep != null) {
                        int currentRecipeStep = recipeStep.getStepNumber();
                        for(int i = 0; i < setStepBackgroundBooleans.size(); i++) {
                            if(i == currentRecipeStep) {
                                setStepBackgroundBooleans.set(currentRecipeStep, Boolean.TRUE);
                            } else {
                                setStepBackgroundBooleans.set(i, Boolean.FALSE);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    /**
     * A simple helper method for setting up dagger with this fragment.
     */
    private void configureDagger(){
        AndroidSupportInjection.inject(this);
    }
}