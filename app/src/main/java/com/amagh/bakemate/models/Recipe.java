package com.amagh.bakemate.models;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import android.databinding.BindingAdapter;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.amagh.bakemate.BR;
import com.amagh.bakemate.R;
import com.amagh.bakemate.glide.GlideApp;
import com.amagh.bakemate.glide.RecipeGlideSignature;
import com.bumptech.glide.load.DataSource;
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
    private final String imageUrl;

    private int imageVisibility;
    private int progressBarVisibility;

    // Used for recipe details screen
    public Recipe(Context context, String name, int servings, String imageUrl) {
        this.name = name;
        this.servings = servings;
        this.imageUrl = imageUrl;
        mContext = context;

        imageVisibility = !imageUrl.isEmpty() ? View.VISIBLE : View.GONE;
        progressBarVisibility = !imageUrl.isEmpty() ? View.VISIBLE : View.GONE;
    }

    // For recipe list screen
    public Recipe(String name, String imageUrl) {
        this.name = name;
        this.servings = -1;
        this.imageUrl = imageUrl;
        mContext = null;

        imageVisibility = !imageUrl.isEmpty() ? View.VISIBLE : View.GONE;
        progressBarVisibility = !imageUrl.isEmpty() ? View.VISIBLE : View.GONE;
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

    @Bindable
    public String getImageUrl() {
        return imageUrl;
    }

    @Bindable
    public int getImageVisibility() {
        return imageVisibility;
    }

    @Bindable
    public int getProgressBarVisibility() {
        return progressBarVisibility;
    }

    @Bindable
    public RequestListener getListener() {
        return new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                hideProgressBar();
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                hideProgressBar();
                return false;
            }
        };
    }

    @BindingAdapter({"bind:imageUrl", "bind:listener"})
    public static void loadImageUrl(ImageView imageView, String imageUrl, RequestListener listener) {
        GlideApp.with(imageView.getContext())
                .load(imageUrl)
                .signature(new RecipeGlideSignature(imageView.getContext().getResources().getInteger(R.integer.glide_current_version)))
                .listener(listener)
                .into(imageView);
    }

    public void hideProgressBar() {
        this.progressBarVisibility = View.GONE;

        notifyPropertyChanged(BR.progressBarVisibility);
    }
}
