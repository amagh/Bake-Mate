package com.amagh.bakemate.ui;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.amagh.bakemate.R;
import com.amagh.bakemate.adapters.RecipeAdapter;
import com.amagh.bakemate.data.RecipeContract;
import com.amagh.bakemate.data.RecipeDatabase;
import com.amagh.bakemate.data.RecipeProvider;
import com.amagh.bakemate.databinding.ActivityRecipeListBinding;
import com.amagh.bakemate.sync.SyncRecipesTaskLoader;

public class RecipeListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_list);
    }


}
