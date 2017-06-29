package com.amagh.bakemate.ui;

import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amagh.bakemate.R;
import com.amagh.bakemate.databinding.FragmentRecipeDetailsBinding;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecipeDetailsFragment extends Fragment {
    // **Constants** //
    private static final String TAG = RecipeDetailsFragment.class.getSimpleName();
    interface BundleKeys {
        String RECIPE_URI = "recipe_uri";
    }

    // **Member Variables** //
    private FragmentRecipeDetailsBinding mBinding;

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
            Log.d(TAG, "Uri: " + args.getParcelable(BundleKeys.RECIPE_URI));
        }

        return rootView;
    }
}
