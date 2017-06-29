package com.amagh.bakemate.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by hnoct on 6/29/2017.
 */

public class Step extends BaseObservable {
    private final String videoUrl;
    private final String shortDescription;
    private final String description;

    public Step(String videoUrl, String shortDescription, String description) {
        this.videoUrl = videoUrl;
        this.shortDescription = shortDescription;
        this.description = description;
    }

    @Bindable
    public String getVideoUrl() {
        return videoUrl;
    }

    @Bindable
    public String getShortDescription() {
        return shortDescription;
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    @BindingAdapter({"bind:videoUrl"})
    public static void loadVideoThumbnail(ImageView imageView, String videoUrl) {
        Glide.with(imageView.getContext()).load(videoUrl).into(imageView);
    }
}
