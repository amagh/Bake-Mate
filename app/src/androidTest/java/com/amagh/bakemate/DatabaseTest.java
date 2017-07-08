package com.amagh.bakemate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.amagh.bakemate.data.RecipeDatabase;
import com.amagh.bakemate.data.RecipeProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by hnoct on 6/27/2017.
 */

@SuppressWarnings("WeakerAccess")
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    // **Member Variables** //
    Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testInsertRecipe() {
        // Get dummy recipe values
        ContentValues recipeValues = TestUtilities.getRecipeContentValues();

        // Insert values into database
        mContext.getContentResolver().insert(
                RecipeProvider.Recipes.CONTENT_URI,
                recipeValues
        );

        // Query database
        Cursor cursor = mContext.getContentResolver().query(
                RecipeProvider.Recipes.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Check the Cursor
        checkCursor(cursor, recipeValues);
    }

    @Test
    public void testInsertIngredient() {
        // Insert recipe values into the database because of ForeignKey Constraint
        testInsertRecipe();

        // Get dummy ingredient values
        ContentValues ingredientValues = TestUtilities.getIngredientValues();

        // Insert values into database
        mContext.getContentResolver().insert(
                RecipeProvider.Ingredients.CONTENT_URI,
                ingredientValues
        );

        // Query the database
        Cursor cursor = mContext.getContentResolver().query(
                RecipeProvider.Ingredients.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Check the Cursor
        checkCursor(cursor, ingredientValues);
    }

    @Test
    public void testInsertStep() {
        testInsertRecipe();

        ContentValues stepValues = TestUtilities.getStepValues();

        mContext.getContentResolver().insert(
                RecipeProvider.Steps.CONTENT_URI,
                stepValues
        );

        Cursor cursor = mContext.getContentResolver().query(
                RecipeProvider.Steps.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        checkCursor(cursor, stepValues);
    }

    /**
     * Checks to ensure the Cursor is valid and that it contains values that match the ContentValues
     * use to generate the database values.
     *
     * @param cursor    Cursor containing database values
     * @param values    ContentValues to check against
     */
    public void checkCursor(Cursor cursor, ContentValues values) {
        // Check to ensure Cursor is not null
        String nullCursorError = "Error querying database. Cursor is null.";
        assertNotNull(nullCursorError, cursor);

        // Match database values to inserted ContentValues
        String emptyCursorError = "Error - Cursor does not contain any values.";
        assertTrue(emptyCursorError, cursor.moveToFirst());

        TestUtilities.validateCursorValues(cursor, values);

        // Close the Cursor
        cursor.close();
    }

    @After
    public void tearDown() {
        String errorDeletingDatabse = "Database was not successfully deleted.";
        assertTrue(errorDeletingDatabse, mContext.deleteDatabase(RecipeDatabase.DATABASE_NAME));
    }
}
