package com.amagh.bakemate.utils;

import com.google.android.exoplayer2.SimpleExoPlayer;

/**
 * Created by Nocturna on 7/3/2017.
 */

public interface ManageSimpleExoPlayerInterface {
    /**
     * Retrieves the SimpleExoPlayer to be used for video playback
     *
     * @return SimpleExoPlayer bound to this Activity
     */
    SimpleExoPlayer getPlayer();
}
