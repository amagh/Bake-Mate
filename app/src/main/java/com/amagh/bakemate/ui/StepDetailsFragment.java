package com.amagh.bakemate.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amagh.bakemate.R;
import com.amagh.bakemate.models.Step;

import static com.amagh.bakemate.ui.StepDetailsFragment.BundleKeys.STEP;

/**
 * Created by hnoct on 6/29/2017.
 */

public class StepDetailsFragment extends Fragment {
    // **Constants** //
    private static final String TAG = StepDetailsFragment.class.getSimpleName();
    interface BundleKeys {
        String STEP = "step";
    }

    // **Member Variables** //
    private Step mStep;

    public static StepDetailsFragment newInstance(Step step) {
        // Init a new Bundle to pass Step
        Bundle args = new Bundle();

        // Add the Step to the Bundle
        args.putParcelable(STEP, step);

        // Initialize a new Fragment with the Bundle attached
        StepDetailsFragment fragment = new StepDetailsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public StepDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_step_details, container, false);

        // Retrieve the Step from the Bundle
        Bundle args = getArguments();

        if (args.containsKey(STEP)) {
            mStep = args.getParcelable(STEP);
        } else {
            Log.d(TAG, "No Step passed to the fragment");
        }

        return rootView;
    }
}
