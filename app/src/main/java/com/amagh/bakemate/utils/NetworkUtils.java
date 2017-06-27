package com.amagh.bakemate.utils;


import android.content.ContentValues;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * POJO Helper class for connecting to the network and parsing JSON responses
 */

public class NetworkUtils {
    // **Constants** //
    private static final String TAG = NetworkUtils.class.getSimpleName();

    // Parsing Recipe JSON
    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_INGREDIENTS_ARRAY = "ingredients";
    private static final String JSON_QUANTITY = "quantity";
    private static final String JSON_MEASURE = "measure";
    private static final String JSON_INGREDIENT = "ingredient";
    private static final String JSON_STEPS_ARRAYS = "steps";
    private static final String JSON_SHORT_DESC = "shortDescription";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_VIDEO_URL = "videoURL";
    private static final String JSON_THUMBNAIL_URL = "thumbnailURL";
    private static final String JSON_SERVINGS = "servings";


    public static String getHttpResponse(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            Scanner scanner = new Scanner(urlConnection.getInputStream());
            scanner.useDelimiter("\\A");

            String response = null;

            if (scanner.hasNext()) {
                response = scanner.next();
            }

            scanner.close();

            return response;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public static ContentValues[] getRecipeContentValuesFromJsonResponse(String jsonResponse)
            throws JSONException {

        JSONArray jsonArray = new JSONArray(jsonResponse);

        ContentValues[] contentValues = new ContentValues[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject recipeObject = jsonArray.getJSONObject(i);
            ContentValues recipeValues = new ContentValues();

            int recipeId = recipeObject.getInt(JSON_ID);
            String recipeName = recipeObject.getString(JSON_NAME);
            int servings = recipeObject.getInt(JSON_SERVINGS);

            contentValues[i] = recipeValues;
        }

        return contentValues;
    }
}
