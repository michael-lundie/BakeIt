package io.lundie.michael.bakeit.ui.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import io.lundie.michael.bakeit.ui.adapters.RecipesViewAdapter;
import io.lundie.michael.bakeit.ui.views.RecyclerViewWithSetEmpty;
import io.lundie.michael.bakeit.utilities.AppConstants;
import io.lundie.michael.bakeit.viewmodel.RecipesViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipesFragment extends Fragment {

    private static final String LOG_TAG = RecipesFragment.class.getName();

    // Setting up some static variables
    private static boolean IS_LANDSCAPE_TABLET;
    private static boolean IS_TABLET;

    private static RecipesFragment recipesFragment;
    private OnFragmentInteractionListener mListener;

    @Inject
    ViewModelProvider.Factory recipesViewModelFactory;

    RecipesViewModel recipesViewModel;

    RecipesViewAdapter mAdapter;

    ArrayList<Recipe> mRecipeList;

    @BindView(R.id.recipes_list_rv) RecyclerViewWithSetEmpty mRecyclerView;

    public RecipesFragment() { /* Required empty public constructor for fragment classes. */ }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Check what kind of device we are viewing on
        IS_LANDSCAPE_TABLET = getResources().getBoolean(R.bool.isLandscapeTablet);
        IS_TABLET = getResources().getBoolean(R.bool.isTablet);

        // Inflate the layout for this fragment
        View listFragmentView =  inflater.inflate(R.layout.fragment_recipes, container, false);

        // Time to butter some toast... Bind view references with butterknife library.
        ButterKnife.bind(this, listFragmentView);

        if (mRecipeList == null || mRecipeList.isEmpty()){
            if (savedInstanceState != null) {
                mRecipeList = savedInstanceState.getParcelableArrayList("mRecipeList");
                Log.i(LOG_TAG, "TEST: Retrieving parcelable data for recipe steps: " + mRecipeList);
            } else {
                mRecipeList = new ArrayList<Recipe>();
            }
        }

        mAdapter = new RecipesViewAdapter(mRecipeList, new RecipesViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Recipe recipe) {
                //TODO: Set up Recipe details fragment

                recipesViewModel.selectRecipeItem(recipe);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.primary_content_frame,
                                new StepsFragment(), AppConstants.FRAGTAG_STEPS)
                        .addToBackStack(null)
                        .commit();
            }
        });

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getListSpanCount()));
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mRecipeList != null){
            outState.putParcelableArrayList("mRecipeList", mRecipeList);
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

        recipesViewModel.getRecipes().observe(this, new Observer<ArrayList<Recipe>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Recipe> recipes) {


                if((recipes != null) && (!recipes.isEmpty())) {

                    for (int i=0; i < recipes.size(); i++) {
                        Log.i(LOG_TAG, "Recipe ID: " + recipes.get(i).getId());

                        Log.v(LOG_TAG, "Ingredients: " + recipes.get(i).getRecipeSteps().get(i).getDescription());
                    }

                    mAdapter.setRecipes(recipes);

                }
                Log.v(LOG_TAG, "on changed called" + mRecipeList);
            }
        });
    }

    private int getListSpanCount() {
        if (getResources().getConfiguration().orientation == 2 || IS_TABLET) {
            return 4;
        } else {
            return 3;
        }
    }


    /**
     * A simple helper method for setting up dagger with this fragment.
     */
    private void configureDagger(){
        AndroidSupportInjection.inject(this);
    }

}
