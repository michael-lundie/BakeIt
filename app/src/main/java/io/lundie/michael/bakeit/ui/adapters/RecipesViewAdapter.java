package io.lundie.michael.bakeit.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.lundie.michael.bakeit.R;
import io.lundie.michael.bakeit.datamodel.models.Recipe;
import io.lundie.michael.bakeit.datamodel.models.RecipeStep;
import io.lundie.michael.bakeit.utilities.CacheManager;

public class RecipesViewAdapter extends RecyclerView.Adapter<RecipesViewAdapter.ViewHolder> {

    private static final String LOG_TAG = RecipesViewAdapter.class.getName();

    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }

    ArrayList<Recipe> mRecipes;

    private final OnItemClickListener mListener;
    private BitmapDrawable nothumbnail;



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
            if(nothumbnail == null) {
                nothumbnail = new BitmapDrawable(BitmapFactory.decodeResource
                        (mContext.getResources(), R.drawable.no_thumbnail));
            }
        }

        void bind(final Recipe recipe, final OnItemClickListener listener) {
            String servings = itemView.getResources().getString(R.string.recipes_text_serving_size)
                    + recipe.getServings();

            recipeNameTv.setText(recipe.getName());
            servingsTv.setText(servings);

            String url = recipe.getImage();
            if(url != null && !url.isEmpty() && url.length() > 10) {
                Glide.with(mContext).asBitmap()
                        .load(url)
                        .into(thumbnailIV);
            } else {
                Log.v(LOG_TAG, "TEST: Attempting to restore cache: " + recipe.getId());
                BitmapDrawable image = CacheManager.getInstance().getBitmapFromMemCache(recipe.getId());

                if(image != null) {
                    thumbnailProgressBar.setVisibility(View.INVISIBLE);
                    thumbnailIV.setImageDrawable(image);
                } else {
                    thumbnailProgressBar.setVisibility(View.VISIBLE);

                    ArrayList<RecipeStep> recipeSteps = (ArrayList<RecipeStep>) recipe.getRecipeSteps();
                    for (int i = recipeSteps.size() - 1; i >= 0; i--) {
                        url = recipeSteps.get(i).getVideoURL();
                        if(url != null && url.length() > 10) {

                            HashMap<Integer, String> imageAndViewPair = new HashMap<Integer, String>();
                            imageAndViewPair.put(thumbnailIV.getId(), url);
                            loadImageInThread(mContext, imageAndViewPair, mView, recipe.getId(), thumbnailProgressBar);

                            //new VideoThumbnail(url, thumbnailIV, thumbnailProgressBar);
                            break;
                        } else {
                            //TODO: Placeholder image here
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
    private void loadImageInThread(Context context, final Map<Integer, String> bindings, final View view, final int id, final ProgressBar progressBar) {
        for (final Map.Entry<Integer, String> binding :
                bindings.entrySet()) {
            new DownloadImageAsync(new DownloadImageAsync.Listener() {

                ImageView thumbnailView = view.findViewById(binding.getKey());

                @Override
                public void onImageDownloaded(final Bitmap bitmap) {
                    // Create a new bitmap drawable
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
                    //Add the drawable to our cache instance
                    CacheManager.getInstance().addBitmapToMemoryCache(id, bitmapDrawable);
                    //Set the drawable to our fragment thumbnail view
                    thumbnailView.setImageDrawable(bitmapDrawable);
                    //Show our thumbnail
                    thumbnailView.setVisibility(View.VISIBLE);
                    //Hide the UI progress spinner
                    progressBar.setVisibility(View.INVISIBLE);
                }
                @Override
                public void onImageDownloadError() {
                    // Let's hide the thumbnail view since no image was returned.
                    CacheManager.getInstance().addBitmapToMemoryCache(id, nothumbnail);
                    thumbnailView.setImageDrawable(nothumbnail);
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.e(LOG_TAG, "Failed to download image for "
                            + binding.getKey());
                }
            }).execute(binding.getValue());
        }
    }
}
