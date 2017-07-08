package com.amagh.bakemate.models;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.amagh.bakemate.BR;
import com.amagh.bakemate.R;
import com.amagh.bakemate.glide.GlideApp;
import com.amagh.bakemate.glide.RecipeGlideSignature;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

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
    private int visibility = View.VISIBLE;

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
    public String getVideoUrl() {
        return videoUrl;
    }

    @Bindable
    public String getServings() {
        assertNotNull(mContext);
        return mContext.getString(R.string.list_details_servings_format, servings);
    }
}
