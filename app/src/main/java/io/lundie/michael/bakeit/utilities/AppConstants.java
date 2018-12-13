package io.lundie.michael.bakeit.utilities;

import android.app.Application;

import javax.inject.Inject;

import io.lundie.michael.bakeit.R;

/**
 * class which is injected via dagger, allowing constants to be available to various classes
 * which otherwise wouldn't have access to a context object
 */
public class AppConstants {

    Application mApplication;
    public static String FRAGTAG_RECIPES;
    public static String FRAGTAG_STEPS;

    @Inject
    public AppConstants(Application application) {
        mApplication = application;

        FRAGTAG_RECIPES = mApplication.getString(R.string.fragtag_recipes);
        FRAGTAG_STEPS = mApplication.getString(R.string.fragtag_steps);
    }
}
