package com.amagh.bakemate.models;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.amagh.bakemate.R;

import java.util.Locale;

/**
 * ViewModel for the Ingredient List Item
 */

public class Ingredient extends BaseObservable {
    // **Member Variables** //
    private Context mContext;
    private double quantity;
    private String measure;
    private String ingredient;

    public Ingredient(Context context, double quantity, String measure, String ingredient) {
        mContext = context;
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    @Bindable
    public String getQuantity() {
        String measure = null;

        // Set the measure to more readable String
        switch (this.measure) {
            case "G":
                measure = mContext.getString(R.string.measurement_gram);
                break;
            case "TBLSP":
                measure = mContext.getString(R.string.measurement_tablespoon);
                break;
            case "TSP":
                measure = mContext.getString(R.string.measurement_teaspoon);
                break;
            case "UNIT":
                measure = "";
                break;
            case "K":
                measure = mContext.getString(R.string.measurement_package);
                break;
            case "CUP":
                measure = mContext.getString(R.string.measurement_cup);
                break;
            case "OZ":
                measure = mContext.getString(R.string.measurement_ounce);
                break;
        }

        // Check if quantity contains a decimal or if it can be boxed to an equivalent int
        if (quantity == (int) quantity) {
            return String.format(Locale.getDefault(), "%d %s", (int) quantity, measure);
        } else {
            return String.format(Locale.getDefault(), "%s %s", convertDecimalToFraction(quantity), measure);
        }
    }

    @Bindable
    public String getIngredient() {
        return ingredient;
    }

    /**
     * Helper method for converting decimals to fractions for easier readability
     * @param quantity
     * @return
     */
    private String convertDecimalToFraction(double quantity) {
        if ((int) quantity > 0) {
            return (int) quantity + "\u00bd";
        } else {
            return "\u00bd";
        }
    }
}
