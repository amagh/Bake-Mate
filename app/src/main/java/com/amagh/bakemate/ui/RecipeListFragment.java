package com.amagh.bakemate.ui;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amagh.bakemate.R;
import com.amagh.bakemate.adapters.RecipeAdapter;
import com.amagh.bakemate.data.RecipeContract;
import com.amagh.bakemate.data.RecipeDatabase;
import com.amagh.bakemate.data.RecipeProvider;
import com.amagh.bakemate.databinding.FragmentRecipeDetailsBinding;
import com.amagh.bakemate.databinding.FragmentRecipeListBinding;
import com.amagh.bakemate.sync.SyncRecipesTaskLoader;

/**
 * Created by hnoct on 6/28/2017.
 */

public class RecipeListFragment extends Fragment implements LoaderManager.LoaderCallbacks {
    // **Constants** //
    private static final boolean deleteDatabase = true;
    private static final int RECIPE_CURSOR_LOADER = 3249;
    private static final int SYNC_LOADER = 4654;

    // **Member Variables** //
    private FragmentRecipeListBinding mBinding;
    private RecipeAdapter mAdapter;

    public RecipeListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recipe_list, container);
        mBinding = DataBindingUtil.bind(rootView);

        // For debugging purposes only
        if (deleteDatabase) {
            getActivity().deleteDatabase(RecipeDatabase.DATABASE_NAME);
        }

        // Set up the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        mAdapter = new RecipeAdapter(new RecipeAdapter.ClickHandler() {
            @Override
            public void onRecipeClicked(int recipeId) {
                // Pass click event to RecipeListActivity
                ((RecipeListActivity)getActivity()).onRecipeClicked(recipeId);
            }
        });

        mAdapter.setHasStableIds(true);
        mBinding.recipeListRv.setAdapter(mAdapter);
        mBinding.recipeListRv.setLayoutManager(layoutManager);

        // Initialize Loaders
        getActivity().getSupportLoaderManager().initLoader(SYNC_LOADER, null, this);
        getActivity().getSupportLoaderManager().initLoader(RECIPE_CURSOR_LOADER, null, this);

        return rootView;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        // Create the Loader depending on the Loader's ID
        switch (id) {
            case SYNC_LOADER: {
                return new SyncRecipesTaskLoader(getActivity());
            }

            case RECIPE_CURSOR_LOADER: {
                return new CursorLoader(
                        getActivity(),
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

    public interface RecipeClickCallback {
        void onRecipeClicked(int recipeId);
    }
}
