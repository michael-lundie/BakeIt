package io.lundie.michael.bakeit.ui.activities;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.lundie.michael.bakeit.R;
import io.lundie.michael.bakeit.datamodel.models.Recipe;
import io.lundie.michael.bakeit.viewmodel.RecipesViewModel;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getName();

    @Inject
    RecipesViewModel recipesViewModel;

    ArrayList<Recipe> mRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Configure Dagger 2 injection
        this.configureDagger();



        recipesViewModel.getRecipes().observe(this, new Observer<ArrayList<Recipe>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Recipe> recipes) {

                mRecipes = recipes;
                if(mRecipes != null) {
                    for (int i=0; i < mRecipes.size(); i++) {
                        Log.i(LOG_TAG, "Recipe ID: " + mRecipes.get(i).getId());
                    }
                }
                Log.v(LOG_TAG, "on changed called" + mRecipes);
            }
        });


    }

    private void configureDagger(){
        AndroidInjection.inject(this);
    }

}