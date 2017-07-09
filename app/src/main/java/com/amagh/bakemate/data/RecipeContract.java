package com.amagh.bakemate.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

/**
 * Uses the Schematic (https://github.com/SimonVT/schematic) library to define the columns in a
 * content provider backed by a database
 */

public class RecipeContract {

    public class RecipeEntry {
        @DataType(DataType.Type.INTEGER)
        @PrimaryKey(onConflict = ConflictResolutionType.REPLACE)
        @AutoIncrement
        public static final String COLUMN_ID                = "_id";

        @DataType(DataType.Type.INTEGER)
        @NotNull
        public static final String COLUMN_RECIPE_ID         = "recipe_id";

        @DataType(DataType.Type.TEXT)
        @NotNull
        public static final String COLUMN_RECIPE_NAME       = "recipe";

        @DataType(DataType.Type.INTEGER)
        public static final String COLUMN_RECIPE_SERVINGS   = "servings";

        @DataType(DataType.Type.TEXT)
        public static final String COLUMN_IMAGE_URL         = "image_url";
    }

    public class IngredientEntry {
        @DataType(DataType.Type.INTEGER)
        @PrimaryKey(onConflict = ConflictResolutionType.FAIL)
        @AutoIncrement
        public static final String COLUMN_ID                = "_id";

        @DataType(DataType.Type.INTEGER)
        @NotNull
        @References(
                table = RecipeDatabase.RECIPES,
                column = RecipeEntry.COLUMN_RECIPE_ID)
        public static final String COLUMN_RECIPE_ID         = RecipeEntry.COLUMN_RECIPE_ID;

        @DataType(DataType.Type.REAL)
        @NotNull
        public static final String COLUMN_QUANTITY          = "quantity";

        @DataType(DataType.Type.TEXT)
        @NotNull
        public static final String COLUMN_MEASURE           = "measure";

        @DataType(DataType.Type.TEXT)
        @NotNull
        public static final String COLUMN_INGREDIENT        = "ingredient";
    }

    public class StepEntry {
        @DataType(DataType.Type.INTEGER)
        @PrimaryKey(onConflict =  ConflictResolutionType.FAIL)
        @AutoIncrement
        public static final String COLUMN_ID                = "_id";

        @DataType(DataType.Type.INTEGER)
        @NotNull
        public static final String COLUMN_STEP_ID           = "step_id";

        @DataType(DataType.Type.INTEGER)
        @NotNull
        @References(
                table = RecipeDatabase.RECIPES,
                column = RecipeEntry.COLUMN_RECIPE_ID)
        public static final String COLUMN_RECIPE_ID         = RecipeEntry.COLUMN_RECIPE_ID;

        @DataType(DataType.Type.TEXT)
        @NotNull
        public static final String COLUMN_SHORT_DESC        = "short_description";

        @DataType(DataType.Type.TEXT)
        @NotNull
        public static final String COLUMN_DESCRIPTION       = "description";

        @DataType(DataType.Type.TEXT)
        public static final String COLUMN_VIDEO_URL         = "video_url";

        @DataType(DataType.Type.TEXT)
        public static final String COLUMN_THUMBNAIL_URL     = "thumbnail_url";
    }
}
