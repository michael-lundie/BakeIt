package injection.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.lundie.michael.bakeit.ui.adapters.IngredientsViewAdapter;
import io.lundie.michael.bakeit.ui.fragments.utils.DataUtils;

@Module
public class IngredientsViewAdapterModule {

    @Provides
    IngredientsViewAdapter provideIngredientsViewAdapter(DataUtils dataUtils) {
        return new IngredientsViewAdapter(dataUtils);
    }
}
