package com.amagh.bakemate.models;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.amagh.bakemate.utils.FormattingUtils;

/**
 * ViewModel for the Ingredient List Item
 */

public class Ingredient extends BaseObservable {
    // **Member Variables** //
    private final Context mContext;
    private final double quantity;
    private final String measure;
    private final String ingredient;

    public Ingredient(Context context, double quantity, String measure, String ingredient) {
        mContext = context;
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    @Bindable
    public String getQuantity() {
        return FormattingUtils.formatQuantityAndMeasurement(mContext, this.quantity, this.measure);
    }

    @Bindable
    public String getIngredient() {
        return ingredient;
    }


}
