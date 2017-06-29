package com.amagh.bakemate.models;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.amagh.bakemate.R;

/**
 * Created by hnoct on 6/29/2017.
 */

public class Recipe extends BaseObservable {
    private String name;
    private int servings;
    private Context mContext;

    public Recipe(Context context, String name, int servings) {
        this.name = name;
        this.servings = servings;
        mContext = context;
    }

    @Bindable
    public String getName() {
        return name;
    }

    @Bindable
    public String getServings() {
        return mContext.getString(R.string.list_details_servings_format, servings);
    }
}
