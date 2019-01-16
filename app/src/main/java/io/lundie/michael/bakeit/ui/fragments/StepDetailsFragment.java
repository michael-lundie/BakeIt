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

import com.google.android.exoplayer2.C;
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
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;
import io.lundie.michael.bakeit.R;
import io.lundie.michael.bakeit.datamodel.models.Recipe;
import io.lundie.michael.bakeit.datamodel.models.RecipeStep;
import io.lundie.michael.bakeit.utilities.AppConstants;
import io.lundie.michael.bakeit.utilities.AppUtils;
import io.lundie.michael.bakeit.viewmodel.RecipesViewModel;

public class StepDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = StepDetailsFragment.class.getName();

    @Inject ViewModelProvider.Factory recipesViewModelFactory;

    private final static boolean ON_RESUME_TRUE = true;
    private final static boolean ON_RESUME_FALSE = false;

    private SimpleExoPlayer mExoPlayer;
    private MediaSource videoSource;
    private long mPlayerPosition;
    private int mPlayerWindow;
    private boolean mPlayWhenReady;

    private RecipesViewModel recipesViewModel;
    private RecipeStep recipeStep;
    private int totalSteps;
    private Uri mMediaUri;

    @BindView(R.id.detail_test_tv) TextView testTextTv;
    @BindView(R.id.detail_instruction_tv) TextView instructionsTv;
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

        // Inflate the layout for this fragment
        View listFragmentView =  inflater.inflate(R.layout.fragment_details, container, false);

        // Time to butter some toast... Bind view references with butterknife library.
        ButterKnife.bind(this, listFragmentView);

        if (savedInstanceState != null) {

            // Get recipe from saved instance if parcel exists.
            //TODO: Assign some variable constants
            recipeStep = savedInstanceState.getParcelable("mRecipeStep");
            // Get previously saved player position
            if (mMediaUri != null) {

                mMediaUri = Uri.parse(savedInstanceState.getString("mMediaUri"));
            }
            mPlayerPosition = savedInstanceState.getLong("mPlayerPosition");
            mPlayerWindow = savedInstanceState.getInt("mPlayerWindow");
            mPlayWhenReady = savedInstanceState.getBoolean("mPlayWhenReady");
        } else {
            mPlayerPosition = C.TIME_UNSET;
            mPlayerWindow = C.INDEX_UNSET;
            mPlayWhenReady = true;
        }

        previousStepBtn.setOnClickListener(this);
        nextStepBtn.setOnClickListener(this);

        // Return the layout for this fragment
        return listFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mExoPlayer != null) {
            exoPlayerOnSave();
            if (Util.SDK_INT <= 23) {
                if (playerView != null) {
                    playerView.onPause();
                }
                releasePlayer();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(recipesViewModel == null) {
            this.configureViewModel();
            this.configureObservers(ON_RESUME_TRUE);
            getTotalSteps();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause();
            }
        } releasePlayer();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.configureDagger();

        if(recipesViewModel == null) {
            this.configureViewModel();
            this.configureObservers(ON_RESUME_FALSE);
            getTotalSteps();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(mMediaUri != null) {
            outState.putString("mMediaUri", mMediaUri.toString());
        }
        outState.putLong("mPlayerPosition", mPlayerPosition);
        outState.putInt("mPlayerWindow", mPlayerWindow);
        outState.putBoolean("mPlayWhenReady", mPlayWhenReady);

        if (recipeStep != null){
            outState.putParcelable("mRecipeStep", recipeStep);
        }
        super.onSaveInstanceState(outState);
    }

    public void exoPlayerOnSave() {
        mPlayWhenReady = mExoPlayer.getPlayWhenReady();
        mPlayerWindow = mExoPlayer.getCurrentWindowIndex();
        mPlayerPosition = mExoPlayer.getCurrentPosition();
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

    private void configureObservers(Boolean onResume) {
        recipesViewModel.getSelectedRecipeStep().removeObservers(this);

        recipesViewModel.getSelectedRecipeStep().observe(this, new Observer<RecipeStep>() {
            @Override
            public void onChanged(@Nullable RecipeStep selectedRecipeStep) {

                if(selectedRecipeStep != null) {
                    Log.v(LOG_TAG, "Observer Changed: " +selectedRecipeStep.getDescription());
                    recipeStep = selectedRecipeStep;

                    String descriptionString = selectedRecipeStep.getShortDescription();
                    String instructionsString = selectedRecipeStep.getDescription();

                    if(!descriptionString.isEmpty()) {
                        testTextTv.setText(descriptionString);
                    } else {
                        testTextTv.setVisibility(View.INVISIBLE);
                    }

                    if(!instructionsString.isEmpty()) {
                        instructionsTv.setText(AppUtils.replaceNumberedDescription(instructionsString));
                    } else {
                        instructionsTv.setText(getResources().getString(R.string.details_no_instructions));
                    }

                    setNavigationButtons();

                    if(!selectedRecipeStep.getVideoURL().isEmpty()) {

                        Uri mediaUri = Uri.parse(selectedRecipeStep.getVideoURL());

                        if(mMediaUri != null && !mMediaUri.equals(mediaUri)) {
                            // Reset player to 0 if we are choosing a new recipe step.
                            mPlayerPosition = 0;
                            mPlayWhenReady = true;
                        }
                        mMediaUri = Uri.parse(selectedRecipeStep.getVideoURL());

                        Log.v(LOG_TAG, "Vid: Loading this URI: " + mMediaUri.toString());
                        playerView.setVisibility(View.VISIBLE);
                        mPlayWhenReady = true;
                        initializePlayer(mMediaUri);

                        if(onResume) {
                            if (playerView != null) {
                                playerView.onResume();
                            }
                        }
                    } else {
                        resetPlayer();
                        playerView.setVisibility(View.GONE);
                        Log.v(LOG_TAG, "Vid: No valid URI for this recipe step.");
                    }
                }
            }
        });
    }

    private void setNavigationButtons() {
        if(recipeStep.getStepNumber() == 0) {
            previousStepBtn.setVisibility(View.INVISIBLE);
        } else {
            previousStepBtn.setVisibility(View.VISIBLE);
        }

        if(recipeStep.getStepNumber().equals(totalSteps)) {
            nextStepBtn.setVisibility(View.INVISIBLE);
        }else {
            nextStepBtn.setVisibility(View.VISIBLE);
        }
    }

    private void getTotalSteps() {

        Recipe currentRecipe = recipesViewModel.getSelectedRecipe().getValue();

        if(currentRecipe != null) {
            totalSteps = currentRecipe.getRecipeSteps().size();
        }
    }

    /////// [ Media Player Related Methods ] ///////

    /**
     * Initialize ExoPlayer.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            //Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();

            mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                    getActivity(), new DefaultRenderersFactory(getActivity()), trackSelector, loadControl);

            attachPlayerView();

            mExoPlayer.setPlayWhenReady(mPlayWhenReady);
        }

        prepareMediaSource(mediaUri);

        // Restore the playback position
        boolean hasStartingPos = mPlayerWindow != C.INDEX_UNSET;
        if (hasStartingPos) {
            mExoPlayer.seekTo(mPlayerWindow, mPlayerPosition);
        }
        mExoPlayer.prepare(videoSource,!hasStartingPos, false);
    }

    private void prepareMediaSource(Uri mediaUri) {
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory =
                new DefaultHttpDataSourceFactory(Util.getUserAgent(getActivity(), this.getString(R.string.app_name)));

        // This is the MediaSource representing the media to be played.
        videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(mediaUri);
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