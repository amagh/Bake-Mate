package com.amagh.bakemate.models;

import android.content.Context;
import android.database.Cursor;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.amagh.bakemate.BR;
import com.amagh.bakemate.data.RecipeContract;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by hnoct on 6/29/2017.
 */

public class Step extends BaseObservable implements Parcelable{
    // **Member Variables** //
    private final String videoUrl;
    private final String shortDescription;
    private final String description;

    private int visibility;
    private SimpleExoPlayer player;
    private long playerPosition;
    private int stepId;

    public Step(String videoUrl, String shortDescription, String description) {
        this.videoUrl = videoUrl;
        this.shortDescription = shortDescription;
        this.description = description;

        // If there is no video thumbnail to load, then ProgressBar should be hidden
        this.visibility = !videoUrl.isEmpty() ? View.VISIBLE : View.GONE;
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
    public SimpleExoPlayer getPlayer() {
        return player;
    }

    @BindingAdapter({"bind:videoUrl", "bind:listener"})
    public static void loadVideoThumbnail(ImageView imageView, String videoUrl, RequestListener<Drawable> listener) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            // Nothing to load
            return;
        }

        Glide.with(imageView.getContext())
                .load(videoUrl)
                .listener(listener)
                .into(imageView);
    }

    @BindingAdapter("bind:player")
    public static void loadVideoIntoPlayer(SimpleExoPlayerView playerView, SimpleExoPlayer player) {
        if (player == null) {
            // No media, nothing to play
            return;
        }

        // Set player to the SimpleExoPlayerView
        playerView.setPlayer(player);

        // Start the media once the Layout has been inflated
        player.setPlayWhenReady(true);
    }

    private void hideProgressBar() {
        // Set visibility to GONE and then notify the Listener of the change in property
        visibility = View.GONE;
        notifyPropertyChanged(BR.visibility);
    }

    public int getStepId() {
        return stepId;
    }

    public void setStepId(int stepId) {
        this.stepId = stepId;
    }

    public void stopPlayer() {
        // Save player's current position so it can be re-set when the user re-enters the Fragment
        playerPosition = player.getCurrentPosition();
        player.stop();
    }

    public void setPlayer(Context context) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            // No media, nothing to play
            return;
        }

        if (player == null) {
            // Init SimpleExoPlayer
            this.player = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
        }

        // Convert String videoUrl to a Uri
        Uri videoUri = Uri.parse(videoUrl);

        // Init MediaSource from the videoUri
        String userAgent = Util.getUserAgent(context, "BakeMate");
        MediaSource mediaSource = new ExtractorMediaSource(
                videoUri,
                new DefaultDataSourceFactory(context, userAgent),
                new DefaultExtractorsFactory(),
                null,
                null);

        // Set mediaSoruce into player
        player.prepare(mediaSource);

        if (playerPosition != 0) {
            // Seek to the previous position if it was saved
            player.seekTo(playerPosition);
        }

        notifyPropertyChanged(BR.player);
    }

    /**
     * Helper method for creating a Step from Cursor contents
     *
     * @param cursor    Cursor describing the recipe step. Should already be in the correct position
     * @return A Step Object created with information from the Cursor
     */
    public static Step createStepFromCursor(Cursor cursor) {
        // Retrieve the index of the columns required to make a Step. Retrieving the information
        // dynamically will remove the requirement of passing a projection and column index through
        int IDX_SHORT_DESC = cursor.getColumnIndex(RecipeContract.StepEntry.COLUMN_SHORT_DESC);
        int IDX_DESCRIPTION = cursor.getColumnIndex(RecipeContract.StepEntry.COLUMN_DESCRIPTION);
        int IDX_VIDEO_URL = cursor.getColumnIndex(RecipeContract.StepEntry.COLUMN_VIDEO_URL);

        // Retrieve info from database
        String shortDescription = cursor.getString(IDX_SHORT_DESC);
        String description = cursor.getString(IDX_DESCRIPTION);
        String videoUrl = cursor.getString(IDX_VIDEO_URL);

        return new Step(videoUrl, shortDescription, description);
    }

    // Parcelable Related Methods

    public Step(Parcel parcel) {
        this.videoUrl = parcel.readString();
        this.shortDescription = parcel.readString();
        this.description = parcel.readString();
    }

    public static final Parcelable.Creator<Step> CREATOR = new Parcelable.Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel parcel) {
            return new Step(parcel);
        }

        @Override
        public Step[] newArray(int i) {
            return new Step[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(shortDescription);
        parcel.writeString(description);
        parcel.writeString(videoUrl);
    }

}
