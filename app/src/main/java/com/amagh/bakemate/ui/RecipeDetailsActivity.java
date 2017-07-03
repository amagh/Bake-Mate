package com.amagh.bakemate.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.amagh.bakemate.R;
import com.amagh.bakemate.data.RecipeProvider;
import com.amagh.bakemate.models.Step;
import com.amagh.bakemate.utils.DatabaseUtils;
import com.amagh.bakemate.utils.LayoutUtils;
import com.google.android.exoplayer2.SimpleExoPlayer;

import static junit.framework.Assert.assertNotNull;

public class RecipeDetailsActivity extends AppCompatActivity implements RecipeDetailsFragment.StepClickCallback{
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
                // Generate the Cursor to be used to create the Step for the Fragment
                long recipeId = RecipeProvider.getRecipeIdFromUri(mRecipeUri);

                Uri stepUri = RecipeProvider.Steps.forRecipeAndStep(recipeId, 0);
                Cursor stepCursor = DatabaseUtils.getCursorForStep(this, stepUri);

                // Generate the Step from the Cursor
                Step step = Step.createStepFromCursor(stepCursor);

                // Close the Cursor
                assertNotNull(stepCursor);
                stepCursor.close();

                // Create the Fragment containing the Step details and inflate it into the view
                StepDetailsFragment detailsFragment = StepDetailsFragment.newInstance(step, 0);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_step_details, detailsFragment)
                        .commit();
            }
        }
    }

    @Override
    public void onStepClicked(long recipeId, long stepId) {
        // Start the StepDetailsActivity
        startStepDetailsActivity(recipeId, stepId);

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
}
