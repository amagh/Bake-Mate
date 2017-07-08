package com.amagh.bakemate.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by hnoct on 6/27/2017.
 */

@Database(version = RecipeDatabase.VERSION,
        fileName = RecipeDatabase.DATABASE_NAME)
public class RecipeDatabase {
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "recipes.db";

    @Table(RecipeContract.RecipeEntry.class)
    public static final String RECIPES = "recipes";

    @Table(RecipeContract.IngredientEntry.class)
    public static final String INGREDIENTS = "ingredients";

    @Table(RecipeContract.StepEntry.class)
    public static final String STEPS = "steps";
}
