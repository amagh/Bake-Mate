package com.amagh.bakemate.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.amagh.bakemate.R;
import com.amagh.bakemate.adapters.StepSectionAdapter;
import com.amagh.bakemate.data.RecipeProvider;
import com.amagh.bakemate.models.Step;
import com.amagh.bakemate.utils.DatabaseUtils;
import com.amagh.bakemate.utils.LayoutUtils;
import com.amagh.bakemate.utils.ManageSimpleExoPlayerInterface;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

import static junit.framework.Assert.assertNotNull;

public class RecipeDetailsActivity extends MediaSourceActivity
        implements RecipeDetailsFragment.StepClickCallback, ManageSimpleExoPlayerInterface{
    // **Constants** //
    private static final String TAG = RecipeDetailsActivity.class.getSimpleName();

    // **Member Variables**//
    private Uri mRecipeUri;
    private SimpleExoPlayer mPlayer;

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

        if (savedInstanceState == null && mRecipeUri != null) {
            // Pass the recipeUri to the Fragment as part of an attached Bundle
            Bundle args = new Bundle();
            args.putParcelable(RecipeDetailsFragment.BundleKeys.RECIPE_URI, mRecipeUri);

            RecipeDetailsFragment fragment = new RecipeDetailsFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_recipe_details, fragment)
                    .commit();

            // Check whether StepDetailsFragment needs to be inflated
            if (LayoutUtils.inTwoPane(this)) {
                if (mPlayer == null) {
                    mPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
                }
                // Create the StepDetailsFragment and swap it into the container
                long recipeId = RecipeProvider.getRecipeIdFromUri(mRecipeUri);

                swapStepDetailsFragment(recipeId, 0L);
            }
        }
    }

    @Override
    public void onStepClicked(long recipeId, long stepId) {
        if (getResources().getBoolean(R.bool.two_pane)) {
            swapStepDetailsFragment(recipeId, stepId);
        } else {
            // Start the StepDetailsActivity
            startStepDetailsActivity(recipeId, stepId);
        }
    }

    private void startStepDetailsActivity(long recipeId, long stepId) {
        // Create the URI that will point to the steps for the recipe
        Uri stepsUri = RecipeProvider.Steps.forRecipe(recipeId);

        // Create Intent for RecipeDetailsActivity, set the URI, and put the page selected as an
        // extra
        Intent intent = new Intent(this, StepDetailsActivity.class);
        intent.setData(stepsUri);
        intent.putExtra(StepDetailsActivity.BundleKeys.STEP_ID, stepId);

        // Launch the intent
        startActivity(intent);
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

        // Initialize the StepDetailsFragment with the Step
        StepDetailsFragment detailsFragment = StepDetailsFragment.newInstance(step, (int) stepId);

        // Swap the Fragment into the container
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_step_details, detailsFragment)
                .commit();
    }

    @Override
    public SimpleExoPlayer getPlayer() {
        return mPlayer;
    }
}
