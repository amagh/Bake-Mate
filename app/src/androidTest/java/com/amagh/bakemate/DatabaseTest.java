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

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    // **Member Variables** //
    Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testProviderInsert() {
        // Get dummy recipe values
        ContentValues recipeValues = TestUtilities.getRecipeContentValues();

        // Insert values into database
        mContext.getContentResolver().insert(
                RecipeProvider.Recipes.RECIPES,
                recipeValues
        );

        // Query database
        Cursor cursor = mContext.getContentResolver().query(
                RecipeProvider.Recipes.RECIPES,
                null,
                null,
                null,
                null
        );

        // Check to ensure Cursor is not null
        String nullCursorError = "Error querying database. Cursor is null.";
        assertNotNull(nullCursorError, cursor);

        // Match database values to inserted ContentValues
        String emptyCursorError = "Error - Cursor does not contain any values.";
        assertTrue(emptyCursorError, cursor.moveToFirst());

        TestUtilities.validateCursorValues(cursor, recipeValues);

        // Close the Cursor
        cursor.close();
    }

    @After
    public void tearDown() {
        String errorDeletingDatabse = "Database was not successfully deleted.";
        assertTrue(errorDeletingDatabse, mContext.deleteDatabase(RecipeDatabase.DATABASE_NAME));
    }
}
