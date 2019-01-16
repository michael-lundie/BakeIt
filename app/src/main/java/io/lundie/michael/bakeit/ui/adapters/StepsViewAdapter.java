package io.lundie.michael.bakeit.ui.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

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

    private ArrayList<Boolean> mSetStepBackgroundBooleans;

    public StepsViewAdapter(ArrayList<RecipeStep> recipeSteps, StepsViewAdapter.OnItemClickListener listener) {
        this.mRecipeSteps = recipeSteps;
        this.mListener = listener;
    }

    public StepsViewAdapter(ArrayList<RecipeStep> recipeSteps, ArrayList<Boolean> setStepBackgroundBooleans,
                            StepsViewAdapter.OnItemClickListener listener) {
        this.mRecipeSteps = recipeSteps;
        this.mListener = listener;
        this.mSetStepBackgroundBooleans = setStepBackgroundBooleans;
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
        if(mSetStepBackgroundBooleans != null) {
            holder.bind(mRecipeSteps.get(position),
                    mSetStepBackgroundBooleans.get(position), mListener);
        } else {
            holder.bind(mRecipeSteps.get(position), Boolean.FALSE, mListener);
        }

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

    /**
     * Method override for setStepsList
     */
    public void setStepsList(ArrayList<RecipeStep> steps,
                             ArrayList<Boolean> setStepBackgroundBooleans) {
        mSetStepBackgroundBooleans = setStepBackgroundBooleans;
        mRecipeSteps = steps;
        notifyDataSetChanged();
    }

    @Override public int getItemViewType(int position) { return position; }


    class ViewHolder extends RecyclerView.ViewHolder {

        final View mView;


        // Bind views using butterknife
        @BindView(R.id.list_step_top_spacer) View recipeStepTopSpacer;
        @BindView(R.id.list_step_name_tv) TextView recipeStepShortDescription;
        @BindView(R.id.list_step_number) TextView recipeStepNumber;
        @BindView(R.id.recipe_step_icon_cam) ImageView recipeStepIconCam;
        @BindView(R.id.recipe_step_icon_note) ImageView recipeStepIconNote;

        ViewHolder(View view) {
            super(view);
            this.mView = view;
            ButterKnife.bind(this, view);
        }

        void bind(final RecipeStep recipeStep, final Boolean setBackground
                ,final StepsViewAdapter.OnItemClickListener listener) {

            if(recipeStep.getStepNumber() == 0) {
                recipeStepTopSpacer.setVisibility(View.INVISIBLE);
            }

            if(setBackground == Boolean.TRUE) {
                mView.setBackgroundColor(mView.getContext().getResources().getColor(R.color.colorAccent));
            } else {
                mView.setBackgroundColor(mView.getContext().getResources().getColor(R.color.colorPrimaryLight));
            }

            recipeStepShortDescription.setText(recipeStep.getShortDescription());
            Integer stepNumber = recipeStep.getStepNumber() +1;
            Log.v(LOG_TAG, "Step number: " + stepNumber);
            recipeStepNumber.setText(stepNumber.toString());

            if(recipeStep.getVideoURL().isEmpty()) {
                Log.i(LOG_TAG, "TEST: VIDEO URL NULL");
                recipeStepIconCam.setVisibility(View.GONE);
                recipeStepIconNote.setVisibility(View.VISIBLE);
            }
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(recipeStep);
                }
            });

        }
    }
}
