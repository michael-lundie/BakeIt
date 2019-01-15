package io.lundie.michael.bakeit.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import io.lundie.michael.bakeit.R;
import io.lundie.michael.bakeit.datamodel.models.Ingredient;
import io.lundie.michael.bakeit.datamodel.models.Recipe;
import io.lundie.michael.bakeit.services.IngredientsWidgetService;
import io.lundie.michael.bakeit.ui.fragments.RecipesFragment;
import io.lundie.michael.bakeit.ui.fragments.utils.DataUtils;
import io.lundie.michael.bakeit.utilities.Prefs;
import io.lundie.michael.bakeit.widget.utils.IngredientsWidgetUtility;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsWidgetProvider extends AppWidgetProvider {

    private static final String LOG_TAG = IngredientsWidgetProvider.class.getName();

    public final static String ACTION_UPDATE_WIDGET_INGREDIENTS = "update_widget_ingredients";
    public final static String RECIPE_DATA = "recipe_data";

    DataUtils dataUtils;
    Prefs prefs;
    Set<String> ingredientsSet;
    String recipeTitle;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String recipeName, ArrayList<String> ingredients) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget_provider);

        Intent intent = new Intent(context, IngredientsWidgetService.class);
        intent.putExtra(IngredientsWidgetService.INGREDIENTS_LIST, ingredients);

        views.setTextViewText(R.id.widget_title, recipeName);

        views.setRemoteAdapter(R.id.widget_list_view, intent);
        views.setEmptyView(R.id.widget_list_view, R.id.widget_empty_view);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list_view);

        // Update Widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(intent.getAction() != null) {
            switch(intent.getAction()) {
                case ACTION_UPDATE_WIDGET_INGREDIENTS:
                    Recipe recipe = intent.getParcelableExtra(RECIPE_DATA);
                    recipeTitle = recipe.getName();
                    ingredientsSet = recipeIngredientsToSet(context, recipe);
                    getPrefs(context).setIngredientsForWidget(ingredientsSet);
                    getPrefs(context).setRecipeNameForWidget(recipeTitle);
                    updateWidget(context);
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        ArrayList<String> ingredients = recipeIngredientsToList(getPrefs(context).getIngredientsForWidget());
        String recipeTitle = getPrefs(context).getRecipeNameForWidget();
        Log.v(LOG_TAG, "TEST: WIDGET ON UPDATE " );
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipeTitle, ingredients);
        }
    }

    private void updateWidget(Context context) {
        ComponentName appWidget = new ComponentName(context, IngredientsWidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(appWidget);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private ArrayList<String> recipeIngredientsToList(Set<String> ingredientsSet) {
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

    private Set<String> recipeIngredientsToSet(Context context, Recipe recipe) {
        List<Ingredient> ingredients = recipe.getIngredients();
        Set<String> ingredientsSet = new HashSet<>();
        int i = 0;
        for(Ingredient ingredient: ingredients) {
            String ingredientsString = getDataUtils(context).parseQuantity(
                    ingredient.getQuantity(), ingredient.getMeasure())
                    + " " + ingredient.getIngredient();
            ingredientsSet.add(ingredientsString);
        } return ingredientsSet;
    }

    private Prefs getPrefs(Context context) {
        if(prefs == null) {
            prefs = new Prefs(PreferenceManager.getDefaultSharedPreferences(context));
        }
        return prefs;
    }

    private DataUtils getDataUtils(Context context) {
        if(dataUtils == null) {
            dataUtils = new DataUtils(context);
        } return dataUtils;
    }
}