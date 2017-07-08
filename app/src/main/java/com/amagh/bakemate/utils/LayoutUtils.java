package com.amagh.bakemate.utils;

import android.content.Context;
import android.view.ViewGroup;

import com.amagh.bakemate.R;
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

}
