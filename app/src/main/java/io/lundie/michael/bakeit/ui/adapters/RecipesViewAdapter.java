package io.lundie.michael.bakeit.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.lundie.michael.bakeit.R;
import io.lundie.michael.bakeit.datamodel.models.Recipe;

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

        // Bind views using butterknife
        @BindView(R.id.list_recipe_name_tv) TextView recipeNameTv;

        ViewHolder(View view) {
            super(view);
            this.mView = view;
            ButterKnife.bind(this, view);
        }

        void bind(final Recipe recipe, final OnItemClickListener listener) {
            recipeNameTv.setText(recipe.getName());
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(recipe);
                }
            });

        }
    }
}
