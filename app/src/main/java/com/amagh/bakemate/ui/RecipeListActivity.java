package com.amagh.bakemate.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amagh.bakemate.R;

public class RecipeListActivity extends AppCompatActivity implements RecipeListFragment.RecipeClickCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
    }

    @Override
    public void onRecipeClicked(int recipeId) {

    }

    private void startDetailsActivity(int recipeId) {

    }
}
