package io.lundie.michael.bakeit.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.lundie.michael.bakeit.R;
import io.lundie.michael.bakeit.datamodel.models.Ingredient;
import io.lundie.michael.bakeit.ui.fragments.dummy.DummyContent.DummyItem;
import io.lundie.michael.bakeit.ui.fragments.utils.DataUtils;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Ingredient}
 */
public class IngredientsViewAdapter extends RecyclerView.Adapter<IngredientsViewAdapter.ViewHolder> {

    DataUtils dataUtils;

    private ArrayList<Ingredient> mIngredients = new ArrayList<>();

    @Inject
    public IngredientsViewAdapter(DataUtils dataUtils) {
        this.dataUtils = dataUtils;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.bind(mIngredients.get(position));
    }

    @Override
    public int getItemCount() {
        return mIngredients.size();
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setIngredients(ArrayList<Ingredient> ingredients) {
        mIngredients = ingredients;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        @BindView(R.id.ingredient_name) TextView ingredientNameTv;
        @BindView(R.id.ingredient_quantity) TextView ingredientQuantityTv;

        ViewHolder(View view) {
            super(view);
            this.mView = view;
            ButterKnife.bind(this, view);
        }

        void bind(final Ingredient ingredient) {
            ingredientNameTv.setText(ingredient.getIngredient());
            String text = dataUtils.parseQuantity(ingredient.getQuantity(), ingredient.getMeasure());
            ingredientQuantityTv.setText(text);

        }
    }
}
