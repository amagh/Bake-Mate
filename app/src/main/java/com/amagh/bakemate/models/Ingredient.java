package com.amagh.bakemate.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * ViewModel for the Ingredient List Item
 */

public class Ingredient extends BaseObservable {
    private int quantity;
    private String measure;
    private String ingredient;

    public Ingredient(int quantity, String measure, String ingredient) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    @Bindable
    public String getQuantity() {
        return Integer.toString(quantity);
    }

    @Bindable
    public String getMeasure() {
        return measure;
    }

    @Bindable
    public String getIngredient() {
        return ingredient;
    }
}
