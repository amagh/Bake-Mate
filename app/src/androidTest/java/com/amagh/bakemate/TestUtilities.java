package com.amagh.bakemate;

import android.content.ContentValues;
import android.database.Cursor;

import com.amagh.bakemate.data.RecipeContract;

import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by hnoct on 6/27/2017.
 */

public class TestUtilities {

    /**
     * Checks values from a Cursor to match the values in the ContentValues
     *
     * @param cursor    Cursor containing database values
     * @param values    ContentValues from which the database values are to be matched against
     */
    public static void validateCursorValues(Cursor cursor, ContentValues values) {
        String nullCursorError = "Cursor is null. Is ContentProvider registered in " +
                "AndroidManifest.xml?";
        assertNotNull(nullCursorError, cursor);

        Set<String> keySet = values.keySet();

        for (String key : keySet) {
            int columnIndex = cursor.getColumnIndex(key);

            String columnNotFoundError = key + " column not found";
            assertFalse(columnNotFoundError, columnIndex == -1);

            String expectedValue = values.getAsString(key);
            String cursorValue = cursor.getString(columnIndex);

            String matchError = "Expected value: " + expectedValue +
                    " does not match actual value: " + cursorValue;

            assertEquals(matchError, expectedValue, cursorValue);
        }
    }

    /**
     * Creates dummy values for the Recipe table for testing purposes
     *
     * @return ContentValues describing a test recipe
     */
    public static ContentValues getRecipeContentValues() {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID, 0);
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME, "RecipeTest");
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_SERVINGS, 8);

        return recipeValues;
    }
}
