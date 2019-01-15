package io.lundie.michael.bakeit.utilities;

import android.app.Application;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import io.lundie.michael.bakeit.R;
import io.lundie.michael.bakeit.datamodel.models.Ingredient;
import io.lundie.michael.bakeit.datamodel.models.Recipe;

public class Prefs {
    private final static String INGREDIENTS_DATA = "widget_ingredients_data";
    private final static String RECIPE_NAME = "widget_recipe_name";

    private SharedPreferences mSharedPrefs;

    @Inject
    public Prefs(SharedPreferences sharedPrefs) {
        mSharedPrefs = sharedPrefs;
    }

    public void setRecipeNameForWidget(String recipeName) {
        mSharedPrefs.edit().putString(RECIPE_NAME, recipeName).apply();
    }

    public String getRecipeNameForWidget() {
        return mSharedPrefs.getString(RECIPE_NAME, null);
    }

    public void setIngredientsForWidget(Set<String> ingredientsSet) {
        mSharedPrefs.edit().putStringSet(INGREDIENTS_DATA, ingredientsSet).apply();
    }

    public Set<String> getIngredientsForWidget() {
        return mSharedPrefs.getStringSet(INGREDIENTS_DATA, null);
    }
}
