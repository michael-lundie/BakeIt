package io.lundie.michael.bakeit.ui.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;
import io.lundie.michael.bakeit.R;
import io.lundie.michael.bakeit.datamodel.models.Recipe;
import io.lundie.michael.bakeit.datamodel.models.RecipeStep;
import io.lundie.michael.bakeit.utilities.AppConstants;
import io.lundie.michael.bakeit.viewmodel.RecipesViewModel;

public class StepDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = StepDetailsFragment.class.getName();

    // Setting up some static variables
    private static boolean IS_LANDSCAPE_TABLET;
    private static boolean IS_TABLET;



    private boolean hasInitialisedPlayer = false;
    private boolean hasOnGoingMediaSession = false;

    private SimpleExoPlayer mExoPlayer;
    private MediaSource videoSource;



    private Handler mainHandler;
    private TrackSelection.Factory videoTrackSelectionFactory;
    private TrackSelector trackSelector;
    private LoadControl loadControl;
    private DataSource.Factory dataSourceFactory;

    @Inject
    ViewModelProvider.Factory recipesViewModelFactory;

    RecipesViewModel recipesViewModel;

    RecipeStep recipeStep;

    int totalSteps;
    Uri mediaUri;

    @BindView(R.id.detail_test_tv) TextView testTextTv;
    @BindView(R.id.previous_step_btn) Button previousStepBtn;
    @BindView(R.id.next_step_btn) Button nextStepBtn;
    @BindView(R.id.video_pv) PlayerView playerView;


    public StepDetailsFragment() { /* Required empty public constructor for fragment classes. */ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        //TODO: Add interaction listener, so any interaction with details set's it as default frag
        int currentStepNumber = recipeStep.getStepNumber();


        switch (view.getId()) {
            case R.id.previous_step_btn:
                if (currentStepNumber != 0) {
                    recipesViewModel.requestFragment(AppConstants.FRAGTAG_DETAILS);
                    recipesViewModel.selectRecipeStep(currentStepNumber -1);
                }
                break;
            case R.id.next_step_btn:
                if (currentStepNumber != recipesViewModel.getNumberOfSteps() -1) {
                    recipesViewModel.requestFragment(AppConstants.FRAGTAG_DETAILS);
                    recipesViewModel.selectRecipeStep(currentStepNumber +1);
                }
                break;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Checking the current orientation and device
        IS_LANDSCAPE_TABLET = getResources().getBoolean(R.bool.isLandscapeTablet);
        IS_TABLET = getResources().getBoolean(R.bool.isTablet);

        // Inflate the layout for this fragment
        View listFragmentView =  inflater.inflate(R.layout.fragment_details, container, false);

        // Time to butter some toast... Bind view references with butterknife library.
        ButterKnife.bind(this, listFragmentView);

        if (savedInstanceState != null) {
            // Get recipe from saved instance if parcel exists.
            recipeStep = savedInstanceState.getParcelable("mRecipeStep");
        }

        previousStepBtn.setOnClickListener(this);
        nextStepBtn.setOnClickListener(this);

        // Return the layout for this fragment
        return listFragmentView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (playerView != null) {
            playerView.onPause();
        }
        releasePlayer();
        //TODO: Resolve any onPause stuff here.
        Log.v(LOG_TAG, "TEST: ON PAUSE CALLED");
    }

    @Override
    public void onResume() {
        super.onResume();
        if(recipesViewModel == null) {
            this.configureViewModel();
            getTotalSteps();
        }
        // If we paused this activity we can be pretty sure our observer was destroyed.
        // We must restart our observer.
        this.configureObservers();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (playerView != null) {
            playerView.onPause();
        }
        releasePlayer();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.configureDagger();

        if(recipesViewModel == null) {
            this.configureViewModel();
            this.configureObservers();
            getTotalSteps();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (recipeStep != null){
            outState.putParcelable("mRecipeStep", recipeStep);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * A simple helper method to configure our view model.
     * Let's return two observables. One, which accesses our data. The other returns network status.
     */
    private void configureViewModel() {

        // Get our view model provider.
        recipesViewModel = ViewModelProviders.of(getActivity(),
                recipesViewModelFactory).get(RecipesViewModel.class);

    }

    private void configureObservers() {
        recipesViewModel.getSelectedRecipeStep().removeObservers(this);

        recipesViewModel.getSelectedRecipeStep().observe(this, new Observer<RecipeStep>() {
            @Override
            public void onChanged(@Nullable RecipeStep selectedRecipeStep) {

                if(selectedRecipeStep != null) {
                    Log.v(LOG_TAG, "Observer Changed: " +selectedRecipeStep.getDescription());
                    recipeStep = selectedRecipeStep;
                    testTextTv.setText(selectedRecipeStep.getShortDescription());

                    if(!selectedRecipeStep.getVideoURL().isEmpty()) {
                        mediaUri = Uri.parse(selectedRecipeStep.getVideoURL());
                            if (!hasOnGoingMediaSession) {
                                Log.v(LOG_TAG, "Vid: Loading this URI: " + mediaUri.toString());
                                playerView.setVisibility(View.VISIBLE);
                                initializePlayer();
                                prepareMediaSource(mediaUri);
                            } else {
                                initializePlayer();
                                if (playerView != null) {
                                    playerView.onResume();
                                }

                            }
                            //TODO: Check network connectivity
                    } else {
                        //TODO: Should we release player here?
                        //TODO: Add image to testing JSON to test image loading here

                        resetPlayer();
                        playerView.setVisibility(View.GONE);
                        Log.v(LOG_TAG, "Vid: No valid URI for this recipe step.");
                    }
                }
            }
        });
    }

    private void getTotalSteps() {

        Recipe currentRecipe = recipesViewModel.getSelectedRecipe().getValue();

        if(currentRecipe != null) {
            totalSteps = currentRecipe.getRecipeSteps().size();
        }
    }

    /////// [ Media Player Related Methods ] ///////

    //TODO: Figure out how to inject EXO Player. Blerghhhhh. .¬__.¬
    /**
     * Initialize ExoPlayer.
     */
    private void initializePlayer() {
        if (mExoPlayer != null) {
            resetPlayer();
        } else {
            //Create an instance of the ExoPlayer.

            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();

            mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                    getActivity(), new DefaultRenderersFactory(getActivity()), trackSelector, loadControl);

            attachPlayerView();

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(new Player.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {
                    Log.v(LOG_TAG, "Vid: Loading changed: " + isLoading);
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    Log.v(LOG_TAG, "Vid: Player State Changed:");
                }
            });
        }
    }

    private void prepareMediaSource(Uri mediaUri) {

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(),
                Util.getUserAgent(getActivity(), this.getString(R.string.app_name)));

        // This is the MediaSource representing the media to be played.
        videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(mediaUri);
        mExoPlayer.prepare(videoSource);
        mExoPlayer.setPlayWhenReady(true);
    }

    /**
     * Simple method to attach the Expo Player to our Player View
     */
    public void attachPlayerView(){
        playerView.setPlayer(mExoPlayer);
    }

    private void resetPlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop(true);
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if(mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    /**
     * A simple helper method for setting up dagger with this fragment.
     */
    private void configureDagger(){
        AndroidSupportInjection.inject(this);
    }
}