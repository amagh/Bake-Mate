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
import android.view.View;
import android.widget.ImageView;

import com.amagh.bakemate.BR;
import com.amagh.bakemate.R;
import com.amagh.bakemate.data.RecipeContract;
import com.amagh.bakemate.glide.RecipeGlideSignature;
import com.amagh.bakemate.ui.StepDetailsActivity;
import com.amagh.bakemate.glide.GlideApp;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

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
    private ExtractorMediaSource mediaSource;
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

    @Bindable
    public ExtractorMediaSource getMediaSource( ) {
        return mediaSource;
    }

    @BindingAdapter({"bind:videoUrl", "bind:listener"})
    public static void loadVideoThumbnail(ImageView imageView, String videoUrl, RequestListener<Drawable> listener) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            // Nothing to load
            return;
        }

        GlideApp.with(imageView.getContext())
                .load(videoUrl)
                .signature(new RecipeGlideSignature(imageView.getContext().getResources().getInteger(R.integer.glide_current_version)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(listener)
                .into(imageView);
    }

    @BindingAdapter({"bind:player", "bind:mediaSource"})
    public static void loadVideoIntoPlayer(SimpleExoPlayerView playerView, SimpleExoPlayer player, ExtractorMediaSource mediaSource) {
        if (player == null) {
            // No media, nothing to play
            return;
        }

        // Load the MediaSource into the SimpleExoPlayer
        player.prepare(mediaSource);

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

    /**
     * Stops video playback and prevents the ExoVideoPlayerView from attempting to load the video
     */
    public void stopPlayer() {
        // Save player's current position so it can be re-set when the user re-enters the Fragment
        playerPosition = player.getCurrentPosition();

        // Stop the video playback
        player.stop();
        player.prepare(null);

        // Set member variables to null so they can't be loaded by loadVideoIntoPlayer()
        player = null;
        mediaSource = null;

        // Notify of property change
        notifyPropertyChanged(BR.mediaSource);
    }

    /**
     * Retrieves the SimpleExoVideoPlayer instance held by the Activity
     *
     * @param context       Activity with getPlayer() method
     * @param mediaSource   The MediaSource that will be prepared by the player for video playback
     */
    public void setPlayer(Context context, ExtractorMediaSource mediaSource) {
        // Retrieve the SimpleExoPlayer from the Activity
        this.player = ((StepDetailsActivity) context).getPlayer();

        // Set member variable to parameter
        this.mediaSource = mediaSource;

        // Notify of property change
        notifyPropertyChanged(BR.mediaSource);
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
