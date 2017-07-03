package com.amagh.bakemate.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.amagh.bakemate.R;
import com.amagh.bakemate.data.RecipeProvider;
import com.amagh.bakemate.models.Step;
import com.amagh.bakemate.utils.DatabaseUtils;
import com.amagh.bakemate.utils.LayoutUtils;

public class RecipeDetailsActivity extends AppCompatActivity implements RecipeDetailsFragment.StepClickCallback{
    // **Constants** //
    private static final String TAG = RecipeDetailsActivity.class.getSimpleName();

    // **Member Variables**//
    private Uri mRecipeUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

            if (LayoutUtils.inTwoPane(this)) {
                long recipeId = RecipeProvider.getRecipeIdFromUri(mRecipeUri);

                Uri stepUri = RecipeProvider.Steps.forRecipeAndStep(recipeId, 0);

                Step step = Step.createStepFromCursor(DatabaseUtils.getCursorForStep(this, stepUri));
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
