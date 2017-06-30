package com.amagh.bakemate.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.amagh.bakemate.BR;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

/**
 * Created by hnoct on 6/29/2017.
 */

public class Step extends BaseObservable {
    private final String videoUrl;
    private final String shortDescription;
    private final String description;

    private int visibility;

    public Step(String videoUrl, String shortDescription, String description) {
        this.videoUrl = videoUrl;
        this.shortDescription = shortDescription;
        this.description = description;

        // If there is no video thumbnail to load, then ProgressBar should be hidden
        visibility = !videoUrl.isEmpty() ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public String getVideoUrl() {
        return videoUrl;
    }

    @Bindable
    public int getVisibility() {
        return visibility;
    }

    @Bindable
    public RequestListener getListener() {
        // Return a RequestListener that merely hides the ProgressBar when Glide has finished
        // loading
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

    @Bindable
    public String getShortDescription() {
        return shortDescription;
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    @BindingAdapter({"bind:videoUrl", "bind:listener"})
    public static void loadVideoThumbnail(ImageView imageView, String videoUrl, RequestListener listener) {
        if (videoUrl.isEmpty()) {
            // Nothing to load
            return;
        }

        Glide.with(imageView.getContext())
                .load(videoUrl)
                .listener(listener)
                .into(imageView);
    }

    private void hideProgressBar() {
        // Set visibility to GONE and then notify the Listener of the change in property
        visibility = View.GONE;
        notifyPropertyChanged(BR.visibility);
    }
}
