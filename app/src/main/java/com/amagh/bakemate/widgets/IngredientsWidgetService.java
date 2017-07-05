package com.amagh.bakemate.widgets;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.amagh.bakemate.R;
import com.amagh.bakemate.adapters.DetailsAdapter;
import com.amagh.bakemate.data.RecipeProvider;
import com.amagh.bakemate.utils.FormattingUtils;

import static com.amagh.bakemate.adapters.DetailsAdapter.IngredientProjection.IDX_INGREDIENT_MEASURE;
import static com.amagh.bakemate.adapters.DetailsAdapter.IngredientProjection.IDX_INGREDIENT_NAME;
import static com.amagh.bakemate.adapters.DetailsAdapter.IngredientProjection.IDX_INGREDIENT_QUANTITY;
import static com.amagh.bakemate.widgets.IngredientsWidgetService.BundleKeys.RECIPE_ID;

/**
 * Created by Nocturna on 7/4/2017.
 */

public class IngredientsWidgetService extends RemoteViewsService {
    // **Constants** //
    private static final String TAG = IngredientsWidgetService.class.getSimpleName();
    interface BundleKeys {
        String RECIPE_ID = "recipe_id";
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        long recipeId = intent.getLongExtra(RECIPE_ID, 0);

        return new IngredientsRemoteViewsFactory(this, recipeId);
    }

    private class IngredientsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        // **Member Variables** //
        Context mContext;
        Cursor mCursor;
        long mRecipeId;

        IngredientsRemoteViewsFactory(Context context, long recipeId) {
            mContext = context;
            mRecipeId = recipeId;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            // Init the Cursor that will display the data if it is not already initialized
            if (mCursor != null) {
                mCursor.close();
            }

            mCursor = mContext.getContentResolver().query(
                    RecipeProvider.Ingredients.forRecipe(mRecipeId),
                    DetailsAdapter.IngredientProjection.INGREDIENT_PROJECTION,
                    null,
                    null,
                    null);
        }

        @Override
        public void onDestroy() {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        @Override
        public int getCount() {
            // If mCursor is valid, return its count
            if (mCursor != null) {
                return mCursor.getCount();
            }
            return 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            // Move the Cursor to the correct position
            mCursor.moveToPosition(position);

            // Get the details for the ingredient
            double quantity     = mCursor.getDouble(IDX_INGREDIENT_QUANTITY);
            String measure      = mCursor.getString(IDX_INGREDIENT_MEASURE);
            String ingredient   = mCursor.getString(IDX_INGREDIENT_NAME);

            // Format the quantity and measure to display in a single TextView
            String formattedQuantityAndMeasure =
                    FormattingUtils.formatQuantityAndMeasurement(mContext, quantity, measure);

            // Init the RemoteViews and bind the data to the Views
            RemoteViews view = new RemoteViews(mContext.getPackageName(), R.layout.list_item_ingredient_widget);
            view.setTextViewText(R.id.list_ingredient_quantity_tv, formattedQuantityAndMeasure);
            view.setTextViewText(R.id.list_ingredient_tv, ingredient);

            return view;
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
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
