package com.amagh.bakemate.adapters;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hnoct on 6/28/2017.
 */

public class DetailsAdapter extends RecyclerView.Adapter {
    // **Constants** //
    private static final int RECIPE_DETAILS_VIEW = 0;
    private static final int INGREDIENTS_VIEW = 1;
    private static final int STEPS_VIEW = 2;

    // **Member Variables** //
    private Cursor mRecipeCursor;
    private Cursor mIngredientsCursor;
    private Cursor mStepsCursor;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        // Only return the total Cursor count if all three Cursors are valid
        if (!cursorsAreValid()) return 0;

        return mRecipeCursor.getCount() + mIngredientsCursor.getCount() + mStepsCursor.getCount();
    }

    /**
     * Determines whether all necessary Cursors have been swapped into the Adapter
     * @return
     */
    private boolean cursorsAreValid() {
        return mRecipeCursor != null && mIngredientsCursor != null && mStepsCursor != null;
    }

    public void swapRecipeCursor(Cursor newRecipeCursor) {
        mRecipeCursor = newRecipeCursor;

        if (cursorsAreValid()) {
            notifyDataSetChanged();
        }
    }

    public void swapIngredientsCursor(Cursor newIngredientsCursor) {
        mIngredientsCursor = newIngredientsCursor;

        if (cursorsAreValid()) {
            notifyDataSetChanged();
        }
    }

    public void swapStepsCursor(Cursor newStepsCursor) {
        mStepsCursor = newStepsCursor;

        if (cursorsAreValid()) {
            notifyDataSetChanged();
        }
    }

    public class DetailsViewHolder extends RecyclerView.ViewHolder {
        // **Member Variables** //
        private ViewDataBinding mBinding;

        public DetailsViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

            switch (getItemViewType()) {

            }
        }
    }
}
