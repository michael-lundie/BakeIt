package io.lundie.michael.bakeit.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.lundie.michael.bakeit.ui.fragments.IngredientsFragment;
import io.lundie.michael.bakeit.ui.fragments.StepsFragment;
import io.lundie.michael.bakeit.utilities.AppConstants;

public class RecipePagerAdapter extends FragmentPagerAdapter {

    private static final int FRAG_NUM = 2;

    public RecipePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return FRAG_NUM;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new StepsFragment();
        } else {
            return new IngredientsFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if(position == 0){
            return AppConstants.FRAGMENT_STEPS_TITLE;
        } else {
            return AppConstants.FRAGMENT_INGREDIENTS_TITLE;
        }
    }
}
