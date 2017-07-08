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

    public static ContentValues getIngredientValues() {
        ContentValues ingredientValues = new ContentValues();
        ingredientValues.put(RecipeContract.IngredientEntry.COLUMN_RECIPE_ID, 0);
        ingredientValues.put(RecipeContract.IngredientEntry.COLUMN_QUANTITY, 12);
        ingredientValues.put(RecipeContract.IngredientEntry.COLUMN_MEASURE, "CUP");
        ingredientValues.put(RecipeContract.IngredientEntry.COLUMN_INGREDIENT, "flour");

        return ingredientValues;
    }

    public static ContentValues getStepValues() {
        ContentValues stepValues = new ContentValues();
        stepValues.put(RecipeContract.StepEntry.COLUMN_STEP_ID, 0);
        stepValues.put(RecipeContract.StepEntry.COLUMN_RECIPE_ID, 0);
        stepValues.put(RecipeContract.StepEntry.COLUMN_SHORT_DESC, "Recipe Instruction");
        stepValues.put(RecipeContract.StepEntry.COLUMN_DESCRIPTION, "Complete Recipe Instruction");
        stepValues.put(
                RecipeContract.StepEntry.COLUMN_VIDEO_URL,
                "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4"
        );

        return stepValues;
    }
}
