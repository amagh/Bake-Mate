package com.amagh.bakemate.data;

import android.content.ContentUris;
import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

import java.util.List;

/**
 * Uses the Schematic (https://github.com/SimonVT/schematic) to create a content provider and
 * define URIs for the provider
 */

@SuppressWarnings("WeakerAccess")
@ContentProvider(
        authority = RecipeProvider.AUTHORITY,
        database = RecipeDatabase.class)
public class RecipeProvider {
    // **Constants** //
    public static final String AUTHORITY = "com.amagh.bakemate.provider";
//    public static final String JOIN_RECIPE_INGREDIENT_STEP =
//            "JOIN " + RecipeDatabase.INGREDIENTS + " ON "                                       +
//            RecipeDatabase.RECIPES + "." + RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + " = "  +
//            RecipeDatabase.INGREDIENTS + "." + RecipeContract.IngredientEntry.COLUMN_RECIPE_ID  +
//            "JOIN" + RecipeDatabase.STEPS + " ON "                                              +
//            RecipeDatabase.RECIPES + "." + RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + " = "  +
//            RecipeDatabase.STEPS + "." + RecipeContract.StepEntry.COLUMN_RECIPE_ID;

    interface Path {
        String RECIPES = "recipes";
        String INGREDIENTS = "ingredients";
        String STEPS = "steps";
    }

    /**
     * Helper method for retrieving the recipeId from the URI
     *
     * @param uri URI containing a recipeId
     * @return The ID of the recipe in the URI or -1 if not recipeId is contained in the URI
     */
    public static long getRecipeIdFromUri(Uri uri) {
        // Get all path segments
        List<String> segments = uri.getPathSegments();

        // Check to see if URI contains the correct preceding segment
        if (segments.contains(Path.RECIPES)) {
            // recipeId is contained in the segment after Path.RECIPES
            return Long.parseLong(segments.get(segments.indexOf(Path.RECIPES) + 1));
        } else {
            return -1;
        }
    }

    @TableEndpoint(table = RecipeDatabase.RECIPES)
    public static class Recipes {

        @ContentUri(
                path        = Path.RECIPES,
                type        = "vnd.android.cursor.dir/recipe",
                defaultSort = RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + " ASC")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + RecipeDatabase.RECIPES);

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
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" +RecipeDatabase.INGREDIENTS);

        @InexactContentUri(
                path        = Path.INGREDIENTS + "/" + Path.RECIPES + "/#",
                name        = "INGREDIENTS_WITH_RECIPE_ID",
                type        = "vnd.android.cursor.dir/ingredient",
                whereColumn = RecipeContract.IngredientEntry.COLUMN_RECIPE_ID,
                pathSegment = 2,
                defaultSort = RecipeContract.IngredientEntry.COLUMN_MEASURE + " ASC")
        public static Uri forRecipe(long recipeId) {
            return CONTENT_URI.buildUpon()
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
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + RecipeDatabase.STEPS);

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

        @InexactContentUri(
                path        = Path.STEPS + "/" + Path.RECIPES + "/#/" + Path.STEPS + "/#",
                name        = "STEP_WITH_RECIPE_ID_AND_STEP_ID",
                type = "vnd.android.cursor.item/step",
                whereColumn = {RecipeContract.StepEntry.COLUMN_RECIPE_ID, RecipeContract.StepEntry.COLUMN_STEP_ID},
                pathSegment = {2, 4})
        public static Uri forRecipeAndStep(long recipeId, long stepId) {
            return forRecipe(recipeId).buildUpon()
                    .appendPath(Path.STEPS)
                    .appendPath(Long.toString(stepId))
                    .build();
        }
    }
}
