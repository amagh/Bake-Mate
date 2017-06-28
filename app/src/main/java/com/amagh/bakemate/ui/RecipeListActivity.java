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

public class RecipeListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {
    // **Constants** //
    private static final boolean deleteDatabase = true;
    private static final int RECIPE_CURSOR_LOADER = 3249;
    private static final int SYNC_LOADER = 4654;

    // **Member Variables **//
    ActivityRecipeListBinding mBinding;
    RecipeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_list);

        // For debugging purposes only
        if (deleteDatabase) {
            deleteDatabase(RecipeDatabase.DATABASE_NAME);
        }

        // Set up the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new RecipeAdapter();
        mAdapter.setHasStableIds(true);
        mBinding.recipeListRv.setAdapter(mAdapter);
        mBinding.recipeListRv.setLayoutManager(layoutManager);

        // Initialize Loaders
        getSupportLoaderManager().initLoader(SYNC_LOADER, null, this);
        getSupportLoaderManager().initLoader(RECIPE_CURSOR_LOADER, null, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        // Create the Loader depending on the Loader's ID
        switch (id) {
            case SYNC_LOADER: {
                return new SyncRecipesTaskLoader(RecipeListActivity.this);
            }

            case RECIPE_CURSOR_LOADER: {
                return new CursorLoader(
                        this,
                        RecipeProvider.Recipes.RECIPES,
                        RecipeAdapter.Projection.RECIPE_PROJECTION,
                        null,
                        null,
                        RecipeContract.RecipeEntry.COLUMN_RECIPE_ID
                );
            }

            default: {
                throw new UnsupportedOperationException("Unknown Loader ID: " + id);
            }
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case RECIPE_CURSOR_LOADER: {
                // Swap the Cursor into the Adapter
                mAdapter.swapCursor((Cursor) data);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        switch (loader.getId()) {
            case RECIPE_CURSOR_LOADER: {
                // Swap in null while Loader is restarting
                mAdapter.swapCursor(null);
            }
        }
    }
}
