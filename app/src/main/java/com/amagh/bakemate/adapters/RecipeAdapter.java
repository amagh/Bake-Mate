package com.amagh.bakemate.adapters;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amagh.bakemate.R;
import com.amagh.bakemate.data.RecipeContract;
import com.amagh.bakemate.databinding.ListItemRecipeBinding;
import com.amagh.bakemate.models.Recipe;
import com.amagh.bakemate.utils.DatabaseUtils;
import com.bumptech.glide.Glide;

/**
 * Created by hnoct on 6/28/2017.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    // **Constants** //
    private static final String TAG = RecipeAdapter.class.getSimpleName();

    // **Member Variables** //
    private ClickHandler mClickHandler;

    public RecipeAdapter(ClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public interface Projection {
        String[] RECIPE_PROJECTION = {
                RecipeContract.RecipeEntry.COLUMN_RECIPE_ID,
                RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME
        };

        int IDX_RECIPE_ID       = 0;
        int IDX_RECIPE_NAME     = 1;
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
        // Return Cursor count if Cursor is valid
        if (mCursor != null) return mCursor.getCount();

        return 0;
    }

    @Override
    public long getItemId(int position) {
        // Return the recipeId
        mCursor.moveToPosition(position);

        return mCursor.getInt(Projection.IDX_RECIPE_ID);
    }

    public void swapCursor(Cursor newCursor) {
        // Set member variable to the new Cursor
        mCursor = newCursor;

        if (mCursor != null) {
            // If new Cursor is valid, refresh Views
            notifyDataSetChanged();
        }
    }

    public interface ClickHandler {
        void onRecipeClicked(int recipeId);
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // **Member Variables** //
        ListItemRecipeBinding mBinding;

        public RecipeViewHolder(ListItemRecipeBinding binding) {
            super(binding.getRoot());

            mBinding = binding;

            // Set the OnClickListener
            View itemView = mBinding.getRoot();
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Retrieve the recipeId of the ViewHolder that was clicked
            int position = getAdapterPosition();
            mCursor.moveToPosition(position);

            int recipeId = mCursor.getInt(Projection.IDX_RECIPE_ID);

            // Pass the click event and recipeId to mClickHandler
            mClickHandler.onRecipeClicked(recipeId);
        }

        public void bindData(int position) {
            // Move Cursor to correct position
            mCursor.moveToPosition(position);

            // Retrieve recipe information from Cursor
            int recipeId = mCursor.getInt(Projection.IDX_RECIPE_ID);
            String recipeName = mCursor.getString(Projection.IDX_RECIPE_NAME);

            // Get videoUrl to utilize a still frame as the image since no recipes contain a
            // thumbnail
            String videoUrl = DatabaseUtils.getVideoUrlForThumbnail(itemView.getContext(), recipeId);

            Recipe recipe = new Recipe(recipeName, videoUrl);
            mBinding.setRecipe(recipe);

            // Force bindings to execute immediately
            mBinding.executePendingBindings();
        }
    }
}
