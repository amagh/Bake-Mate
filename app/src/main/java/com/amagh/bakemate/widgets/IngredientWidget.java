package com.amagh.bakemate.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.amagh.bakemate.R;
import com.amagh.bakemate.utils.DatabaseUtils;

import static com.amagh.bakemate.widgets.IngredientsWidgetService.BundleKeys.RECIPE_ID;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link IngredientWidgetConfigureActivity IngredientWidgetConfigureActivity}
 */
public class IngredientWidget extends AppWidgetProvider {
    // **Constants** //
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, long recipeId) {

        String recipeName = IngredientWidgetConfigureActivity.loadTitlePref(context, appWidgetId);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ingredient);
        Intent intent = new Intent(context, IngredientsWidgetService.class);
        intent.putExtra(RECIPE_ID, recipeId);

        views.setRemoteAdapter(R.id.ingredient_widget_lv, intent);
        views.setTextViewText(R.id.ingredient_widget_recipe_tv, recipeName);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            // Get the recipeId from the recipeName associated with the appWidgetid
            long recipeId = DatabaseUtils.getRecipeId(
                    context,
                    IngredientWidgetConfigureActivity.loadTitlePref(context, appWidgetId));

            // Update the widget
            updateAppWidget(context, appWidgetManager, appWidgetId, recipeId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            IngredientWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

