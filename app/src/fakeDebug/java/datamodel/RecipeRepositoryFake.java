package datamodel;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.michael.bakeit.datamodel.RecipeRepository;
import io.lundie.michael.bakeit.datamodel.models.Recipe;
import io.lundie.michael.bakeit.utilities.AssetProvider;
import io.lundie.michael.bakeit.utilities.SimpleLruCache;

/**
 * Recipe Repository TEST Fake class responsible for the fetching and management of recipe data.
 * This repository fake does not implement the LRU cache
 */
public class RecipeRepositoryFake implements RecipeRepository {

    private static final String LOG_TAG = RecipeRepositoryFake.class.getSimpleName();
    private final Gson gson;
    private final AssetProvider assetProvider;

    @Inject
    public RecipeRepositoryFake(Gson gson, AssetProvider assetProvider) {
        this.gson = gson;
        this.assetProvider = assetProvider;
    }

    private static MutableLiveData<ArrayList<Recipe>> recipes;

    @Override
    public MutableLiveData<ArrayList<Recipe>> getRecipes() {

        if (recipes == null) {
            recipes = new MutableLiveData<>();
        }

        if(recipes.getValue() == null || recipes.getValue().isEmpty()) {
                retrieveFromJSON();
        }

        return recipes;
    }

    private void retrieveFromJSON() {
        Reader reader = new InputStreamReader(assetProvider.getJsonFile());

        Type recipeListType = new TypeToken<ArrayList<Recipe>>(){}.getType();

        ArrayList<Recipe> recipeList = gson.fromJson(reader, recipeListType);

        recipes.setValue(recipeList);
    }

    private void getHardCodedRecipes() {
        //TODO; If time - code some hard coded recipes.
    }
}