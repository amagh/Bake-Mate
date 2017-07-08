package com.amagh.bakemate.utils;

import android.content.Context;

import com.amagh.bakemate.R;

import java.util.Locale;

/**
 * Created by Nocturna on 7/4/2017.
 */

public class FormattingUtils {
    public static String formatQuantityAndMeasurement(Context context, double quantity, String measure) {
        String formattedMeasure = null;

        // Set the measure to more readable String
        switch (measure) {
            case "G":
                formattedMeasure = context.getString(R.string.measurement_gram);
                break;
            case "TBLSP":
                formattedMeasure = context.getString(R.string.measurement_tablespoon);
                break;
            case "TSP":
                formattedMeasure = context.getString(R.string.measurement_teaspoon);
                break;
            case "UNIT":
                formattedMeasure = "";
                break;
            case "K":
                formattedMeasure = context.getString(R.string.measurement_package);
                break;
            case "CUP":
                formattedMeasure = context.getString(R.string.measurement_cup);
                break;
            case "OZ":
                formattedMeasure = context.getString(R.string.measurement_ounce);
                break;
        }

        // Check if quantity contains a decimal or if it can be boxed to an equivalent int
        if (quantity == (int) quantity) {
            return String.format(Locale.getDefault(), "%d %s", (int) quantity, formattedMeasure);
        } else {
            return String.format(Locale.getDefault(), "%s %s", convertDecimalToFraction(quantity), formattedMeasure);
        }
    }

    /**
     * Helper method for converting decimals to fractions for easier readability
     *
     * @param quantity    The quantity of the ingredient
     * @return A formatted String replacing the "0.5" measurement when the Unicode fraction
     * equivalent
     */
    private static String convertDecimalToFraction(double quantity) {
        if ((int) quantity > 0) {
            return (int) quantity + "\u00bd";
        } else {
            return "\u00bd";
        }
    }
}
