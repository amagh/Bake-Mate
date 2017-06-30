package com.amagh.bakemate.ui;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
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
import com.amagh.bakemate.databinding.FragmentRecipeDetailsBinding;
import com.amagh.bakemate.databinding.FragmentStepDetailsBinding;
import com.amagh.bakemate.models.Step;

import static com.amagh.bakemate.ui.StepDetailsFragment.BundleKeys.STEP;
import static com.amagh.bakemate.ui.StepDetailsFragment.BundleKeys.STEP_ID;

/**
 * Created by hnoct on 6/29/2017.
 */

public class StepDetailsFragment extends Fragment {
    // **Constants** //
    private static final String TAG = StepDetailsFragment.class.getSimpleName();
    interface BundleKeys {
        String STEP = "step";
        String STEP_ID = "step_id";
    }

    // **Member Variables** //
    private Step mStep;
    private int mStepid;
    private FragmentStepDetailsBinding mBinding;

    public static StepDetailsFragment newInstance(Step step, int stepId) {
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
        mBinding = DataBindingUtil.bind(rootView);

        // Retrieve the Step from the Bundle
        Bundle args = getArguments();

        if (args.containsKey(STEP)) {
            mStep = args.getParcelable(STEP);
        } else {
            Log.d(TAG, "No Step passed to the fragment");
        }

        // Bind data to the View
        mBinding.setStep(mStep);

        // Set a PageChangeCallBack to be notified when the user changes pages
        ((StepDetailsActivity) getActivity()).setPageChangeCallBack(mPageChangeCallBack);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Release the Player
        releasePlayer();

        // Unregister the PageChangeCallBack
        ((StepDetailsActivity) getActivity()).removePageChangeCallBack(mPageChangeCallBack);
    }

    /**
     * Stops media playback and released the player
     */
    private void releasePlayer() {
        if (mStep.getPlayer() == null) return;

        mStep.getPlayer().stop();
        mStep.getPlayer().release();
    }

    StepDetailsActivity.PageChangeCallBack mPageChangeCallBack = new StepDetailsActivity.PageChangeCallBack() {
        @Override
        public void onPageChanged(int currentPage) {
            // If the step has not video, do nothing
            if (mStep.getPlayer() == null) return;

            if (currentPage == mStep.getStepId()) {
                // If the currentPage matches the Fragment's position, being playback
                mStep.getPlayer().setPlayWhenReady(true);
            } else {
                // If the currentPage does not match, stop the playback
                mStep.getPlayer().stop();
            }
        }
    };
}
