package com.amagh.bakemate.models;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import android.view.View;

import com.amagh.bakemate.R;


import static junit.framework.Assert.assertNotNull;

/**
 * Created by hnoct on 6/29/2017.
 */

public class Recipe extends BaseObservable {
    // **Member Variables** //
    private final Context mContext;
    private final String name;
    private final int servings;

    private final String videoUrl;

    // Used for recipe details screen
    public Recipe(Context context, String name, int servings) {
        this.name = name;
        this.servings = servings;
        this.videoUrl = null;
        mContext = context;
    }

    // For recipe list screen
    public Recipe(String name, String videoUrl) {
        this.name = name;
        this.servings = -1;
        this.videoUrl = videoUrl;
        mContext = null;
    }

    @Bindable
    public String getName() {
        return name;
    }

    @Bindable
    public String getServings() {
        assertNotNull(mContext);
        return mContext.getString(R.string.list_details_servings_format, servings);
    }
}
