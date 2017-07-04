package com.amagh.bakemate.utils;

import android.content.Context;
import android.view.ViewGroup;

import com.amagh.bakemate.R;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

/**
 * Created by Nocturna on 7/2/2017.
 */

public class LayoutUtils {

    /**
     * Checks whether the current layout utilizes a two pane master-detail-flow configuration
     *
     * @param context Interface to global Context
     * @return boolean value true if layout has two panes, false otherwise
     */
    public static boolean inTwoPane(Context context) {
        return context.getResources().getBoolean(R.bool.two_pane);
    }

    /**
     * Sets the PlayerView's height to match the width and a 16:9 aspect ratio
     *
     * @param playerView SimpleExoPlayerView to set the height for
     * @return The new height of the SimpleExoPlayerView
     */
    public static int setPlayerViewHeight(SimpleExoPlayerView playerView) {
        // Retrieve the measured width of the PlayerView
        ViewGroup.LayoutParams params = playerView.getLayoutParams();

        // Calculate the height required to display at 16:9 video
        int height = params.width / 16 * 9;

        // Modify the LayoutParams and set it to the View
        params.height = height;
        playerView.setLayoutParams(params);

        return height;
    }
}
