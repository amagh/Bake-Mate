package com.amagh.bakemate;


import android.content.Intent;
import android.net.Uri;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.amagh.bakemate.data.RecipeProvider;
import com.amagh.bakemate.ui.StepDetailsActivity;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class StepDetailsActivityTest {
    private IdlingResource mIdlingResource;
    private String shortDescription0 = "Recipe Introduction";
    private String shortDescription1 = "Starting prep";
    private String shortDescription5 = "Finish filling prep";

    @Rule
    public ActivityTestRule<StepDetailsActivity> mActivityTestRule =
            new ActivityTestRule<>(StepDetailsActivity.class, false, false);

    @Before
    public void setUp() {
        // Generate the URI used to display Steps for a Recipe
        Uri stepUri = RecipeProvider.Steps.forRecipe(1);
        Intent intent = new Intent();
        intent.setData(stepUri);

        // Launch the Activity with the Intent
        mActivityTestRule.launchActivity(intent);

        // Register the IdlingResource
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        registerIdlingResources(mIdlingResource);
    }

    @Test
    public void checkClickPause_ShowsPlay() {
        // Check ExoPlayer Control changes from pause to play after clicking pause
        onView(allOf(withId(R.id.exo_pause), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.exo_play), isDisplayed())).check(matches(isDisplayed()));
    }

    @Test
    public void checkClickPlay_ShowsPause() {
        // Click pause button first
        onView(allOf(withId(R.id.exo_pause), isDisplayed())).perform(click());

        // Check ExoPlayer Control changes from play to pause
        onView(allOf(withId(R.id.exo_play), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.exo_pause), isDisplayed())).check(matches(isDisplayed()));
    }

    @Test
    public void checkClickPrev_ProgressBarResets() {
        // Let the video play a bit
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Pause the video
        onView(allOf(withId(R.id.exo_pause), isDisplayed())).perform(click());

        // Click the prev button
        onView(allOf(withId(R.id.exo_prev), isDisplayed())).perform(click());

        // Check the position of the Player
        long position = mActivityTestRule.getActivity().getPlayer().getCurrentPosition();

        // Check that the position reset to 0
        String errorPrevButton = "Position did not reset. Actual position is " + position;
        assertEquals(errorPrevButton, 0, position);
    }

    @Test
    public void swipeLeft_ShowsNextStep() {
        // Check that the first Step shows the correct description
        onView(allOf(withId(R.id.step_details_short_description_tv), isDisplayed())).check(matches(withText(shortDescription0)));

        // Swipe left
        onView(withId(R.id.step_details_vp)).perform(swipeLeft());

        // Check that the new Step shows the correct description
        onView(allOf(withId(R.id.step_details_short_description_tv), isDisplayed())).check(matches(withText(shortDescription1)));
    }

    @Test
    public void clickSlidingPageTab_SwitchSteps() {
        // Click on the PagerSlidingTabStrip for the 5th Step
        onView(allOf(withId(R.id.psts_tab_title), isDisplayed(), withText("Step 5"))).perform(click());

        // Check that the description of the recipe matches Step 5
        onView(allOf(withId(R.id.step_details_short_description_tv), isDisplayed())).check(matches(withText(shortDescription5)));
    }

    @Test
    public void checkExoPlayerControlsHide( ){
        // Wait for the ExoPlayer controls to disappear
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check to make sure the controls are not visible
        onView(allOf(withId(R.id.exo_pause), not(isDisplayed())));
    }

    @Test
    public void clickExoPlayer_HidesControls() {
        // Click the ExoPlayer View to hide the controls
        onView(allOf(withId(R.id.step_details_exo), isDisplayed(), instanceOf(SimpleExoPlayerView.class))).perform(click());

        // Check to make sure the controls are not visible
        onView(allOf(withId(R.id.exo_pause), not(isDisplayed())));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            unregisterIdlingResources(mIdlingResource);
        }
    }
}
