package io.lundie.michael.bakeit.ui.fragments.utils;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.michael.bakeit.R;
import io.lundie.michael.bakeit.datamodel.models.Ingredient;

public class DataUtils {

    Context mApplication;

    @Inject
    public DataUtils(Context application) {
        mApplication = application;
    }

    public String generateIngredientsList(ArrayList<Ingredient> ingredients) {
        StringBuilder ingredientsBuilder = new StringBuilder();

        for(int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            ingredientsBuilder
                    .append(i+1)
                    .append(". ")
                    .append(ingredient.getQuantity().toString()
                            .replaceAll("\\.?0*$", ""))
                    .append(" ")
                    .append(getMeasurement(ingredient.getMeasure()))
                    .append(" ")
                    .append(ingredients.get(i).getIngredient());
            if(i < ingredients.size()-1) {
                ingredientsBuilder.append("\n");
            }
        }
        return ingredientsBuilder.toString();
    }

    public String parseQuantity(Double quantity, String measure) {
        return quantity.toString().replaceAll("\\.?0*$", "") + " " + getMeasurement(measure);
    }

    private String getMeasurement(String acronym) {
        if(acronym.equals(mApplication.getString(R.string.acronym_grams)))
        {
            return mApplication.getString(R.string.text_grams);
        }
        else if(acronym.equals(mApplication.getString(R.string.acronym_unit)))
        {
            return "";
        }
        else if(acronym.equals(mApplication.getString(R.string.acronym_tea_spoon)))
        {
            return mApplication.getString(R.string.text_tea_spoon);
        }
        else if(acronym.equals(mApplication.getString(R.string.acronym_table_spoon)))
        {
            return mApplication.getString(R.string.text_table_spoon);
        }
        else if(acronym.equals(mApplication.getString(R.string.acronym_kilo)))
        {
            return mApplication.getString(R.string.text_kilo);
        }
        else if(acronym.equals(mApplication.getString(R.string.acronym_cup)))
        {
            return mApplication.getString(R.string.text_cup);
        }
        else if(acronym.equals(mApplication.getString(R.string.acronym_ounce)))
        {
            return mApplication.getString(R.string.text_ounce);
        }
        return acronym;
    }
}
