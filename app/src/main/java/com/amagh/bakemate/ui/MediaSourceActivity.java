package com.amagh.bakemate.ui;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.amagh.bakemate.R;
import com.amagh.bakemate.models.Step;
import com.amagh.bakemate.utils.LayoutUtils;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import static com.amagh.bakemate.ui.MediaSourceActivity.SavedInstanceStateKeys.CURRENT_POSITION_KEY;
import static com.amagh.bakemate.ui.MediaSourceActivity.SavedInstanceStateKeys.PREVIOUS_CONFIGURATION_KEY;
import static com.amagh.bakemate.ui.MediaSourceActivity.SavedInstanceStateKeys.STEPS_KEY;
import static com.amagh.bakemate.ui.RecipeDetailsActivity.LayoutConfiguration.MASTER_DETAIL_FLOW;
import static com.amagh.bakemate.ui.RecipeDetailsActivity.LayoutConfiguration.SINGLE_PANEL;

/**
 * Created by Nocturna on 7/3/2017.
 */

@SuppressWarnings("WeakerAccess")
@SuppressLint("Registered")
public class MediaSourceActivity extends AppCompatActivity {
    // **Constants** //
    interface SavedInstanceStateKeys {
        String STEPS_KEY                    = "steps";
        String CURRENT_POSITION_KEY         = "current_position";
        String PREVIOUS_CONFIGURATION_KEY   = "previous_config";
    }

    // **Member Variables** //
    protected Step[] mSteps;
    protected int mCurrentPosition;
    protected SimpleExoPlayer mPlayer;
    @RecipeDetailsActivity.LayoutConfiguration protected int mLayoutConfig;



    /**
     * Creates the ExtractorMediaSource to be used by SimpleExoPlayer from a Step containing a
     * URL to a video
     *
     * @param step Step containing a videoUrl
     * @return ExtractorMediaSource to play the step's associated video
     */
    public ExtractorMediaSource getMediaSource(Step step) {
        if (step.getVideoUrl() == null || step.getVideoUrl().isEmpty()) {
            // No video URL, nothing to generate
            return null;
        }

        // Retrieve the video URL
        Uri videoUri = Uri.parse(step.getVideoUrl());

        // Generate the user agent needed for the ExtractorMediaSource
        String userAgent = Util.getUserAgent(this, getString(R.string.app_name));

        // Init and return the ExtractorMediaSource
        return new ExtractorMediaSource(
                videoUri,
                new DefaultDataSourceFactory(this, userAgent),
                new DefaultExtractorsFactory(),
                null,
                null
        );
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the mem var to current LayoutConfiguration so it can be saved in onSaveInstanceState
        if (LayoutUtils.inTwoPane(this)) {
            mLayoutConfig = MASTER_DETAIL_FLOW;
        } else {
            mLayoutConfig = SINGLE_PANEL;
        }

        // Restore member variables
        if (savedInstanceState != null) {
            // Retrieve member variable values from savedInstanceState
            mSteps = (Step[]) savedInstanceState.getParcelableArray(STEPS_KEY);

            mCurrentPosition = savedInstanceState.getInt(CURRENT_POSITION_KEY);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the playerPosition of the video playing
        if (mSteps != null && mSteps[mCurrentPosition] != null) {
            mSteps[mCurrentPosition].stopPlayer();
        }

        // Save member variables
        outState.putParcelableArray(STEPS_KEY, mSteps);
        outState.putInt(CURRENT_POSITION_KEY, mCurrentPosition);

        // Save the layout config in the Bundle
        outState.putInt(PREVIOUS_CONFIGURATION_KEY, mLayoutConfig);

        // Release the player
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
        }
    }
}
