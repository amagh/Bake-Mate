package com.amagh.bakemate.adapters;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amagh.bakemate.R;
import com.amagh.bakemate.data.RecipeContract;
import com.amagh.bakemate.databinding.ListItemRecipeBinding;
import com.amagh.bakemate.utils.DatabaseUtils;
import com.bumptech.glide.Glide;

/**
 * Created by hnoct on 6/28/2017.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    // **Constants** //
    private static final String TAG = RecipeAdapter.class.getSimpleName();

    public interface Projection {
        String[] RECIPE_PROJECTION = {
                RecipeContract.RecipeEntry.COLUMN_RECIPE_ID,
                RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME
        };

        int COLUMN_RECIPE_ID        = 0;
        int COLUMN_RECIPE_NAME      = 1;
    }

    // **Member Variables** //
    private Cursor mCursor;

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Use DataBinding to inflate the layout
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemRecipeBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.list_item_recipe,
                parent,
                false
        );

        return new RecipeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) return mCursor.getCount();

        return 0;
    }

    public void swapCursor(Cursor newCursor) {
        // Set member variable to the new Cursor
        mCursor = newCursor;

        if (mCursor != null) {
            // If new Cursor is valid, refresh Views
            notifyDataSetChanged();
        }
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        // **Member Variables** //
        ListItemRecipeBinding mBinding;

        public RecipeViewHolder(ListItemRecipeBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bindData(int position) {
            // Move Cursor to correct position
            mCursor.moveToPosition(position);

            // Retrieve recipe information from Cursor
            int recipeId = mCursor.getInt(Projection.COLUMN_RECIPE_ID);
            String recipe = mCursor.getString(Projection.COLUMN_RECIPE_NAME);

            // Get videoUrl to utilize a still frame as the image since no recipes contain a
            // thumbnail
            String videoUrl = DatabaseUtils.getVideoUrlForThumbnail(itemView.getContext(), recipeId);

            // Populate Views
            Glide.with(itemView.getContext())
                    .load(videoUrl)
                    .into(mBinding.listRecipeIv);

            mBinding.listRecipeNameTv.setText(recipe);

            // Force bindings to execute immediately
            mBinding.executePendingBindings();
        }
    }
}
