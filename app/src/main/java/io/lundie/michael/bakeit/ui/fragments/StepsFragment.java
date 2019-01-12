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

    RecipesViewModel recipesViewModel;

    StepsViewAdapter mAdapter;

    ArrayList<RecipeStep> mRecipeSteps;

    @BindView(R.id.steps_list_rv)
    RecyclerViewWithSetEmpty mRecyclerView;

    public StepsFragment() { /* Required empty public constructor for fragment classes. */ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View listFragmentView =  inflater.inflate(R.layout.fragment_steps, container, false);

        // Time to butter some toast... Bind view references with butterknife library.
        ButterKnife.bind(this, listFragmentView);

        if (mRecipeSteps == null || mRecipeSteps.isEmpty()){
            if (savedInstanceState != null) {
                mRecipeSteps = savedInstanceState.getParcelableArrayList("mRecipeSteps");
            } else {
                mRecipeSteps = new ArrayList<>();
            }
        }


        mAdapter = new StepsViewAdapter(mRecipeSteps, new StepsViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecipeStep recipeStepItem) {
                Log.i(LOG_TAG, "TEST: View Adapter click received.");
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
        Log.v(LOG_TAG, "TEST: ON RESUME called");
        super.onResume();
        this.configureViewModel();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mRecipeSteps != null){
            Log.i(LOG_TAG, "TEST: Saving parcelable recipe steps.");
            outState.putParcelableArrayList("mRecipeSteps", mRecipeSteps);
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

        recipesViewModel.getRecipes().removeObservers(this);

        recipesViewModel.getSelectedRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe selectedRecipe) {

                if(selectedRecipe != null) {

                    if(!selectedRecipe.getRecipeSteps().isEmpty()) {
//                        for (int i=0; i < selectedRecipe.getRecipeSteps().size(); i++) {
//                            Log.i(LOG_TAG, "Recipe Step No: " + selectedRecipe.getRecipeSteps().get(i).getStepNumber());
//                        }
                        // Holding a local reference to recipe steps so that Android can handle
                        // our fragment state without having to re-configure our viewmodel on
                        // resume. (See further notes in RecipesFragment.java)
                        //mRecipeSteps = (ArrayList<RecipeStep>) selectedRecipe.getRecipeSteps();
                        mAdapter.setStepsList((ArrayList<RecipeStep>)selectedRecipe.getRecipeSteps());
                        //TODO: Which is better? Using notify data changed on local variable or just
                        // reconfiguring the viewmodel every time?
                    }
                }
            }
        });
    }

    /**
     * A simple helper method for setting up dagger with this fragment.
     */
    private void configureDagger(){
        AndroidSupportInjection.inject(this);
    }
}