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

    interface Path {
        String RECIPES = "recipes";
        String INGREDIENTS = "ingredients";
        String STEPS = "steps";
    }

    @TableEndpoint(table = RecipeDatabase.RECIPES)
    public static class Recipes {

        @ContentUri(
                path = Path.RECIPES,
                type = "vnd.android.cursor.dir/recipe",
                defaultSort = RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + " ASC")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/recipes");

        @InexactContentUri(
                path = Path.RECIPES + "/#",
                name = "RECIPE_ID",
                type = "vnd.android.cursor.item/recipe",
                whereColumn = RecipeContract.RecipeEntry.COLUMN_RECIPE_ID,
                pathSegment = 1)
        public static Uri withId(long recipeId) {
            return ContentUris.withAppendedId(CONTENT_URI, recipeId);
        }
    }

    @TableEndpoint(table = RecipeDatabase.INGREDIENTS)
    public static class Ingredients {

        @ContentUri(
                path = "ingredients",
                type = "vnd.android.cursor.dir/ingredient",
                defaultSort = RecipeContract.IngredientEntry.COLUMN_ID + " ASC")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/ingredients");

    }

    @TableEndpoint(table = RecipeDatabase.STEPS)
    public static class Steps {

        @ContentUri(
                path = "steps",
                type = "vnd.android.cursor.dir/step",
                defaultSort = RecipeContract.StepEntry.COLUMN_STEP_ID + " ASC")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/steps");
    }
}
