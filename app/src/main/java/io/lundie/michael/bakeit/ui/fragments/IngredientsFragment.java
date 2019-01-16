package io.lundie.michael.bakeit.ui.fragments;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.lundie.michael.bakeit.R;
import io.lundie.michael.bakeit.datamodel.models.Ingredient;
import io.lundie.michael.bakeit.datamodel.models.Recipe;
import io.lundie.michael.bakeit.ui.adapters.IngredientsViewAdapter;
import io.lundie.michael.bakeit.viewmodel.RecipesViewModel;

/**
 * A fragment representing a list of Items.
 */
public class IngredientsFragment extends Fragment {

    @Inject ViewModelProvider.Factory recipesViewModelFactory;
    @Inject IngredientsViewAdapter mAdapter;

    private RecipesViewModel recipesViewModel;

    private ArrayList<Ingredient> mIngredientsList;

    private RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public IngredientsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredient_list, container, false);

        if (mIngredientsList == null || mIngredientsList.isEmpty()){
            if (savedInstanceState != null) {
                mIngredientsList = savedInstanceState.getParcelableArrayList("mIngredientsList");
            } else {
                mIngredientsList = new ArrayList<>();
            }
        }

        // Set the adapter
        if (view instanceof RecyclerView) {
            recyclerView = (RecyclerView) view;
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.configureDagger();
        if(recipesViewModel == null) {
            this.configureViewModel();
            this.configureData();
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

    private void configureData() {
        Recipe selectedRecipe = recipesViewModel.getSelectedRecipe().getValue();
        if(selectedRecipe != null) {
            mIngredientsList = (ArrayList<Ingredient>) selectedRecipe.getIngredients();
            mAdapter.setIngredients(mIngredientsList);
        }
    }

    /**
     * A simple helper method for setting up dagger with this fragment.
     */
    private void configureDagger(){
        AndroidSupportInjection.inject(this);
    }
}
