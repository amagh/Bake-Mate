package com.amagh.bakemate.data;

import android.content.ContentUris;
import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Uses the Schematic (https://github.com/SimonVT/schematic) to create a content provider and
 * define URIs for the provider
 */

@ContentProvider(
        authority = RecipeProvider.AUTHORITY,
        database = RecipeDatabase.class)
public class RecipeProvider {

    public static final String AUTHORITY = "com.amagh.bakemate.provider";
    public static final String JOIN_RECIPE_INGREDIENT_STEP =
            "JOIN " + RecipeDatabase.INGREDIENTS + " ON "                                       +
            RecipeDatabase.RECIPES + "." + RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + " = "  +
            RecipeDatabase.INGREDIENTS + "." + RecipeContract.IngredientEntry.COLUMN_RECIPE_ID  +
            "JOIN" + RecipeDatabase.STEPS + " ON "                                              +
            RecipeDatabase.RECIPES + "." + RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + " = "  +
            RecipeDatabase.STEPS + "." + RecipeContract.StepEntry.COLUMN_RECIPE_ID;

    interface Path {
        String RECIPES = "recipes";
        String INGREDIENTS = "ingredients";
        String STEPS = "steps";
    }

    @TableEndpoint(table = RecipeDatabase.RECIPES)
    public static class Recipes {

        @ContentUri(
                path        = Path.RECIPES,
                type        = "vnd.android.cursor.dir/recipe",
                defaultSort = RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + " ASC")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/recipes");

        @InexactContentUri(
                path        = Path.RECIPES + "/#",
                name        = "RECIPE_ID",
                type        = "vnd.android.cursor.item/recipe",
                whereColumn = RecipeContract.RecipeEntry.COLUMN_RECIPE_ID,
                pathSegment = 1)
        public static Uri withId(long recipeId) {
            return ContentUris.withAppendedId(CONTENT_URI, recipeId);
        }
    }

    @TableEndpoint(table = RecipeDatabase.INGREDIENTS)
    public static class Ingredients {

        @ContentUri(
                path        = Path.INGREDIENTS,
                type        = "vnd.android.cursor.dir/ingredient",
                defaultSort = RecipeContract.IngredientEntry.COLUMN_ID + " ASC")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/ingredients");

        @InexactContentUri(
                path        = Path.INGREDIENTS + "/" + Path.RECIPES + "/#",
                name        = "INGREDIENTS_WITH_RECIPE_ID",
                type        = "vnd.android.cursor.dir/ingredient",
                whereColumn = RecipeContract.IngredientEntry.COLUMN_RECIPE_ID,
                pathSegment = 2,
                defaultSort = RecipeContract.IngredientEntry.COLUMN_MEASURE + " ASC")
        public static Uri forRecipe(long recipeId) {
            return Recipes.CONTENT_URI.buildUpon()
                    .appendPath(Path.RECIPES)
                    .appendPath(Long.toString(recipeId))
                    .build();
        }

    }

    @TableEndpoint(table = RecipeDatabase.STEPS)
    public static class Steps {

        @ContentUri(
                path        = Path.STEPS,
                type        = "vnd.android.cursor.dir/step",
                defaultSort = RecipeContract.StepEntry.COLUMN_STEP_ID + " ASC")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/steps");

        @InexactContentUri(
                path        = Path.STEPS + "/" + Path.RECIPES + "/#",
                name        = "STEPS_WITH_RECIPE_ID",
                type        = "vnd.android.cursor.dir/step",
                whereColumn = RecipeContract.StepEntry.COLUMN_RECIPE_ID,
                pathSegment = 2,
                defaultSort = RecipeContract.StepEntry.COLUMN_STEP_ID + " ASC")
        public static Uri forRecipe(long recipeId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Path.RECIPES)
                    .appendPath(Long.toString(recipeId))
                    .build();
        }
    }
}
