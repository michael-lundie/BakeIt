package io.lundie.michael.bakeit;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.internal.util.Checks;
import androidx.test.rule.ActivityTestRule;

import io.lundie.michael.bakeit.ui.activities.LauncherActivity;
import io.lundie.michael.bakeit.utilities.RecyclerViewMatcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class LauncherActivityTestTemp {

    @Rule
    public ActivityTestRule<LauncherActivity> mActivityTestRule = new ActivityTestRule<>(LauncherActivity.class);

    @Test
    public void launcherActivityCheckCountTest() {
        onView(withId(R.id.recipes_list_rv)).check(new RecyclerViewItemCountAssertion(4));
    }

    @Test
    public void launcherActivityContentTest() {
        onView(withRecyclerView(R.id.recipes_list_rv).atPosition(0))
                .check(matches(hasDescendant(withText("Nutella Pie"))));
        onView(withRecyclerView(R.id.recipes_list_rv).atPosition(1))
                .check(matches(hasDescendant(withText("Brownies"))));
        onView(withRecyclerView(R.id.recipes_list_rv).atPosition(2))
                .check(matches(hasDescendant(withText("Yellow Cake"))));
        onView(withRecyclerView(R.id.recipes_list_rv).atPosition(3))
                .check(matches(hasDescendant(withText("Cheesecake"))));
    }

    @Test
    public void launcherActivityOpenRecipeTest() {
        
        onView(withRecyclerView(R.id.recipes_list_rv).atPosition(3)).perform(click());
    }
    /**
     * Method from: https://stackoverflow.com/a/37339656
     */
    public class RecyclerViewItemCountAssertion implements ViewAssertion {
        private final int expectedCount;

        private RecyclerViewItemCountAssertion(int expectedCount) {
            this.expectedCount = expectedCount;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            assertThat(adapter.getItemCount(), is(expectedCount));
        }
    }

    // Used tutorial: https://spin.atomicobject.com/2016/04/15/espresso-testing-recyclerviews/
    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }
}
