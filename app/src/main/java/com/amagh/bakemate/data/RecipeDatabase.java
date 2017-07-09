package com.amagh.bakemate.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by hnoct on 6/27/2017.
 */

@SuppressWarnings("WeakerAccess")
@Database(version = RecipeDatabase.VERSION,
        fileName = RecipeDatabase.DATABASE_NAME)
public class RecipeDatabase {
    public static final int VERSION = 2;
    public static final String DATABASE_NAME = "recipes.db";

    @Table(RecipeContract.RecipeEntry.class)
    public static final String RECIPES = "recipes";

    @Table(RecipeContract.IngredientEntry.class)
    public static final String INGREDIENTS = "ingredients";

    @Table(RecipeContract.StepEntry.class)
    public static final String STEPS = "steps";

    @OnUpgrade
    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion, int newVersion) {
        // Delete each table from the database because all the information will be pulled anyways
        String deleteTable = "DROP TABLE IF EXISTS ";
        String[] tables = {RECIPES, INGREDIENTS, STEPS};
        for (String table : tables) {
            db.execSQL(deleteTable + table);
        }
    }
}
