package com.amagh.bakemate.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amagh.bakemate.R;
import com.amagh.bakemate.data.RecipeProvider;

public class RecipeListActivity extends AppCompatActivity implements RecipeListFragment.RecipeClickCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
    }

    @Override
    public void onRecipeClicked(int recipeId) {
        startDetailsActivity(recipeId);
    }

    /**
     * Launches RecipeDetailsActivity for a specific recipe
     *
     * @param recipeId  ID of the recipe to open details for
     */
    private void startDetailsActivity(int recipeId) {
        // Generate the URI to load a single recipe
        Uri recipeUri = RecipeProvider.Recipes.withId(recipeId);

        // Create and launch the explicit Intent with recipeUri attached
        Intent intent = new Intent(this, RecipeDetailsActivity.class);
        intent.setData(recipeUri);

        startActivity(intent);
    }
}
