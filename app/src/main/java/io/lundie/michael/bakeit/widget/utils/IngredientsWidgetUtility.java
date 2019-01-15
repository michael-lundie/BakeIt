package io.lundie.michael.bakeit.widget.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


import io.lundie.michael.bakeit.utilities.Prefs;

public class IngredientsWidgetUtility {

    Context mAppContext;
    Prefs prefs;

    public IngredientsWidgetUtility(Context appContext) {
        this.mAppContext = appContext;
    }

    public void setRecipeIngredients(ArrayList<String> ingredients) {
        getPrefs().setIngredientsForWidget(new HashSet<>(ingredients));
        updateWidget();
    }

    public void updateWidget() {

    }

    public ArrayList<String> getIngredients() {

        Set<String> ingredientsSet = getPrefs().getIngredientsForWidget();

        if(ingredientsSet != null) {
            ArrayList<String> ingredientsList = new ArrayList<>();
            int i = 0;
            for(String string : ingredientsSet) {
                ingredientsList.add(i++, string);
            }
            return  ingredientsList;
        }
        return null;
    }

    private Prefs getPrefs() {
        if(prefs == null) {
            prefs = new Prefs(PreferenceManager.getDefaultSharedPreferences(mAppContext));
        } return prefs;
    }
}
