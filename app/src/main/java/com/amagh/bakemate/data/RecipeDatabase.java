package com.amagh.bakemate.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by hnoct on 6/27/2017.
 */

@Database(version = RecipeDatabase.VERSION)
public class RecipeDatabase {
    public static final int VERSION = 1;

    @Table(RecipeContract.RecipeEntry.class)
    public static final String RECIPES = "recipes";
}
