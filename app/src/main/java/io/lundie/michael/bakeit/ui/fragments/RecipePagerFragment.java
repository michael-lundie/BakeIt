package io.lundie.michael.bakeit.ui.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.lundie.michael.bakeit.R;
import io.lundie.michael.bakeit.ui.adapters.RecipePagerAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipePagerFragment extends Fragment {

    @BindView(R.id.recipe_pager_view) ViewPager viewPager;
    @BindView(R.id.recipe_pager_tabs) TabLayout pagerTabsLayout;
    RecipePagerAdapter recipePagerAdapter;


    public RecipePagerFragment() {/* Required empty public constructor */ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View pagerView = inflater.inflate(R.layout.fragment_recipe_pagers, container, false);
        ButterKnife.bind(this, pagerView);

        recipePagerAdapter = new RecipePagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(recipePagerAdapter);
        pagerTabsLayout.setupWithViewPager(viewPager);
        return pagerView;
    }
}
