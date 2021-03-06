package com.amagh.bakemate.adapters;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.amagh.bakemate.R;
import com.amagh.bakemate.data.RecipeContract;
import com.amagh.bakemate.models.Step;
import com.amagh.bakemate.ui.StepDetailsActivity;
import com.amagh.bakemate.ui.StepDetailsFragment;
import com.google.android.exoplayer2.source.ExtractorMediaSource;

/**
 * Created by hnoct on 6/29/2017.
 */

public class StepSectionAdapter extends FragmentStatePagerAdapter implements StepDetailsActivity.PageChangeListener {
    public interface StepProjection {
        String[] STEP_PROJECTION = {
                RecipeContract.StepEntry.COLUMN_SHORT_DESC,
                RecipeContract.StepEntry.COLUMN_DESCRIPTION,
                RecipeContract.StepEntry.COLUMN_VIDEO_URL,
                RecipeContract.StepEntry.COLUMN_THUMBNAIL_URL
        };

        int IDX_STEP_SHORT_DESC     = 0;
        int IDX_STEP_DESCRIPTION    = 1;
        int IDX_STEP_VIDEO_URL      = 2;
        int IDX_STEP_THUMBNAIL_URL  = 3;
    }

    // **Member Variables** //
    private Cursor mCursor;
    private final SparseArray<ExtractorMediaSource> mMediaSourceArray;
    private final StepDetailsActivity mActivity;
    private int mCurrentPage;

    public StepSectionAdapter(StepDetailsActivity activity, FragmentManager fm) {
        super(fm);

        mActivity = activity;
        activity.setPageChangeCallBack(this);

        // Init the SparseArray
        mMediaSourceArray = new SparseArray<>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);

        // Check if the Step within the SparseArray is the Step being utilized by the Fragment
        if (object instanceof StepDetailsFragment) {
            StepDetailsFragment fragment = (StepDetailsFragment) object;
            if (mActivity.getSteps()[position] == null ||
                    (fragment.getStep() != null && mActivity.getSteps()[position] != fragment.getStep())) {
                // Does not match, replace the Step in the Array with the Step being utilized by
                // the Fragment
                mActivity.getSteps()[position] = ((StepDetailsFragment) object).getStep();
                mActivity.getSteps()[position].setStepId(position);
            }

            if (!mActivity.getSteps()[position].getVideoUrl().isEmpty() &&
                    mMediaSourceArray.get(position) == null) {
                prepareMediaSources(position);
            }
        }

        return object;
    }

    @Override
    public Fragment getItem(int position) {

        if (mActivity.getSteps()[position] == null) {
            // Move Cursor to correct position
            mCursor.moveToPosition(position);

            // Create a Step to pass to the newInstance method
            mActivity.getSteps()[position] = Step.createStepFromCursor(mCursor);
            mActivity.getSteps()[position].setStepId(position);

            // Prepare the MediaSource for the Step
            prepareMediaSources(position);
        }

        return StepDetailsFragment.newInstance(mActivity.getSteps()[position]);
    }

    @Override
    public int getCount() {
        if (mCursor != null) return mCursor.getCount();

        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Set the title for the first tab to "Intro"
        if (position == 0) {
            return mActivity.getString(R.string.step_intro);
        }

        // Move Cursor to correct position
        mCursor.moveToPosition(position);

        // Return formatted String
        return mActivity.getString(R.string.step, position);
    }

    public void swapCursor(Cursor newCursor) {
        // Set member variable to newCursor
        mCursor = newCursor;

        if (mCursor != null) {
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
        mMediaSourceArray.put(
                position,
                mActivity.getMediaSource(mActivity.getSteps()[position]));
    }

    /**
     * Retrieves the MediaSource for a Step
     *
     * @param position The position of the Step in the Adapter to retrieve the MediaSource for
     * @return ExtractorMediaSource for the Step's video
     */
    public ExtractorMediaSource getMediaSource(int position) {
        // Prepare the media source if it wasn't properly created before
        if (mMediaSourceArray.get(position) == null &&
                !mActivity.getSteps()[position].getVideoUrl().isEmpty()) {
            prepareMediaSources(position);
        }
        return mMediaSourceArray.get(position);
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
        if (mActivity.getSteps()[currentPage] == null) return;

        // Start the player on the currentPage. Stop the previously playing page.
        mActivity.getSteps()[mCurrentPage].stopPlayer();
        mActivity.getSteps()[currentPage].setPlayer(mActivity, mMediaSourceArray.get(currentPage));

        // Set member variable to the new currentPage
        mCurrentPage = currentPage;
    }
}
