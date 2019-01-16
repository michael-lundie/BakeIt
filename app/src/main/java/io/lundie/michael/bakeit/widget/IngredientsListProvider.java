package io.lundie.michael.bakeit.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.util.ArrayList;

import io.lundie.michael.bakeit.R;
import io.lundie.michael.bakeit.services.IngredientsWidgetService;

public class IngredientsListProvider implements RemoteViewsFactory {

    private static final String LOG_TAG = IngredientsListProvider.class.getName();

    private ArrayList<String> mDataList = null;
    private Context context = null;
    private int appWidgetId;

    public IngredientsListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        mDataList = intent.getStringArrayListExtra(IngredientsWidgetService.INGREDIENTS_LIST);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getCount() {
        if(mDataList != null) {
            return mDataList.size();
        } return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     *Similar to getView of Adapter where instead of View
     *we return RemoteViews
     *
     */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.ingredients_widget_list_item);

        String ingredient = mDataList.get(position);

        remoteView.setTextViewText(R.id.widget_row, ingredient);

        return remoteView;
    }
}
