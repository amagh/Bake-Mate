package com.amagh.bakemate.utils;

import com.amagh.bakemate.models.Step;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;

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
