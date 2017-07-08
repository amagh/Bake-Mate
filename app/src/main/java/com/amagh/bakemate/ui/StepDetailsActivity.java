package com.amagh.bakemate.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import com.amagh.bakemate.R;
import com.amagh.bakemate.adapters.StepSectionAdapter;
import com.amagh.bakemate.data.RecipeProvider;
import com.amagh.bakemate.databinding.ActivityStepDetailsBinding;
import com.amagh.bakemate.models.Step;
import com.amagh.bakemate.utils.ManageSimpleExoPlayerInterface;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

import java.util.Arrays;

import static com.amagh.bakemate.ui.MediaSourceActivity.SavedInstanceStateKeys.CURRENT_POSITION_KEY;
import static com.amagh.bakemate.ui.MediaSourceActivity.SavedInstanceStateKeys.PREVIOUS_CONFIGURATION_KEY;
import static com.amagh.bakemate.ui.StepDetailsActivity.BundleKeys.STEPS_KEY;
import static com.amagh.bakemate.ui.StepDetailsActivity.BundleKeys.STEP_ID;

public class StepDetailsActivity extends MediaSourceActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, ManageSimpleExoPlayerInterface {
    // **Constants** //
    private static final String TAG = StepDetailsActivity.class.getSimpleName();
    private static final int STEP_CURSOR_LOADER1 = 6587;

    interface BundleKeys {
        String STEP_ID          = "step_id";
        String STEPS_KEY        = "steps";
    }

    // **Member Variables** //
    private Uri mStepsUri;
    private Cursor mCursor;
    private StepSectionAdapter mPagerAdapter;
    private ActivityStepDetailsBinding mBinding;
    private PageChangeListener mPageChangeListener;
    private SimpleExoPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_step_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Retrieve the URI passed in the Intent as well as the page to start at
        Intent intent = getIntent();
        if (intent.getData() != null) {
            mStepsUri = intent.getData();
        } else {
            Log.d(TAG, "No URI passed");
        }

        if (savedInstanceState != null) {
            @RecipeDetailsActivity.LayoutConfiguration int previousConfig =
                    savedInstanceState.getInt(PREVIOUS_CONFIGURATION_KEY);

            // Check whether a layout configuration change has occurred
            if (previousConfig == RecipeDetailsActivity.LayoutConfiguration.SINGLE_PANEL &&
                    mLayoutConfig == RecipeDetailsActivity.LayoutConfiguration.MASTER_DETAIL_FLOW) {

                // Switching from single panel layout to master-detail flow, launch the
                // RecipeDetailsActivity and pre-load the current Step in the details pane
                long recipeId = RecipeProvider.getRecipeIdFromUri(mStepsUri);
                Uri recipeUri = RecipeProvider.Recipes.withId(recipeId);

                // Generate an Intent to be used to either start a new RecipeDetailsActivity if
                // there is no Calling Activity or as a result if there is
                Intent recipeDetailsIntent = new Intent(this, RecipeDetailsActivity.class);
                recipeDetailsIntent.setData(recipeUri);
                recipeDetailsIntent.putExtra(CURRENT_POSITION_KEY, mCurrentPosition);
                recipeDetailsIntent.putExtra(BundleKeys.STEPS_KEY, mSteps);

                // Check if this Activity was started for result
                if (getCallingActivity() != null) {
                    // Called from startActivityForResult
                    setResult(Activity.RESULT_OK, recipeDetailsIntent);
                } else {
                    // Called from StartActivity
                    startActivity(recipeDetailsIntent);
                }

                // Close this Activity to release resources and prevent errors if a second
                // configuration change occurs
                finish();
            }
        } else {
            // Position that the user selected
            mCurrentPosition = (int) intent.getLongExtra(STEP_ID, 0);

            // Retrieve the Array of Steps from the Intent if it is included
            if (intent.hasExtra(STEPS_KEY)) {
                Parcelable[] parcelables = intent.getParcelableArrayExtra(STEPS_KEY);
                mSteps = Arrays.copyOf(parcelables, parcelables.length, Step[].class);
            }
        }

        mPagerAdapter = new StepSectionAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mBinding.stepDetailsVp.setAdapter(mPagerAdapter);
        mBinding.stepDetailsViewPagerTs.setViewPager(mBinding.stepDetailsVp);
        mBinding.stepDetailsViewPagerTs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // Set the current position whenever the user changes it so it can be persisted
                // through state changes
                mCurrentPosition = position;

                // Notify registered PageChangeListener of page change
                if (mPageChangeListener != null) {
                    mPageChangeListener.onPageChanged(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Initialize the SimpleExoPlayer to be shared among all Fragments in the ViewPager
        mPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());

        if (mSteps != null) {
            int numSteps= 0;
            for (Step step : mSteps) {
                if (step != null) {
                    numSteps++;
                }
            }
            Log.d(TAG, "mSteps contains " + numSteps + " Steps");
        }

        // Init the CursorLoader for the Steps
        getSupportLoaderManager().initLoader(STEP_CURSOR_LOADER1, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                mStepsUri,
                StepSectionAdapter.StepProjection.STEP_PROJECTION,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Set the member variable and swap in the Cursor
        mCursor = data;

        if (mSteps == null) {
            // Initialize the Array to hold the Steps
            mSteps = new Step[data.getCount()];
        }

        mPagerAdapter.swapCursor(mCursor);

        // Move to the page the user selected
        mBinding.stepDetailsVp.setCurrentItem(mCurrentPosition);

        // Seek to the same time in the video
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public interface PageChangeListener {
        void onPageChanged(int currentPage);
    }

    /**
     * Registers a PageChangeListener to be notified when the user changes pages
     *
     * @param pageChangeListener PageChangeListener to be registered
     */
    public void setPageChangeCallBack(PageChangeListener pageChangeListener) {
        mPageChangeListener = pageChangeListener;
    }

    @Override
    public SimpleExoPlayer getPlayer() {
        return mPlayer;
    }

    /**
     * Retrieves the StepSectionAdapter used by this Activity's ViewPager
     *
     * @return The StepSectionAdapter used by this Activity's ViewPager
     */
    public StepSectionAdapter getPagerAdapter() {
        return mPagerAdapter;
    }

    /**
     * Returns the Array describing the Steps
     *
     * @return The Array describing the Steps
     */
    public Step[] getSteps() {
        return mSteps;
    }

    /**
     * Retrieves the position of the currently displayed Fragment
     *
     * @return Position of the Fragment currently displayed
     */
    public int getCurrentPosition() {
        return mCurrentPosition;
    }
}
