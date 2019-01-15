package io.lundie.michael.bakeit.datamodel;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.michael.bakeit.datamodel.models.Recipe;
import io.lundie.michael.bakeit.utilities.AssetProvider;
import io.lundie.michael.bakeit.utilities.SimpleLruCache;

/**
 * Recipe Repository class responsible for the fetching and management of recipe data.
 */
public class RecipeRepositoryMain implements RecipeRepository {

    private static final String LOG_TAG = RecipeRepositoryMain.class.getSimpleName();

    private final Gson gson;
    private final AssetProvider assetProvider;
    private SimpleLruCache lruCache;

    @Inject
    public RecipeRepositoryMain(Gson gson, AssetProvider assetProvider, SimpleLruCache lruCache) {
        this.gson = gson;
        this.assetProvider = assetProvider;
        this.lruCache = lruCache;
    }

    private static MutableLiveData<ArrayList<Recipe>> recipes;

    @Override
    public MutableLiveData<ArrayList<Recipe>> getRecipes() {

        if (recipes == null) {
            recipes = new MutableLiveData<>();
        }

        if(recipes.getValue() == null || recipes.getValue().isEmpty()) {
            if(!attemptCacheRetrieval()) {
                retrieveFromJSON();
            }
        }

        return recipes;
    }

    private void retrieveFromJSON() {
        //TODO: Put this into a seperate thread
        Reader reader = new InputStreamReader(assetProvider.getJsonFile());

        Type recipeListType = new TypeToken<ArrayList<Recipe>>(){}.getType();

        ArrayList<Recipe> recipeList = gson.fromJson(reader, recipeListType);

        sendToCache(recipeList);

        recipes.setValue(recipeList);
    }

    private void sendToCache(ArrayList<Recipe> recipes) {
        lruCache.getCacheData().put("recipe", recipes);
    }

    private boolean attemptCacheRetrieval() {
        Log.i(LOG_TAG, "TEST: Retrieving from cache");
        ArrayList<Recipe> recipeList = (ArrayList<Recipe>) lruCache.getCacheData().get("recipe");
        if(recipeList != null && !recipeList.isEmpty()) {
            recipes.setValue(recipeList);
            return true;
        } return false;
    }
}