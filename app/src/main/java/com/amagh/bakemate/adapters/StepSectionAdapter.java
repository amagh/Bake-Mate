package com.amagh.bakemate.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.amagh.bakemate.R;
import com.amagh.bakemate.data.RecipeContract;
import com.amagh.bakemate.models.Step;
import com.amagh.bakemate.ui.StepDetailsActivity;
import com.amagh.bakemate.ui.StepDetailsFragment;

/**
 * Created by hnoct on 6/29/2017.
 */

public class StepSectionAdapter extends FragmentStatePagerAdapter {
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
    private final Context mContext;
    private Cursor mCursor;

    public StepSectionAdapter(Context context, FragmentManager fm) {
        super(fm);

        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // Move Cursor to correct position
        mCursor.moveToPosition(position);

        // Create a Step to pass to the newInstance method
        Step step = Step.createStepFromCursor(mCursor);
        step.setStepId(position);
        step.setPlayer(mContext);

        if (StepDetailsActivity.sCurrentPosition == position) {
            step.getPlayer().setPlayWhenReady(true);
        }

        return StepDetailsFragment.newInstance(step, position);
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
        mCursor = newCursor;

        if (mCursor != null) notifyDataSetChanged();
    }
}
