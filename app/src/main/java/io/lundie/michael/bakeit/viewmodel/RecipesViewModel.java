package io.lundie.michael.bakeit.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.michael.bakeit.datamodel.RecipeRepository;
import io.lundie.michael.bakeit.datamodel.models.Recipe;

public class RecipesViewModel extends ViewModel {

    private static final String LOG_TAG = RecipesViewModel.class.getName();

    // Repo to be injected with dagger via class constructor
    private RecipeRepository recipeRepository;

    // MutableLiveData variables for handling data fetched via repo
    private static MutableLiveData<ArrayList<Recipe>> recipeMutableLiveData;

    public RecipesViewModel() { /* Required empty constructor. */ }

    @Inject
    public RecipesViewModel(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public LiveData<ArrayList<Recipe>> getRecipes() {

        if(recipeMutableLiveData == null) {
            // Instantiate MutableLiveData object
            recipeMutableLiveData = new MutableLiveData<>();

        }

        // We would handle any potential refreshing withing the repository class.
        recipeMutableLiveData = recipeRepository.getRecipes();

        return recipeMutableLiveData;
    }

}
