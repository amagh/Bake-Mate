package com.amagh.bakemate.utils;

import android.content.ContentValues;

import com.amagh.bakemate.data.RecipeContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hnoct on 6/27/2017.
 */

public class ParsingUtils {
    // **Constants** //

    // Parsing Recipe JSON
    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_INGREDIENTS_ARRAY = "ingredients";
    private static final String JSON_QUANTITY = "quantity";
    private static final String JSON_MEASURE = "measure";
    private static final String JSON_INGREDIENT = "ingredient";
    private static final String JSON_STEPS_ARRAY = "steps";
    private static final String JSON_SHORT_DESC = "shortDescription";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_VIDEO_URL = "videoURL";
    private static final String JSON_THUMBNAIL_URL = "thumbnailURL";
    private static final String JSON_SERVINGS = "servings";

    /**
     * Creates an Array of ContentValues describing a recipe from a String containing a JSONObject
     *
     * @param jsonResponse  String containing a JSONObject
     * @return An Array of ContentValues describing the recipes contained in the JSONArray
     * @throws JSONException If there is an error parsing the String to a JSONObject
     */
    public static ContentValues[] getRecipeContentValuesFromJson(String jsonResponse)
            throws JSONException {

        // Create a JSONArray from the jsonResponse
        JSONArray jsonArray = new JSONArray(jsonResponse);

        // Init the Array of ContentValues to return
        ContentValues[] contentValues = new ContentValues[jsonArray.length()];

        // Iterate, create ContentValues from the data, and add it to the Array
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject recipeObject = jsonArray.getJSONObject(i);
            ContentValues recipeValues = new ContentValues();

            int recipeId = recipeObject.getInt(JSON_ID);
            String recipeName = recipeObject.getString(JSON_NAME);
            int servings = recipeObject.getInt(JSON_SERVINGS);

            recipeValues.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID, recipeId);
            recipeValues.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME, recipeName);
            recipeValues.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_SERVINGS, servings);

            contentValues[i] = recipeValues;
        }

        return contentValues;
    }

    /**
     * Creates an Array of ContentValues describing the ingredients of recipes from a String
     * containing a JSONObject
     *
     * @param jsonResponse  String containing a JSONObject
     * @return An Array of ContentValues describing ingredients for a recipe
     * @throws JSONException If there is an error parsing the String parameter to a JSONObject
     */
    public static ContentValues[] getIngredientValuesFromJson(String jsonResponse)
            throws JSONException {

        // Create a JSONArray from the jsonResponse
        JSONArray jsonArray = new JSONArray(jsonResponse);

        // Init the List of ContentValues that will be converted to an Array
        List<ContentValues> contentValuesList = new ArrayList<>();

        // Iterate, create ContentValues from the data, and add it to the List
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject recipeObject = jsonArray.getJSONObject(i);

            int recipeId = recipeObject.getInt(JSON_ID);

            JSONArray ingredientsArray = recipeObject.getJSONArray(JSON_INGREDIENTS_ARRAY);

            for (int j = 0; j < ingredientsArray.length(); j++) {
                ContentValues ingredientValues = new ContentValues();

                JSONObject ingredientObject = ingredientsArray.getJSONObject(j);

                double quantity = ingredientObject.getDouble(JSON_QUANTITY);
                String measure = ingredientObject.getString(JSON_MEASURE);
                String ingredient = ingredientObject.getString(JSON_INGREDIENT);

                ingredientValues.put(RecipeContract.IngredientEntry.COLUMN_RECIPE_ID, recipeId);
                ingredientValues.put(RecipeContract.IngredientEntry.COLUMN_QUANTITY, quantity);
                ingredientValues.put(RecipeContract.IngredientEntry.COLUMN_MEASURE, measure);
                ingredientValues.put(RecipeContract.IngredientEntry.COLUMN_INGREDIENT, ingredient);

                contentValuesList.add(ingredientValues);
            }
        }

        // Convert the List of ContentValues to an Array of equal size
        return contentValuesList.toArray(new ContentValues[contentValuesList.size()]);
    }

    /**
     * Creates an Array of ContentValues describing the steps for recipes from a String containing a
     * JSONObject
     * @param jsonResponse  String containing a JSONObject
     * @return An Array of ContentValues describing the steps for recipes
     * @throws JSONException If there is an error parsing the String parameter to a JSONObject
     */
    public static ContentValues[] getStepContentValuesFromJson(String jsonResponse)
            throws JSONException {

        // Create a JSONArray from the jsonResponse
        JSONArray jsonArray = new JSONArray(jsonResponse);

        // Init the List of ContentValues that will be converted to an Array
        List<ContentValues> contentValuesList = new ArrayList<>();

        // Iterate, create ContentValues from the data, and add to the List
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject recipeObject = jsonArray.getJSONObject(i);

            int recipeId = recipeObject.getInt(JSON_ID);

            JSONArray stepsArray = recipeObject.getJSONArray(JSON_STEPS_ARRAY);

            for (int j = 0; j < stepsArray.length(); j++) {
                ContentValues stepValues = new ContentValues();

                JSONObject stepObject = stepsArray.getJSONObject(j);

                int stepId = stepObject.getInt(JSON_ID);
                String shortDesc = stepObject.getString(JSON_SHORT_DESC);
                String description = stepObject.getString(JSON_DESCRIPTION);
                String videoUrl = stepObject.getString(JSON_VIDEO_URL);

                // Due to an error in the JSON, for one of the steps, the video URL is stored as the
                // thumbnail URL. This is to catch the special case
                if (videoUrl.isEmpty()) {
                    videoUrl = stepObject.getString(JSON_THUMBNAIL_URL);
                }

                stepValues.put(RecipeContract.StepEntry.COLUMN_RECIPE_ID, recipeId);
                stepValues.put(RecipeContract.StepEntry.COLUMN_STEP_ID, stepId);
                stepValues.put(RecipeContract.StepEntry.COLUMN_SHORT_DESC, shortDesc);
                stepValues.put(RecipeContract.StepEntry.COLUMN_SHORT_DESC, description);
                stepValues.put(RecipeContract.StepEntry.COLUMN_VIDEO_URL, videoUrl);

                contentValuesList.add(stepValues);
            }
        }

        // Convert the List of ContentValues to an Array of equal size
        return contentValuesList.toArray(new ContentValues[contentValuesList.size()]);
    }
}
