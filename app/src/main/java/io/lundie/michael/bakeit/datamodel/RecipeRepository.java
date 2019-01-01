package io.lundie.michael.bakeit.datamodel;

import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;

import io.lundie.michael.bakeit.datamodel.models.Recipe;

public interface RecipeRepository {
    MutableLiveData<ArrayList<Recipe>> getRecipes();
}
