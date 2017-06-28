package com.amagh.bakemate.ui;

import android.databinding.DataBindingUtil;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amagh.bakemate.R;
import com.amagh.bakemate.data.RecipeDatabase;
import com.amagh.bakemate.databinding.ActivityRecipeListBinding;
import com.amagh.bakemate.sync.SyncRecipesTaskLoader;

public class RecipeListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {
    // **Constants** //
    private static final boolean deleteDatabase = true;
    private static final int SYNC_LOADER = 4654;

    // **Member Variables **//
    ActivityRecipeListBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_list);

        // For debugging purposes only
        if (deleteDatabase) {
            deleteDatabase(RecipeDatabase.DATABASE_NAME);
        }

        getSupportLoaderManager().initLoader(SYNC_LOADER, null, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new SyncRecipesTaskLoader(RecipeListActivity.this);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
