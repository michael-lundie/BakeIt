package io.lundie.michael.bakeit.datamodel.deserializers;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import io.lundie.michael.bakeit.datamodel.models.RecipeStep;

/**
 * Simple deserializer class allowing for a custom id field to be added to the {@link RecipeStep}
 * POJO model during GSON deserialization.
 *
 */
public class RecipeStepListDeserializer implements JsonDeserializer<RecipeStep> {

    // Note we cannot use a static variable here or else we will have trouble.
    private int stepNumber = 0;

    @Override
    public RecipeStep deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        int currentId = jsonObject.get("id").getAsInt();

        if(currentId == 0) {
            stepNumber = 0;
        }

        Log.i("Deserialize", "TEST: Deserialize " + stepNumber);

        RecipeStep recipeStep = new RecipeStep(
                stepNumber,
                jsonObject.get("id").getAsInt(),
                jsonObject.get("shortDescription").getAsString(),
                jsonObject.get("description").getAsString(),
                jsonObject.get("videoURL").getAsString(),
                jsonObject.get("thumbnailURL").getAsString()
        );
        stepNumber = stepNumber;
        stepNumber++;
        return recipeStep;
    }
}
