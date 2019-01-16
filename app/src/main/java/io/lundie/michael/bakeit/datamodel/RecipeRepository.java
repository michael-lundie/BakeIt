package io.lundie.michael.bakeit.datamodel;

import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;

import io.lundie.michael.bakeit.datamodel.models.Recipe;

/**
 * Interface class giving access ro the recipe repository. This allows for alternate repository
 * versions (static data) to be provided in our alternative test build flavor.
 */
public interface RecipeRepository {
    MutableLiveData<ArrayList<Recipe>> getRecipes();
}
