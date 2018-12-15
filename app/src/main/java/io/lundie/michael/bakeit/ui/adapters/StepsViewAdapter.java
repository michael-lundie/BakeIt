package io.lundie.michael.bakeit.ui.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.lundie.michael.bakeit.R;
import io.lundie.michael.bakeit.datamodel.models.Recipe;
import io.lundie.michael.bakeit.datamodel.models.RecipeStep;

public class StepsViewAdapter extends RecyclerView.Adapter<StepsViewAdapter.ViewHolder> {

    private static final String LOG_TAG = RecipesViewAdapter.class.getName();

    public interface OnItemClickListener {
        void onItemClick(RecipeStep recipeStepItem);
    }

    ArrayList<RecipeStep> mRecipeSteps;

    private final StepsViewAdapter.OnItemClickListener mListener;



    public StepsViewAdapter(ArrayList<RecipeStep> recipeSteps, StepsViewAdapter.OnItemClickListener listener) {
        this.mRecipeSteps = recipeSteps;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public StepsViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.steps_card,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepsViewAdapter.ViewHolder holder, int position) {
        Log.i(LOG_TAG, "Binding to position: " + position);
        holder.bind(mRecipeSteps.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        Log.i(LOG_TAG, "Recipe Step item count is: " +mRecipeSteps.size());
        return mRecipeSteps.size();
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setStepsList(ArrayList<RecipeStep> steps) {
        mRecipeSteps = steps;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        final View mView;

        // Bind views using butterknife
        @BindView(R.id.list_step_name_tv) TextView recipeStepShortDescription;

        ViewHolder(View view) {
            super(view);
            this.mView = view;
            ButterKnife.bind(this, view);
        }

        void bind(final RecipeStep recipeStep, final StepsViewAdapter.OnItemClickListener listener) {
            recipeStepShortDescription.setText(recipeStep.getShortDescription());
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(recipeStep);
                }
            });

        }
    }
}
