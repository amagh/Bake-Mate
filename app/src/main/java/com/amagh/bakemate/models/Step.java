package com.amagh.bakemate.models;

import android.content.Context;
import android.database.Cursor;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.amagh.bakemate.BR;
import com.amagh.bakemate.R;
import com.amagh.bakemate.data.RecipeContract;
import com.amagh.bakemate.glide.GlideApp;
import com.amagh.bakemate.glide.RecipeGlideSignature;
import com.amagh.bakemate.utils.ManageSimpleExoPlayerInterface;
import com.amagh.bakemate.utils.idling.SimpleIdlingResource;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

/**
 * Created by hnoct on 6/29/2017.
 */

@SuppressWarnings("WeakerAccess")
public class Step extends BaseObservable implements Parcelable{
    // **Member Variables** //
    private final String videoUrl;
    private final String thumbnailUrl;
    private final String shortDescription;
    private final String description;

    private int imageVisibility;
    private int playIconVisibility;
    private SimpleExoPlayer player;
    private ExtractorMediaSource mediaSource;
    private long playerPosition;
    private int stepId;
    private int playIcon;

    private SimpleIdlingResource idlingResource;

    public Step(String videoUrl, String thumbnailUrl, String shortDescription, String description) {
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.shortDescription = shortDescription;
        this.description = description;

        // If there is no video thumbnail to load, then ProgressBar should be hidden
        this.imageVisibility = !thumbnailUrl.isEmpty() ? View.VISIBLE : View.INVISIBLE;
        this.playIconVisibility = !videoUrl.isEmpty() ? View.VISIBLE : View.GONE;
        this.playIcon = !thumbnailUrl.isEmpty() ? R.drawable.ic_play_arrow : R.drawable.ic_play_circle_filled;
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
    public int getImageVisibility() {
        return imageVisibility;
    }

    @Bindable
    public int getPlayIcon() {
        return playIcon;
    }

    @Bindable
    public int getPlayIconVisibility() {
        return playIconVisibility;
    }

    @Bindable
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    @Bindable
    public long getPlayerPosition() {
        return playerPosition;
    }

    @Bindable
    public SimpleExoPlayer getPlayer() {
        return player;
    }

    @Bindable
    public ExtractorMediaSource getMediaSource( ) {
        return mediaSource;
    }

    @Bindable
    public SimpleIdlingResource getIdlingResource() {
        return idlingResource;
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

    @BindingAdapter({"bind:player", "bind:mediaSource", "bind:playerPosition", "bind:idlingResource"})
    public static void loadVideoIntoPlayer(final SimpleExoPlayerView playerView, final SimpleExoPlayer player,
                                           ExtractorMediaSource mediaSource, long playerPosition, final SimpleIdlingResource idlingResource) {
        if (player == null || mediaSource == null) {
            // No media, nothing to play
            return;
        }

        // Load the MediaSource into the SimpleExoPlayer
        player.prepare(mediaSource);

        player.seekTo(playerPosition);

        // Set player to the SimpleExoPlayerView
        playerView.setPlayer(player);

        if (idlingResource != null) {
            player.addListener(new ExoPlayer.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    idlingResource.setIdleState(true);
                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {

                }

                @Override
                public void onPositionDiscontinuity() {

                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                }
            });
        }

        // Start the media once the Layout has been inflated
        player.setPlayWhenReady(true);
    }

    @BindingAdapter({"bind:thumbnailUrl", "bind:listener"})
    public static void loadThumbnailUrl(ImageView imageView, String thumbnailUrl, RequestListener listener) {
        if (thumbnailUrl.isEmpty()) {
            return;
        }

        GlideApp.with(imageView.getContext())
                .load(thumbnailUrl)
                .listener(listener)
                .signature(new RecipeGlideSignature(imageView.getContext().getResources().getInteger(R.integer.glide_current_version)))
                .into(imageView);
    }

    @BindingAdapter("bind:playIcon")
    public static void loadPlayIcon(ImageView imageView, int playIcon) {
        imageView.setImageResource(playIcon);
    }

    public void hideProgressBar() {
        this.imageVisibility = View.GONE;

        notifyPropertyChanged(BR.imageVisibility);
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
        savePlayerPosition();

        // Stop the video playback
        if (player != null) {
            player.stop();
            player.prepare(null);
        }

        // Set member variables to null so they can't be loaded by loadVideoIntoPlayer()
        player = null;
        mediaSource = null;

        // Notify of property change
        notifyPropertyChanged(BR.mediaSource);
    }

    /**
     * Saves the player position as a member variable
     */
    public void savePlayerPosition() {
        if (this.player != null && this.player.getCurrentPosition() != 0) {
            this.playerPosition = player.getCurrentPosition();
        }
    }

    /**
     * Retrieves the SimpleExoVideoPlayer instance held by the Activity
     *
     * @param context       Activity with getPlayer() method
     * @param mediaSource   The MediaSource that will be prepared by the player for video playback
     */
    public void setPlayer(Context context, @Nullable ExtractorMediaSource mediaSource) {
        // Retrieve the SimpleExoPlayer from the Activity
        if (context instanceof ManageSimpleExoPlayerInterface) {
            this.player = ((ManageSimpleExoPlayerInterface) context).getPlayer();
        }

        // Set member variable to parameter if it has not already been loaded
        this.mediaSource = mediaSource;

        if (this.mediaSource != null) {
            // Notify of property change
            notifyPropertyChanged(BR.mediaSource);

            imageVisibility = View.VISIBLE;
            notifyPropertyChanged(BR.playIconVisibility);
        } else {
            // Another check to ensure the VideoPlayerView does not show
            imageVisibility = View.GONE;
            notifyPropertyChanged(BR.playIconVisibility);
        }
    }

    public void setIdlingResource(SimpleIdlingResource idlingResource) {
        this.idlingResource = idlingResource;
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
        int IDX_THUMBNAIL_URL = cursor.getColumnIndex(RecipeContract.StepEntry.COLUMN_THUMBNAIL_URL);

        // Retrieve info from database
        String shortDescription = cursor.getString(IDX_SHORT_DESC);
        String description = cursor.getString(IDX_DESCRIPTION);
        String videoUrl = cursor.getString(IDX_VIDEO_URL);
        String thumbnailUrl = cursor.getString(IDX_THUMBNAIL_URL);

        return new Step(videoUrl, thumbnailUrl, shortDescription, description);
    }

    // **Parcelable Related Methods** //

    public Step(Parcel parcel) {
        this.shortDescription = parcel.readString();
        this.description = parcel.readString();
        this.videoUrl = parcel.readString();
        this.thumbnailUrl = parcel.readString();

        this.playerPosition = parcel.readLong();
        this.stepId = parcel.readInt();
    }

    public static final Parcelable.Creator<Step> CREATOR = new Parcelable.Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel parcel) {
            return new Step(parcel);
        }

        @Override
        public Step[] newArray(int i) {
            return new Step[i];
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
        parcel.writeString(thumbnailUrl);

        parcel.writeLong(playerPosition);
        parcel.writeInt(stepId);
    }

}
