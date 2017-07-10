package com.amagh.bakemate.sync;

import android.content.ContentValues;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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

        } catch (IOException e) {
            // Generate a Snackbar to show an error message to the user if unable to connect to the
            // network
            String error = mContext.getString(R.string.error_network);
            final Snackbar snack = Snackbar.make(
                    ((AppCompatActivity) mContext).findViewById(android.R.id.content),
                    error,
                    Snackbar.LENGTH_INDEFINITE);

            // Set onClickListener to dismiss the Snackbar on click
            String dismiss = mContext.getString(R.string.snackbar_dismiss);
            snack.setAction(
                    dismiss,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });

            snack.show();

            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
