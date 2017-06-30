package com.amagh.bakemate.ui;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amagh.bakemate.R;
import com.amagh.bakemate.adapters.DetailsAdapter;
import com.amagh.bakemate.adapters.RecipeAdapter;
import com.amagh.bakemate.data.RecipeProvider;
import com.amagh.bakemate.databinding.FragmentRecipeDetailsBinding;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecipeDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // **Constants** //
    private static final String TAG = RecipeDetailsFragment.class.getSimpleName();
    interface BundleKeys {
        String RECIPE_URI = "recipe_uri";
    }
    private static final int RECIPE_CURSOR_LOADER = 9657;
    private static final int STEPS_CURSOR_LOADER = 1489;
    private static final int INGREDIENTS_CURSOR_LOADER = 2385;
    private static final int[] LOADER_IDS = {
            RECIPE_CURSOR_LOADER,
            STEPS_CURSOR_LOADER,
            INGREDIENTS_CURSOR_LOADER
    };

    // **Member Variables** //
    private FragmentRecipeDetailsBinding mBinding;

    private Uri mRecipeUri;
    private long mRecipeId;

    private DetailsAdapter mAdapter;

    private Cursor mRecipeCursor;
    private Cursor mStepsCursor;
    private Cursor mIngredientsCursor;

    public RecipeDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_details, container, false);
        mBinding = DataBindingUtil.bind(rootView);

        // Retrieve the recipeUri from the Bundle
        Bundle args = getArguments();

        if (args.containsKey(BundleKeys.RECIPE_URI)) {
            mRecipeUri = args.getParcelable(BundleKeys.RECIPE_URI);
            mRecipeId = RecipeProvider.getRecipeIdFromUri(mRecipeUri);
        } else {
            Log.d(TAG, "No URI passed");
        }

        // Init CursorLoader
        for (int loaderId : LOADER_IDS) {
            getActivity().getSupportLoaderManager().initLoader(loaderId, null, this);
        }

        initRecyclerView();

        return rootView;
    }

    /**
     * Initializes and sets the Adapter and LayoutManager for the RecyclerView
     */
    private void initRecyclerView() {
        // Init the Adapter and LayoutManager
        mAdapter = new DetailsAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        // Set them to the RecyclerView
        mBinding.recipeDetailsRv.setAdapter(mAdapter);
        mBinding.recipeDetailsRv.setLayoutManager(layoutManager);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Init Uri variable for building CursorLoader
        Uri uri = null;
        String[] projection = null;

        // Set uri and projection accordingly
        switch (id) {
            case RECIPE_CURSOR_LOADER:
                uri = mRecipeUri;
                projection = DetailsAdapter.RecipeProjection.RECIPE_DETAILS_PROJECTION;
                break;

            case INGREDIENTS_CURSOR_LOADER:
                uri = RecipeProvider.Ingredients.forRecipe(mRecipeId);
                projection = DetailsAdapter.IngredientProjection.INGREDIENT_PROJECTION;
                break;

            case STEPS_CURSOR_LOADER:
                uri = RecipeProvider.Steps.forRecipe(mRecipeId);
                projection = DetailsAdapter.StepProjection.STEP_PROJECTION;
                break;
        }

        return new CursorLoader(
                getActivity(),
                uri,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Set member variable to the loaded Cursor
        switch (loader.getId()) {
            case RECIPE_CURSOR_LOADER: {
                mRecipeCursor = data;
                mAdapter.swapRecipeCursor(mRecipeCursor);
                break;
            }

            case STEPS_CURSOR_LOADER: {
                mStepsCursor = data;
                mAdapter.swapStepsCursor(mStepsCursor);
                break;
            }

            case INGREDIENTS_CURSOR_LOADER: {
                mIngredientsCursor = data;
                mAdapter.swapIngredientsCursor(mIngredientsCursor);
                break;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
