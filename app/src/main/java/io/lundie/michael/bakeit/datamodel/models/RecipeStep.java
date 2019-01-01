package io.lundie.michael.bakeit.datamodel.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import io.lundie.michael.bakeit.datamodel.deserializers.RecipeStepListDeserializer;
import io.lundie.michael.bakeit.ui.fragments.RecipesFragment;

@JsonAdapter(RecipeStepListDeserializer.class)
public class RecipeStep implements Parcelable {

    private static final String LOG_TAG = RecipeStep.class.getName();

    private Integer stepNumber;

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("shortDescription")
    @Expose
    private String shortDescription;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("videoURL")
    @Expose
    private String videoURL;
    @SerializedName("thumbnailURL")
    @Expose
    private String thumbnailURL;

    public final static Parcelable.Creator<RecipeStep> CREATOR = new Creator<RecipeStep>() {

        @SuppressWarnings({ "unchecked" })
        public RecipeStep createFromParcel(Parcel in) {
            Log.i(LOG_TAG, "TEST: Creating RECIPE STEP <FROM> parcel.");
            return new RecipeStep(in); }

        public RecipeStep[] newArray(int size) { return (new RecipeStep[size]); }

    };

    private RecipeStep(Parcel in) {
        this.id = in.readInt();
        this.shortDescription = in.readString();
        this.description = in.readString();
        this.videoURL = in.readString();
        this.thumbnailURL = in.readString();
    }

    public RecipeStep(Integer stepNumber,
                      Integer id,
                      String shortDescription,
                      String description,
                      String videoURL,
                      String thumbnailURL) {

        this.stepNumber = stepNumber;
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
    }

    public Integer getStepNumber() {
        return stepNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public void writeToParcel(Parcel dest, int flags) {
        Log.i(LOG_TAG, "TEST: Writing RECIPE STEP <TO> parcel.");
        dest.writeInt(id);
        dest.writeString(shortDescription);
        dest.writeString(description);
        dest.writeString(videoURL);
        dest.writeString(thumbnailURL);
    }

    public int describeContents() {
        return 0;
    }

}