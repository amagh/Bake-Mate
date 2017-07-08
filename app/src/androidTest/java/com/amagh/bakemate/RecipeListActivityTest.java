package com.amagh.bakemate;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.amagh.bakemate.ui.RecipeListActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Nocturna on 7/4/2017.
 */

@SuppressWarnings("WeakerAccess")
@RunWith(AndroidJUnit4.class)
public class RecipeListActivityTest {
    public static final String RECIPE_NAME = "Nutella Pie";

    @Rule
    public ActivityTestRule<RecipeListActivity> mActivityRule =
            new ActivityTestRule<>(RecipeListActivity.class);

    @Test
    public void clickRecipe_OpensRecipeDetailsActivity() {
        onView(withId(R.id.recipe_list_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.list_details_name_tv)).check(matches(withText(RECIPE_NAME)));
    }
}
