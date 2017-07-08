package com.amagh.bakemate.sync;

import android.content.ContentValues;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.amagh.bakemate.R;
import com.amagh.bakemate.utils.DatabaseUtils;
import com.amagh.bakemate.utils.NetworkUtils;
import com.amagh.bakemate.utils.ParsingUtils;

import org.json.JSONException;

import java.io.IOException;

/**
 * AsyncTaskLoader class to load data from the recipe URL and then bulk-insert the data into the
 * database.
 */

public class SyncRecipesTaskLoader extends AsyncTaskLoader<Void> {
    private final Context mContext;

    public SyncRecipesTaskLoader(Context context) {
        super(context);

        // Interface to global Context
        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        // Start background loading procedures
        forceLoad();
    }

    @Override
    public Void loadInBackground() {
        // Get the URL to connect to
        String url = mContext.getString(R.string.udacity_recipe_url);

        try {
            // Get the JSONObject from the URL
            String jsonResponse = NetworkUtils.getHttpResponse(url);

            // Parse the JSON Document to ContentValues to insert into the database
            ContentValues[] recipeValues = ParsingUtils.getRecipeContentValuesFromJson(jsonResponse);
            ContentValues[] ingredientValues = ParsingUtils.getIngredientValuesFromJson(jsonResponse);
            ContentValues[] stepValues = ParsingUtils.getStepContentValuesFromJson(jsonResponse);

            // Bulk insert ContentValues into database
            DatabaseUtils.insertRecipeValues(mContext, recipeValues, DatabaseUtils.ValueType.RECIPE);
            DatabaseUtils.insertRecipeValues(mContext, ingredientValues, DatabaseUtils.ValueType.INGREDIENT);
            DatabaseUtils.insertRecipeValues(mContext, stepValues, DatabaseUtils.ValueType.STEP);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
