package io.lundie.michael.bakeit.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import java.util.ArrayList;
import java.util.Optional;

import javax.inject.Inject;

import io.lundie.michael.bakeit.datamodel.RecipeRepository;
import io.lundie.michael.bakeit.datamodel.models.Recipe;
import io.lundie.michael.bakeit.datamodel.models.RecipeStep;

public class RecipesViewModel extends ViewModel {

    private static final String LOG_TAG = RecipesViewModel.class.getName();

    // Repo to be injected with dagger via class constructor
    private RecipeRepository recipeRepository;

    // MutableLiveData variables for handling data fetched via repo
    private static MutableLiveData<ArrayList<Recipe>> recipeListMutableLiveData;
    private static MutableLiveData<Recipe> selectedRecipeMutableLiveData;
    private static MutableLiveData<RecipeStep> selectedRecipeStepLiveData = new MutableLiveData<>();
    private static MutableLiveData<String> fragmentRequestedTag = new MutableLiveData<>();

    public RecipesViewModel() { /* Required empty constructor. */ }

    /**
     * ViewModel constructor, which utilizes Dagger 2 constructor injection.
     * @param recipeRepository
     */
    @Inject
    public RecipesViewModel(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    /**
     * Returns a LiveData object containing an array list of recipe objects, retrieved via
     * our recipe repository.
     * @return LiveData object containing an array list of recipe objects
     */
    public LiveData<ArrayList<Recipe>> getRecipes() {

        if(recipeListMutableLiveData == null) {
            // Instantiate MutableLiveData object
            recipeListMutableLiveData = new MutableLiveData<>();
        }

        // We would handle any potential refreshing withing the repository class.
        recipeListMutableLiveData = recipeRepository.getRecipes();

        return recipeListMutableLiveData;
    }

    /**
     * Allows a specific recipe ITEM to be instantiated in a separate LiveData object so it can be
     * retrieved easily by a fragment object.
     * @param recipe the specific recipe ITEM object to be loaded into a LiveData object
     */
    public void selectRecipeItem(Recipe recipe) {

        if(selectedRecipeMutableLiveData == null) {
            selectedRecipeMutableLiveData = new MutableLiveData<>();
        }

        selectedRecipeMutableLiveData.setValue(recipe);
    }

    /**
     * @return a reference to the previously selected recipe item object if it exists.
     */
    public LiveData<Recipe> getSelectedRecipe() { return selectedRecipeMutableLiveData; }

    /**
     * Allows a specific recipe STEP to be instantiated in a separate LiveData object so it can be
     * retrieved easily by a fragment object.
     * @param recipeStep the specific recipe STEP object to be loaded into a LiveData object
     */
    public void selectRecipeStep(RecipeStep recipeStep) {
        selectedRecipeStepLiveData.setValue(recipeStep);
    }

    public void selectRecipeStep(int recipeStepID) {
       RecipeStep step = selectedRecipeMutableLiveData.getValue().getRecipeSteps().get(recipeStepID);
        selectedRecipeStepLiveData.setValue(step);
    }

    public int getNumberOfSteps() {
        return selectedRecipeMutableLiveData.getValue().getRecipeSteps().size();
    }

    /**
     * @return a reference to the previously selected recipe item object if it exists.
     */
    public LiveData<RecipeStep> getSelectedRecipeStep() { return selectedRecipeStepLiveData; }

    public void requestFragment(String fragmentTag) {
        fragmentRequestedTag.setValue(fragmentTag);
    }


    public LiveData<String> fragmentRequestObserver() {
        return fragmentRequestedTag;
    }

}