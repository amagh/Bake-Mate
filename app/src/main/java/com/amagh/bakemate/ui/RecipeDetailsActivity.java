package com.amagh.bakemate.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.amagh.bakemate.R;
import com.amagh.bakemate.data.RecipeProvider;
import com.amagh.bakemate.models.Step;
import com.amagh.bakemate.utils.DatabaseUtils;
import com.amagh.bakemate.utils.LayoutUtils;
import com.amagh.bakemate.utils.ManageSimpleExoPlayerInterface;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.amagh.bakemate.ui.RecipeDetailsActivity.LayoutConfiguration.MASTER_DETAIL_FLOW;
import static com.amagh.bakemate.ui.RecipeDetailsActivity.LayoutConfiguration.SINGLE_PANEL;
import static com.amagh.bakemate.ui.RecipeDetailsActivity.SavedInstanceStateKeys.PREVIOUS_CONFIGURATION_KEY;
import static com.amagh.bakemate.ui.StepDetailsActivity.BundleKeys.STEP_ID;
import static com.amagh.bakemate.ui.StepDetailsActivity.BundleKeys.VIDEO_POSITION;
import static junit.framework.Assert.assertNotNull;

public class RecipeDetailsActivity extends MediaSourceActivity
        implements RecipeDetailsFragment.StepClickCallback, ManageSimpleExoPlayerInterface{
    // **Constants** //
    private static final String TAG = RecipeDetailsActivity.class.getSimpleName();
    private static final String STEP_DETAILS_FRAG = "step_details_fragment";
    private static final int STEP_ACTIVITY_REQUEST_CODE = 2731;

    interface SavedInstanceStateKeys {
        String PREVIOUS_CONFIGURATION_KEY   = "previous_config";
        String VIDEO_POSITION               = "video_position";
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SINGLE_PANEL, MASTER_DETAIL_FLOW})
    @interface LayoutConfiguration {
        int SINGLE_PANEL        = 0;
        int MASTER_DETAIL_FLOW  = 1;
    }

    // **Member Variables**//
    private Uri mRecipeUri;
    private SimpleExoPlayer mPlayer;
    public static int sCurrentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Retrieve the URI for the recipe
        Intent intent = getIntent();
        if (intent.getData() != null) {
            mRecipeUri = intent.getData();
        } else {
            Log.d(TAG, "No URI passed!");
        }

        if (savedInstanceState == null) {
            // Pass the recipeUri to the Fragment as part of an attached Bundle
            Bundle args = new Bundle();
            args.putParcelable(RecipeDetailsFragment.BundleKeys.RECIPE_URI, mRecipeUri);

            RecipeDetailsFragment fragment = new RecipeDetailsFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_recipe_details, fragment)
                    .commit();

            // Check whether StepDetailsFragment needs to be inflated
            if (LayoutUtils.inTwoPane(this)) {
                if (mPlayer == null) {
                    mPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
                }
                // Create the StepDetailsFragment and swap it into the container
                long recipeId = RecipeProvider.getRecipeIdFromUri(mRecipeUri);

                swapStepDetailsFragment(recipeId, 0);
            }
        } else {
            // Check whether a layout configuration change has occurred
            @LayoutConfiguration int previousConfig = savedInstanceState.getInt(PREVIOUS_CONFIGURATION_KEY);
            long recipeId = RecipeProvider.getRecipeIdFromUri(mRecipeUri);

            if (previousConfig == MASTER_DETAIL_FLOW && !getResources().getBoolean(R.bool.two_pane)) {
                // Switch from master-detail-flow to single panel. Start StepDetailsActivity,
                // pre-loaded to the current step and video position
                startStepDetailsActivityForResult(
                        recipeId,
                        sCurrentPosition,
                        savedInstanceState.getLong(SavedInstanceStateKeys.VIDEO_POSITION, 0));
            } else if (previousConfig == SINGLE_PANEL && getResources().getBoolean(R.bool.two_pane)) {
                // Switching from single panel to master-detail-flow. Start the SimpleExoPlayer if
                // it hasn't already been loaded
                if (mPlayer == null) {
                    mPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
                }

                // Swap the new Fragment into the container
                swapStepDetailsFragment(recipeId, sCurrentPosition);
            }
        }

    }

    @Override
    public void onStepClicked(long recipeId, long stepId) {
        // Check whether the Activity is using a two-pane layout
        if (getResources().getBoolean(R.bool.two_pane)) {
            // Swap the Step being used by the StepDetailsFragment
            swapStepDetailsFragment(recipeId, stepId);
        } else {
            // Start the StepDetailsActivity
            startStepDetailsActivity(recipeId, stepId);
        }
    }

    /**
     * Creates the Intent to start the StepDetailsActivity
     *
     * @param recipeId  The ID of the recipe to display the steps for in StepDetailsActivity
     * @param stepId    The ID of the step to show in StepDetailsActivity
     * @return Intent to launch StepDetailsActivity for the recipe and step Id parameters
     */
    private Intent getStepDetailsActivityIntent(long recipeId, long stepId) {
        // Create the URI that will point to the steps for the recipe
        Uri stepsUri = RecipeProvider.Steps.forRecipe(recipeId);

        // Create Intent for StepDetailsActivity, set the URI, and put the page selected as an
        // extra
        Intent intent = new Intent(this, StepDetailsActivity.class);
        intent.setData(stepsUri);
        intent.putExtra(STEP_ID, stepId);

        return intent;
    }

    /**
     * Starts the StepDetailsActivity for the specific recipe and pre-loaded with a Fragment
     * representing the stepId for that recipe.
     *
     * @param recipeId  The ID of the recipe to load the steps for
     * @param stepId    The ID of the step to be shown when the Activity has loaded
     */
    private void startStepDetailsActivity(long recipeId, long stepId) {
        // Init then launch the intent
        startActivity(getStepDetailsActivityIntent(recipeId, stepId));
    }

    /**
     * Starts the StepDetailsActivity and waits for a Result to be used to generate an equivalent
     * StepDetailsFragment in the case of layout configuration change.
     *
     * @param recipeId         The ID of the recipe to generate the StepDetailsActivity for
     * @param stepId           The ID of the step to generate the StepDetailsActivity for
     * @param videoPosition    The position of the video prior to the layout change
     */
    private void startStepDetailsActivityForResult(long recipeId, long stepId, long videoPosition) {
        // Generate an Intent to launch the StepDetailsActivity
        Intent intent = getStepDetailsActivityIntent(recipeId, stepId);

        // Add the video's position
        intent.putExtra(VIDEO_POSITION, videoPosition);

        // Start the Activity and await a result (only occurs if there is a layout configuration
        // change)
        startActivityForResult(intent, STEP_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == STEP_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // Retrieve the information to be used to generate the StepDetailsFragment
            long recipeId = RecipeProvider.getRecipeIdFromUri(mRecipeUri);
            int stepId = data.getIntExtra(STEP_ID, 0);

            // Seek the Player to the correct position in the video
            long videoPosition = data.getLongExtra(VIDEO_POSITION, 0);
            mPlayer.seekTo(videoPosition);

            // Swap the StepDetailsFragment with one containing the step info
            swapStepDetailsFragment(recipeId, stepId);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Instantiates a new instance of StepDetailsFragment and swaps it into the the layout container
     *
     * @param recipeId  The ID of the recipe containing the Step
     * @param stepId    The ID of the step to generate a Fragment for
     */
    private void swapStepDetailsFragment(long recipeId, long stepId) {
        // Generate a Cursor with the Step's details
        Uri stepUri = RecipeProvider.Steps.forRecipeAndStep(recipeId, stepId);
        Cursor cursor = DatabaseUtils.getCursorForStep(this, stepUri);

        // Generate a Step from the Cursor
        Step step = Step.createStepFromCursor(cursor);
        step.setStepId((int) stepId);

        // Close the Cursor
        assertNotNull(cursor);
        cursor.close();

        // Check if the StepDetailsFragment has already been inflated
        StepDetailsFragment detailsFragment =
                (StepDetailsFragment)getSupportFragmentManager().findFragmentByTag(STEP_DETAILS_FRAG);

        // Check to see if a valid StepDetailsFragment has already been inflated
        if (detailsFragment == null || detailsFragment.getView() == null) {
            // Initialize the StepDetailsFragment with the Step
            detailsFragment = StepDetailsFragment.newInstance(step, (int) stepId);

            // Swap the Fragment into the container
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_step_details, detailsFragment, STEP_DETAILS_FRAG)
                    .commit();
        } else {
            // Swap the Step being used by the StepDetailsFragment
            detailsFragment.swapStep(step);
        }

        // Set the current position to the stepId
        sCurrentPosition = (int) stepId;
    }

    @Override
    public SimpleExoPlayer getPlayer() {
        return mPlayer;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Release SimpleExoPlayer assets
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Init the int to be put into the Bundle
        @LayoutConfiguration int layoutConfig;

        // Set layoutConfig based on whether layout uses master-detail-flow
        if (getResources().getBoolean(R.bool.two_pane)) {
            layoutConfig = LayoutConfiguration.MASTER_DETAIL_FLOW;
        } else {
            layoutConfig = LayoutConfiguration.SINGLE_PANEL;
        }

        // Save the layout config in the Bundle
        outState.putInt(PREVIOUS_CONFIGURATION_KEY, layoutConfig);

        // Save the video's position in the Bundle
        if (mPlayer != null) {
            outState.putLong(SavedInstanceStateKeys.VIDEO_POSITION, mPlayer.getCurrentPosition());
        }
    }
}
