package com.amagh.bakemate.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.amagh.bakemate.R;
import com.amagh.bakemate.data.RecipeContract;
import com.amagh.bakemate.models.Step;
import com.amagh.bakemate.ui.StepDetailsActivity;
import com.amagh.bakemate.ui.StepDetailsFragment;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by hnoct on 6/29/2017.
 */

public class StepSectionAdapter extends FragmentStatePagerAdapter implements StepDetailsActivity.PageChangeListener {
    // **Constants** //
    private static final String TAG = StepSectionAdapter.class.getSimpleName();
    public interface StepProjection {
        String[] STEP_PROJECTION = {
                RecipeContract.StepEntry.COLUMN_SHORT_DESC,
                RecipeContract.StepEntry.COLUMN_DESCRIPTION,
                RecipeContract.StepEntry.COLUMN_VIDEO_URL
        };

        int IDX_STEP_SHORT_DESC = 0;
        int IDX_STEP_DESCRIPTION = 1;
        int IDX_STEP_VIDEO_URL = 2;
    }

    // **Member Variables** //
    private Cursor mCursor;
    private Step[] mSteps;
    private ExtractorMediaSource[] mMediaSources;
    private Context mContext;
    private int mCurrentPage;

    public StepSectionAdapter(StepDetailsActivity activity, FragmentManager fm) {
        super(fm);

        mContext = activity;
        activity.setPageChangeCallBack(this);
    }

    @Override
    public Fragment getItem(int position) {
        if (mSteps[position] == null) {
            // Move Cursor to correct position
            mCursor.moveToPosition(position);

            // Create a Step to pass to the newInstance method
            Step step = Step.createStepFromCursor(mCursor);
            step.setStepId(position);

            mSteps[position] = step;

            // Prepare the MediaSource for the Step
            prepareMediaSources(position);
        }

        return StepDetailsFragment.newInstance(mSteps[position], position);
    }

    @Override
    public int getCount() {
        if (mCursor != null) return mCursor.getCount();

        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Move Cursor to correct position
        mCursor.moveToPosition(position);

        // Add 1 to the position to get the step number
        int step = position + 1;

        // Return formatted String
        return mContext.getString(R.string.step, step);
    }

    public void swapCursor(Cursor newCursor) {
        // Set member variable to newCursor
        mCursor = newCursor;

        if (mCursor != null) {
            // Initialize new Array for Steps
            mSteps = new Step[mCursor.getCount()];
            mMediaSources = new ExtractorMediaSource[mCursor.getCount()];

            notifyDataSetChanged();
        }
    }

    /**
     * Helper method for instantiating the MediaSource for a given Step and stores it in the member
     * variable Array
     *
     * @param position The position of the Step to create a MediaSource for
     */
    private void prepareMediaSources(int position) {
        // Retrieve the URL of the video to load into the MediaSource
        String videoUrl = mSteps[position].getVideoUrl();

        // If the step has not accompanying video, do nothing
        if (videoUrl == null || videoUrl.isEmpty()) return;

        // Create a MediaSource for the video
        Uri videoUri = Uri.parse(videoUrl);
        String userAgent = Util.getUserAgent(mContext, "BakeMate");

        mMediaSources[position] = new ExtractorMediaSource(
                videoUri,
                new DefaultDataSourceFactory(mContext, userAgent),
                new DefaultExtractorsFactory(),
                null,
                null
        );
    }

    /**
     * Retrieves the MediaSource for a Step
     *
     * @param position The position of the Step in the Adapter to retrieve the MediaSource for
     * @return ExtractorMediaSource for the Step's video
     */
    public ExtractorMediaSource getMediaSource(int position) {
        return mMediaSources[position];
    }

    /**
     * Setter for the member variable. Used to keep track of which video should be stopped when the
     * user changes pages.
     *
     * @param currentPage The page the Adapter is currently displaying.
     */
    public void setCurrentPage(int currentPage) {
        mCurrentPage = currentPage;
    }

    @Override
    public void onPageChanged(int currentPage) {
        // If steps have not been loaded yet, do nothing.
        if (mSteps[currentPage] == null) return;

        // Start the player on the currentPage. Stop the previously playing page.
        mSteps[mCurrentPage].stopPlayer();
        mSteps[currentPage].setPlayer(mContext, mMediaSources[currentPage]);

        // Set member variable to the new currentPage
        mCurrentPage = currentPage;
    }
}
