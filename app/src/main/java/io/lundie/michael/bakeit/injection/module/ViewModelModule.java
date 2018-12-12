package io.lundie.michael.bakeit.injection.module;


import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import io.lundie.michael.bakeit.injection.ViewModelKey;
import io.lundie.michael.bakeit.viewmodel.RecipesViewModel;
import io.lundie.michael.bakeit.viewmodel.RecipesViewModelFactory;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(RecipesViewModel.class)
    abstract ViewModel bindMoviesViewModel(RecipesViewModel recipesViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(RecipesViewModelFactory factory);
}
