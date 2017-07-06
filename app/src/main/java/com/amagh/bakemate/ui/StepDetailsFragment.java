package com.amagh.bakemate.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amagh.bakemate.R;
import com.amagh.bakemate.databinding.FragmentStepDetailsBinding;
import com.amagh.bakemate.models.Step;
import com.amagh.bakemate.utils.LayoutUtils;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import static com.amagh.bakemate.ui.StepDetailsFragment.BundleKeys.STEP;
import static junit.framework.Assert.assertNotNull;

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

        // Setup the SimpleExoPlayer for the Step
        if (getActivity() instanceof StepDetailsActivity) {
            // Bind data to the View
            mBinding.setStep(mStep);

            // Start the video playback if the Fragment is visible
            if (StepDetailsActivity.sCurrentPosition == mStep.getStepId()) {
                // Because the Step is not destroyed during rotation, it will still have a reference
                // to the media source. Only set the media source if the Fragment is new.
                ExtractorMediaSource mediaSource;

                if (savedInstanceState == null) {
                    mediaSource = ((StepDetailsActivity) getActivity()).getPagerAdapter().getMediaSource(mStep.getStepId());
                } else {
                    mediaSource = null;
                }

                mStep.setPlayer(getActivity(), mediaSource);

                // Set the currentPage variable in the StepPagerAdapter so the Adapter can properly
                // track page changes
                ((StepDetailsActivity) getActivity()).getPagerAdapter().setCurrentPage(mStep.getStepId());
            }
        } else if (getActivity() instanceof MediaSourceActivity) {
            // Swap the Step being used by the Fragment
            swapStep(mStep);
        }

        return rootView;
    }

    // Runnable to be run after the Views have been laid out so that the width of the PlayerView can
    // be properly attained and the height properly calculated
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Set the Player's height based on the PlayerView's width
            LayoutUtils.setPlayerViewHeight(mBinding.stepDetailsExo);
        }
    };

    /**
     * Swaps the Step data being used to bind the Views
     *
     * @param step  The Step to be used to populate the Views
     */
    public void swapStep(Step step) {
        if (mStep != step) {
            // Set the Step as the mem var
            mStep = step;
        }

        // Bind the data to the View
        mBinding.setStep(mStep);

        // Generate the ExtractorMediaSource
        ExtractorMediaSource mediaSource = ((MediaSourceActivity) getActivity()).getMediaSource(mStep);

        // Set the SimpleExoPlayer for the Step to utilize the ExtractorMediaSource
        mStep.setPlayer(getActivity(), mediaSource);

        // Set the PlayerView's height to properly display at 16:9 video
        mBinding.stepDetailsExo.post(runnable);
    }

    /**
     * Retrieves the Step bound to this Fragment
     *
     * @return The Step bound to this Fragment
     */
    public Step getStep() {
        return mStep;
    }
}
