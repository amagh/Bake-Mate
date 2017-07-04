package com.amagh.bakemate.ui;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import com.amagh.bakemate.R;
import com.amagh.bakemate.models.Step;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by Nocturna on 7/3/2017.
 */

public class MediaSourceActivity extends AppCompatActivity {

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
}
