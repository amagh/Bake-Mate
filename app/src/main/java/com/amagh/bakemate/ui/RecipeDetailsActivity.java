package com.amagh.bakemate.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.amagh.bakemate.R;
import com.amagh.bakemate.data.RecipeDatabase;

public class RecipeDetailsActivity extends AppCompatActivity implements RecipeDetailsFragment.StepClickCallback{
    // **Constants** //
    private static final String TAG = RecipeDetailsActivity.class.getSimpleName();

    // **Member Variables**//
    private Uri mRecipeUri;

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
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onStepClicked(long recipeId, long stepId) {

    }
}
