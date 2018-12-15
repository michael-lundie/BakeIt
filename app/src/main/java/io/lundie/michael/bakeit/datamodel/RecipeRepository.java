package io.lundie.michael.bakeit.datamodel;

import android.arch.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.michael.bakeit.datamodel.models.Recipe;
import io.lundie.michael.bakeit.utilities.AssetProvider;

/**
 * Recipe Repository class responsible for the fetching and management of recipe data.
 */
public class RecipeRepository {

    private static final String LOG_TAG = RecipeRepository.class.getSimpleName();

    private final Gson gson;
    private final AssetProvider assetProvider;

    @Inject
    public RecipeRepository(Gson gson, AssetProvider assetProvider) {
        this.gson = gson;
        this.assetProvider = assetProvider;
    }

    private static MutableLiveData<ArrayList<Recipe>> recipes;

    public MutableLiveData<ArrayList<Recipe>> getRecipes() {

        if (recipes == null) {
            recipes = new MutableLiveData<>();
        }

        Reader reader = new InputStreamReader(assetProvider.getJsonFile());

        Type recipeListType = new TypeToken<ArrayList<Recipe>>(){}.getType();

        ArrayList<Recipe> recipeList = gson.fromJson(reader, recipeListType);

        recipes.setValue(recipeList);

        return recipes;
    }
}