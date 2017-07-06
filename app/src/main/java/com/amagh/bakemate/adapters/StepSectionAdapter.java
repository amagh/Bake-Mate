package com.amagh.bakemate.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.amagh.bakemate.R;
import com.amagh.bakemate.data.RecipeContract;
import com.amagh.bakemate.models.Step;
import com.amagh.bakemate.ui.MediaSourceActivity;
import com.amagh.bakemate.ui.StepDetailsActivity;
import com.amagh.bakemate.ui.StepDetailsFragment;
import com.google.android.exoplayer2.source.ExtractorMediaSource;

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
    private SparseArray<Step> mStepsArray;
    private SparseArray<ExtractorMediaSource> mMediaSourceArray;
    private Context mContext;
    private int mCurrentPage;

    public StepSectionAdapter(StepDetailsActivity activity, FragmentManager fm) {
        super(fm);

        mContext = activity;
        activity.setPageChangeCallBack(this);

        // Init the SparseArray
        mStepsArray = new SparseArray<>();
        mMediaSourceArray = new SparseArray<>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);

        // Check if the Step within the SparseArray is the Step being utilized by the Fragment
        if (object instanceof StepDetailsFragment) {
            if (mStepsArray.get(position) == null ||
                    mStepsArray.get(position).equals(((StepDetailsFragment) object).getStep())) {
                // Does not match, replace the Step in the SparseArray with the Step being utilized
                // by the Fragment
                mStepsArray.put(position, ((StepDetailsFragment) object).getStep());

                prepareMediaSources(position);
            }
        }

        return object;
    }

    @Override
    public Fragment getItem(int position) {
        if (mStepsArray.get(position) == null) {
            // Move Cursor to correct position
            mCursor.moveToPosition(position);

            // Create a Step to pass to the newInstance method
            Step step = Step.createStepFromCursor(mCursor);
            mStepsArray.get(position).setStepId(position);

            mStepsArray.put(position, step);

            // Prepare the MediaSource for the Step
            prepareMediaSources(position);
        }

        return StepDetailsFragment.newInstance(mStepsArray.get(position), position);
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
            return mContext.getString(R.string.step_intro);
        }

        // Move Cursor to correct position
        mCursor.moveToPosition(position);

        // Return formatted String
        return mContext.getString(R.string.step, position);
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
                ((MediaSourceActivity) mContext).getMediaSource(mStepsArray.get(position)));
    }

    /**
     * Retrieves the MediaSource for a Step
     *
     * @param position The position of the Step in the Adapter to retrieve the MediaSource for
     * @return ExtractorMediaSource for the Step's video
     */
    public ExtractorMediaSource getMediaSource(int position) {
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
        if (mStepsArray.get(currentPage) == null) return;

        // Start the player on the currentPage. Stop the previously playing page.
        mStepsArray.get(mCurrentPage).stopPlayer();
        mStepsArray.get(currentPage).setPlayer(mContext, mMediaSourceArray.get(currentPage));

        // Set member variable to the new currentPage
        mCurrentPage = currentPage;
    }
}
