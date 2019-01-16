package io.lundie.michael.bakeit.ui.adapters;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.lundie.michael.bakeit.R;
import io.lundie.michael.bakeit.datamodel.models.Recipe;
import io.lundie.michael.bakeit.datamodel.models.RecipeStep;
import io.lundie.michael.bakeit.utilities.CacheManager;
import io.lundie.michael.bakeit.utilities.VideoThumbnailUtility;

public class RecipesViewAdapter extends RecyclerView.Adapter<RecipesViewAdapter.ViewHolder> {

    private static final String LOG_TAG = RecipesViewAdapter.class.getName();

    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }

    ArrayList<Recipe> mRecipes;

    private final OnItemClickListener mListener;

    public RecipesViewAdapter(ArrayList<Recipe> recipes, OnItemClickListener listener) {
        this.mRecipes = recipes;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public RecipesViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_card,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipesViewAdapter.ViewHolder holder, int position) {
        Log.i(LOG_TAG, "Recipe Step item count is: " +mRecipes.size());
        holder.bind(mRecipes.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setRecipes(ArrayList<Recipe> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        final View mView;
        Context mContext;

        @BindView(R.id.list_recipe_name_tv) TextView recipeNameTv;
        @BindView(R.id.list_recipe_servings) TextView servingsTv;
        @BindView(R.id.list_recipe_thumbnail_iv) ImageView thumbnailIV;
        @BindView(R.id.list_progress_bar) ProgressBar thumbnailProgressBar;

        ViewHolder(View view) {
            super(view);
            mContext = view.getContext();
            this.mView = view;
            ButterKnife.bind(this, view);
        }

        void bind(final Recipe recipe, final OnItemClickListener listener) {
            String servings = itemView.getResources().getString(R.string.recipes_text_serving_size)
                    + recipe.getServings();

            recipeNameTv.setText(recipe.getName());
            servingsTv.setText(servings);

            String url = recipe.getImage();

            if(url != null && !url.isEmpty() && url.length() > 10) {
                // If we have a valid image url let's fetch the thumbnail using picasso.
                Picasso.get().load(url)
                        .into(thumbnailIV, new Callback() {
                            @Override
                            public void onSuccess() {
                                thumbnailProgressBar.setVisibility(View.INVISIBLE);
                            }
                            @Override
                            public void onError(Exception e) {
                                thumbnailProgressBar.setVisibility(View.INVISIBLE);
                                thumbnailIV.setBackgroundColor(ContextCompat.getColor(
                                        mContext, R.color.colorPrimaryLight));
                            }
                        });
            } else {
                BitmapDrawable image = CacheManager.getInstance().getBitmapFromMemCache(recipe.getId());

                if(image != null) {
                    thumbnailProgressBar.setVisibility(View.INVISIBLE);
                    thumbnailIV.setImageDrawable(image);
                } else {
                    // Let's generate an image from the video.
                    // NOTE: This is a little beyond me currently, but I gave it my best shot!
                    // It is slow and laggy. I will improve this ASAP.
                    ArrayList<RecipeStep> recipeSteps = (ArrayList<RecipeStep>) recipe.getRecipeSteps();

                    //Reverse iterate through our recipeSteps to get the very last video url
                    for (int i = recipeSteps.size() - 1; i >= 0; i--) {
                        url = recipeSteps.get(i).getVideoURL();

                        if(url != null && url.length() > 10) {
                            // We are good to go. We will generate a thumbnail from the video stream.
                            HashMap<Integer, String> imageAndViewPair = new HashMap<Integer, String>();
                            imageAndViewPair.put(thumbnailIV.getId(), url);
                            new VideoThumbnailUtility().fetchVideoThumbnail(mContext, imageAndViewPair,
                                    mView, recipe.getId(), thumbnailProgressBar);
                            break;
                        }
                    }
                }
            }

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(recipe);
                }
            });
        }
    }
}
