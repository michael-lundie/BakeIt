package io.lundie.michael.bakeit.services;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import io.lundie.michael.bakeit.widget.IngredientsListProvider;

public class IngredientsWidgetService extends RemoteViewsService {

    public final static String INGREDIENTS_LIST = "ingredients_list";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        return new IngredientsListProvider(getApplicationContext(), intent);
    }
}
