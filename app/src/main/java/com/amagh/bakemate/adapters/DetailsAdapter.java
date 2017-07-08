package com.amagh.bakemate.adapters;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amagh.bakemate.R;
import com.amagh.bakemate.data.RecipeContract;
import com.amagh.bakemate.databinding.ListItemIngredientBinding;
import com.amagh.bakemate.databinding.ListItemRecipeDetailsBinding;
import com.amagh.bakemate.databinding.ListItemStepBinding;
import com.amagh.bakemate.models.Ingredient;
import com.amagh.bakemate.models.Recipe;
import com.amagh.bakemate.models.Step;
import com.amagh.bakemate.utils.LayoutUtils;

/**
 * Created by hnoct on 6/28/2017.
 */

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder> {

    // ViewTypes
    private static final int RECIPE_DETAILS_VIEW        = 0;
    private static final int INGREDIENTS_VIEW           = 1;
    private static final int STEPS_VIEW                 = 2;
    private static final int INGREDIENTS_HEADER_VIEW    = 3;
    private static final int STEPS_HEADER_VIEW          = 4;

    // Column Projections
    public interface RecipeProjection {
        String[] RECIPE_DETAILS_PROJECTION = {
                RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME,
                RecipeContract.RecipeEntry.COLUMN_RECIPE_SERVINGS
        };

        int IDX_RECIPE_NAME             = 0;
        int IDX_RECIPE_SERVINGS         = 1;
    }

    public interface IngredientProjection {
        String[] INGREDIENT_PROJECTION = {
                RecipeContract.IngredientEntry.COLUMN_QUANTITY,
                RecipeContract.IngredientEntry.COLUMN_MEASURE,
                RecipeContract.IngredientEntry.COLUMN_INGREDIENT
        };

        int IDX_INGREDIENT_QUANTITY     = 0;
        int IDX_INGREDIENT_MEASURE      = 1;
        int IDX_INGREDIENT_NAME         = 2;
    }

    public interface StepProjection {
        String[] STEP_PROJECTION = {
                RecipeContract.StepEntry.COLUMN_STEP_ID,
                RecipeContract.StepEntry.COLUMN_SHORT_DESC,
                RecipeContract.StepEntry.COLUMN_DESCRIPTION,
                RecipeContract.StepEntry.COLUMN_VIDEO_URL
        };

        int IDX_STEP_ID                 = 0;
        int IDX_STEP_SHORT_DESC         = 1;
        int IDX_STEP_DESCRIPTION        = 2;
        int IDX_STEP_VIDEO_URL          = 3;
    }

    // **Member Variables** //
    private Cursor mRecipeCursor;
    private Cursor mIngredientsCursor;
    private Cursor mStepsCursor;
    private int mSelectedItem;

    private final ClickHandler mClickHandler;

    public DetailsAdapter(ClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public DetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Init the layoutId variable for the layout to inflate for the View
        int layoutId;

        switch (viewType) {
            case RECIPE_DETAILS_VIEW:
                layoutId = R.layout.list_item_recipe_details;
                break;
            case INGREDIENTS_HEADER_VIEW:
                layoutId = R.layout.list_item_ingredient_header;
                break;
            case INGREDIENTS_VIEW:
                layoutId = R.layout.list_item_ingredient;
                break;
            case STEPS_HEADER_VIEW:
                layoutId = R.layout.list_item_step_header;
                break;
            case STEPS_VIEW:
                layoutId = R.layout.list_item_step;
                break;
            default: throw new UnsupportedOperationException("Unknown ViewType: " + viewType);
        }
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType < INGREDIENTS_HEADER_VIEW) {
            // Create a ViewDataBinding using the layoutId
            ViewDataBinding binding = DataBindingUtil.inflate(inflater, layoutId, parent, false);
            return new DetailsViewHolder(binding, viewType);
        } else {
            // Inflate the View using the layoutId
            View view = inflater.inflate(layoutId, parent, false);
            return new DetailsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(DetailsViewHolder holder, int position) {
        int viewType = holder.getItemViewType();

        if (viewType != INGREDIENTS_HEADER_VIEW && viewType != STEPS_HEADER_VIEW) {
            holder.bindData(position);
        }
    }

    @Override
    public int getItemCount() {
        // Only return the total Cursor count if all three Cursors are valid
        if (!cursorsAreValid()) return 0;

        // Return the total count of all Cursors and add 2 for the headers
        return mRecipeCursor.getCount() + mIngredientsCursor.getCount() + mStepsCursor.getCount() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return RECIPE_DETAILS_VIEW;
        }
        // Ingredient header View is always the view right after the recipe details
        if (position == 1) {
            return INGREDIENTS_HEADER_VIEW;
        }
        if (position < mIngredientsCursor.getCount() + 2) {
            return INGREDIENTS_VIEW;
        }
        // Steps header View is always right after the list of ingredients
        if (position == mIngredientsCursor.getCount() + 2) {
            return STEPS_HEADER_VIEW;
        }
        if (position > mIngredientsCursor.getCount() + 2) {
            return STEPS_VIEW;
        }

        return super.getItemViewType(position);
    }

    /**
     * Determines whether all necessary Cursors have been swapped into the Adapter
     *
     * @return True if all Cursors have been loaded. False otherwise
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

    /**
     * Sets the step item to be selected to control its background to indicate its state
     *
     * @param stepId ID of the Step to set to be activated
     */
    public void setSelected(long stepId) {
        // Set the mem var to the parameter
        mSelectedItem = (int) stepId;

        // Refresh the item to the background shows
        notifyItemChanged(mSelectedItem + 3 + mIngredientsCursor.getCount());
    }

    public interface ClickHandler {
        void onStepClicked(long stepId);
    }

    public class DetailsViewHolder extends RecyclerView.ViewHolder {
        // **Member Variables** //
        private ViewDataBinding mBinding;

        public DetailsViewHolder(ViewDataBinding binding, int viewType) {
            super(binding.getRoot());

            // Only set an OnClickListener for ViewHolders with step-information
            if (viewType == STEPS_VIEW) {
                binding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Compensate for headers and ingredients
                        int position = getAdapterPosition() - 3 - mIngredientsCursor.getCount();

                        // Get the ID of the selected step
                        mStepsCursor.moveToPosition(position);
                        long stepId = mStepsCursor.getLong(StepProjection.IDX_STEP_ID);

                        // Pass the ID to the ClickHandler
                        mClickHandler.onStepClicked(stepId);

                        // Get the position of the previously selected item so it can be de-activated
                        int previousItem = mSelectedItem + 3 + mIngredientsCursor.getCount();

                        // Get the position of the currently activated item
                        mSelectedItem = position;

                        // Refresh the background of the items to be de-activated/activated
                        notifyItemChanged(previousItem);
                        notifyItemChanged(getAdapterPosition());
                    }
                });
            }

            mBinding = binding;
        }

        public DetailsViewHolder(View itemView) {
            super(itemView);
        }

        public void bindData(int position) {
            switch (getItemViewType()) {
                // Retrieve data from the correct Cursor, create a new ViewModel Object, and bind
                // the data to the View
                case RECIPE_DETAILS_VIEW: {
                    mRecipeCursor.moveToPosition(position);

                    String recipeName = mRecipeCursor.getString(RecipeProjection.IDX_RECIPE_NAME);
                    int servings = mRecipeCursor.getInt(RecipeProjection.IDX_RECIPE_SERVINGS);

                    Recipe recipe = new Recipe(mBinding.getRoot().getContext(), recipeName, servings);

                    ((ListItemRecipeDetailsBinding) mBinding).setDetails(recipe);
                    break;
                }

                case INGREDIENTS_VIEW: {
                    // Subtract 2 from the position to compensate for the positions of the recipe
                    // details and the ingredient header
                    position = position - 2;
                    mIngredientsCursor.moveToPosition(position);

                    double quantity = mIngredientsCursor.getDouble(IngredientProjection.IDX_INGREDIENT_QUANTITY);
                    String measure = mIngredientsCursor.getString(IngredientProjection.IDX_INGREDIENT_MEASURE);
                    String ingredientName = mIngredientsCursor.getString(IngredientProjection.IDX_INGREDIENT_NAME);

                    Ingredient ingredient = new Ingredient(mBinding.getRoot().getContext(), quantity, measure, ingredientName);

                    ((ListItemIngredientBinding) mBinding).setIngredient(ingredient);
                    break;
                }

                case STEPS_VIEW: {
                    // Subtract 3 from the position to compensate for the positions of the recipe
                    // details and the two headers. Then subtract out the number of ingredients to
                    // compensate for ingredients
                    position = position - 3 - mIngredientsCursor.getCount();
                    mStepsCursor.moveToPosition(position);

                    String shortDescription = mStepsCursor.getString(StepProjection.IDX_STEP_SHORT_DESC);
                    String description = mStepsCursor.getString(StepProjection.IDX_STEP_DESCRIPTION);
                    String videoUrl = mStepsCursor.getString(StepProjection.IDX_STEP_VIDEO_URL);

                    Step step = new Step(videoUrl, shortDescription, description);

                    ((ListItemStepBinding) mBinding).setStep(step);

                    // Set selected status of the item
                    if (LayoutUtils.inTwoPane(mBinding.getRoot().getContext())) {
                        mBinding.getRoot().setSelected(position == mSelectedItem);
                    }

                    break;
                }
            }

            mBinding.executePendingBindings();
        }
    }
}
