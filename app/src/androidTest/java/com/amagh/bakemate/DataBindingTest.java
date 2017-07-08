package com.amagh.bakemate;

import android.content.ContentValues;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.amagh.bakemate.data.RecipeContract;
import com.amagh.bakemate.data.RecipeDatabase;
import com.amagh.bakemate.ui.RecipeDetailsActivity;
import com.amagh.bakemate.utils.DatabaseUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Created by hnoct on 6/29/2017.
 */

@RunWith(AndroidJUnit4.class)
public class DataBindingTest {
    @Rule
    ActivityTestRule<RecipeDetailsActivity> mActivityRule = new ActivityTestRule<>(RecipeDetailsActivity.class);

    @Before
    public void resetDatabase() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(RecipeDatabase.DATABASE_NAME);
    }

    @Test
    public void DataBindingTest() {
        Context context = InstrumentationRegistry.getTargetContext();
        ContentValues recipeValues = TestUtilities.getRecipeContentValues();

        DatabaseUtils.insertRecipeValues(
                context,
                new ContentValues[] {recipeValues},
                DatabaseUtils.ValueType.RECIPE);

        String recipeName = recipeValues.getAsString(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME);
        int servings = recipeValues.getAsInteger(RecipeContract.RecipeEntry.COLUMN_RECIPE_SERVINGS);
        String servingsString = context.getString(
                R.string.list_details_servings_format,
                servings
        );

        onData(anything()).atPosition(0).inAdapterView(withId(R.id.list_details_name_tv))
                .check(matches(withText(recipeName)));
    }
}
