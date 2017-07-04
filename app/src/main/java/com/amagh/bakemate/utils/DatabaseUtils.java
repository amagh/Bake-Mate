package com.amagh.bakemate.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.amagh.bakemate.data.RecipeContract;
import com.amagh.bakemate.data.RecipeProvider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.amagh.bakemate.utils.DatabaseUtils.ValueType.INGREDIENT;
import static com.amagh.bakemate.utils.DatabaseUtils.ValueType.RECIPE;
import static com.amagh.bakemate.utils.DatabaseUtils.ValueType.STEP;
import static junit.framework.Assert.assertNotNull;

/**
 * Contains methods for simple database queries, inserting, and updating ContentValues
 */

public class DatabaseUtils {
    // **Constants** //
    private static final String TAG = DatabaseUtils.class.getSimpleName();

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RECIPE, INGREDIENT, STEP})
    public @interface ValueType {
        int RECIPE = 0;
        int INGREDIENT = 1;
        int STEP = 2;
    }

    /**
     * Helper method for safely bulk-inserting ContentValues into the database
     *
     * @param context       Interface to global Context
     * @param contentValues Array of ContentValues to be bulk-inserted into the database
     * @param type          The type of data described in contentValues
     * @return Number of ContentValues inserted into the database
     */
    public static int insertRecipeValues(@NonNull Context context, ContentValues[] contentValues, @ValueType int type) {
        // Init variables
        Set<Integer> databaseRecipeIds = new HashSet<>();
        Uri uri = null;

        // Set the uri to query/insert based on the ValueType
        switch (type) {
            case RECIPE: {
                uri = RecipeProvider.Recipes.CONTENT_URI;
                break;
            }

            case INGREDIENT: {
                uri = RecipeProvider.Ingredients.CONTENT_URI;
                break;
            }

            case STEP: {
                uri = RecipeProvider.Steps.CONTENT_URI;
                break;
            }
        }

        // Query the database
        assertNotNull(uri);
        Cursor cursor = context.getContentResolver().query(
                uri,
                new String[] {RecipeContract.RecipeEntry.COLUMN_RECIPE_ID},
                null,
                null,
                null
        );

        // Add the recipeIds that already exist in the table to a HashSet to be checked against
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    databaseRecipeIds.add(cursor.getInt(0));
                } while (cursor.moveToNext());
            }

            // Close the Cursor
            cursor.close();
        }

        // Init the List that will contain all the ContentValues to be bulk-inserted
        List<ContentValues> recipeValuesList = new ArrayList<>();

        // Add ContentValues that do not include a recipeId in the table
        for (ContentValues recipeValue : contentValues) {
            int recipeId = recipeValue.getAsInteger(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID);

            if (!databaseRecipeIds.contains(recipeId)) {
                recipeValuesList.add(recipeValue);
            }
        }

        // Bulk insert all valid ContentValues
        assertNotNull(uri);
        context.getContentResolver().bulkInsert(
                uri,
                recipeValuesList.toArray(new ContentValues[recipeValuesList.size()])
        );

        return recipeValuesList.size();
    }

    /**
     * Retrieves the URL for the last video for a given recipe to be used to load a thumbnail for
     * the recipe
     *
     * @param context   Interface to global Context
     * @param recipeId  ID of the recipe to be used to query the database
     * @return String of URL for the video of the last step for the recipe
     */
    public static String getVideoUrlForThumbnail(@NonNull Context context, int recipeId) {
        // Query the database, filtering for the recipeId and sorting by highest step ID
        Cursor cursor = context.getContentResolver().query(
                RecipeProvider.Steps.CONTENT_URI,
                new String[] {
                        RecipeContract.StepEntry.COLUMN_VIDEO_URL,
                        RecipeContract.StepEntry.COLUMN_RECIPE_ID},
                RecipeContract.StepEntry.COLUMN_RECIPE_ID + " = ?",
                new String[] {Integer.toString(recipeId)},
                RecipeContract.StepEntry.COLUMN_STEP_ID + " DESC"
        );

        // Init the variable to return
        String videoUrl = null;

        // Check to see if Cursor is valid, if so retrieve the videoUrl
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                videoUrl = cursor.getString(0);
            }

            // Close the Cursor
            cursor.close();
        }

        return videoUrl;
    }

    /**
     * Queries the database and generates a Cursor pointing to a specific step for a recipe
     *
     * @param context   Interface to global Context
     * @param stepUri   Uri pointing to a specific recipe and step
     * @return Cursor containing the details for a single step, already moved to the first row
     */
    public static Cursor getCursorForStep(@NonNull Context context, Uri stepUri) {
        // Generate a Cursor using the Uri pointing to the specific Step
        Cursor cursor = context.getContentResolver().query(
                stepUri,
                null,
                null,
                null,
                null
        );

        // Return the valid Cursor if there is content
        if (cursor != null && cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }
    }
}
