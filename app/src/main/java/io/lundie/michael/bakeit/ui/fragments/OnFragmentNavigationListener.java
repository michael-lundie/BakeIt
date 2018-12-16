package io.lundie.michael.bakeit.ui.fragments;

/**
 * An usinversal interface method which can be implemented by multiple Fragments,
 * allowing for communication between the root activity and fragments.
 * Concept adapted from https://stackoverflow.com/a/30763151
 */
public interface OnFragmentNavigationListener {
    void onFragmentNavigation(String destinationFragmentTag);
}
