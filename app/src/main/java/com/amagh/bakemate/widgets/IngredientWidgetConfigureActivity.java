package com.amagh.bakemate.widgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.amagh.bakemate.R;
import com.amagh.bakemate.adapters.RecipeAdapter;
import com.amagh.bakemate.data.RecipeProvider;
import com.amagh.bakemate.databinding.WidgetIngredientConfigureBinding;
import com.amagh.bakemate.utils.DatabaseUtils;

import static com.amagh.bakemate.adapters.RecipeAdapter.RecipeLayouts.*;

/**
 * The configuration screen for the {@link IngredientWidget IngredientWidget} AppWidget.
 */
@SuppressWarnings("WeakerAccess")
public class IngredientWidgetConfigureActivity extends Activity
        implements android.app.LoaderManager.LoaderCallbacks<Cursor>{
    // **Constants** //
    private static final String PREFS_NAME = "com.amagh.bakemate.widgets.IngredientWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private static final int RECIPE_CURSOR_LOADER = 3465;

    // **Member Variables** //
    private WidgetIngredientConfigureBinding mBinding;
    private Cursor mCursor;
    private RecipeAdapter mAdapter;

    int mAppWidgetId;

    public IngredientWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return null;
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        mBinding = DataBindingUtil.setContentView(this, R.layout.widget_ingredient_configure);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        initRecyclerView();

        getLoaderManager().initLoader(RECIPE_CURSOR_LOADER, null, this);
    }

    private void initRecyclerView() {
        mAdapter = new RecipeAdapter(new RecipeAdapter.ClickHandler() {
            @Override
            public void onRecipeClicked(int recipeId) {
                // Get the recipe name from the recipeId
                Context context = IngredientWidgetConfigureActivity.this;
                String recipeName = DatabaseUtils.getRecipeName(context, recipeId);

                // When the button is clicked, store the string locally
                saveTitlePref(context, mAppWidgetId, recipeName);

                // It is the responsibility of the configuration activity to update the app widget
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                IngredientWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId, recipeId);

                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);

                finish();
            }
        });

        // Set the Adapter to utilize the WidgetLayout
        mAdapter.setLayout(WIDGET_LAYOUT);

        // Init the LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        // Set the Adapter and LayoutManager to the RecyclerView
        mBinding.widgetConfigureRecipeRv.setAdapter(mAdapter);
        mBinding.widgetConfigureRecipeRv.setLayoutManager(layoutManager);
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                this,
                RecipeProvider.Recipes.CONTENT_URI,
                RecipeAdapter.Projection.RECIPE_PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        // Set the mem var to the Cursor parameter
        mCursor = cursor;

        // Swap the Cursor into mAdapter
        mAdapter.swapCursor(mCursor);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }
}

