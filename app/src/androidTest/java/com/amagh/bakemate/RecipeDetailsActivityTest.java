package com.amagh.bakemate;

import android.content.Intent;
import android.net.Uri;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.amagh.bakemate.data.RecipeProvider;
import com.amagh.bakemate.ui.RecipeDetailsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by Nocturna on 7/4/2017.
 */

@RunWith(AndroidJUnit4.class)
public class RecipeDetailsActivityTest {
    public static final String STEP_SHORT_DESC = "Starting prep";
    public static final String RECIPE_DETAILS_ACTIVITY_PACKAGE = "com.amagh.bakemate.ui";

    @Rule
    public ActivityTestRule<RecipeDetailsActivity> mActivityRule =
            new ActivityTestRule<>(RecipeDetailsActivity.class, false, false);

    @Test
    public void clickStep_InflatesStepDetailsFragment() {
        // Generate the URI used to initialize the RecipeDetailsActivity
        Uri recipeUri = RecipeProvider.Recipes.withId(1);

        // Generate the Intent to launch the RecipeDetailsActivity including the recipeUri
        Intent intent = new Intent();
        intent.setData(recipeUri);

        // Launch the Activity
        mActivityRule.launchActivity(intent);

        onView(withId(R.id.recipe_details_rv))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(STEP_SHORT_DESC)), click()));
        onView(allOf(withId(R.id.step_details_short_description_tv), isDisplayed())).check(matches(withText(STEP_SHORT_DESC)));
    }
}
