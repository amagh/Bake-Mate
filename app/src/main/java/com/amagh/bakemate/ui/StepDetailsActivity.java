package com.amagh.bakemate.ui;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amagh.bakemate.R;
import com.amagh.bakemate.adapters.StepSectionAdapter;
import com.amagh.bakemate.databinding.ActivityStepDetailsBinding;

import static com.amagh.bakemate.ui.StepDetailsActivity.BundleKeys.STEP_ID;

public class StepDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    // **Constants** //
    private static final String TAG = StepDetailsActivity.class.getSimpleName();
    private static final int STEP_CURSOR_LOADER1 = 6587;

    interface BundleKeys {
        String STEP_ID = "step_id";
    }

    // **Member Variables** //
    private Uri mStepsUri;
    private Cursor mCursor;
    private StepSectionAdapter mPagerAdapter;
    private ActivityStepDetailsBinding mBinding;

    public static int sCurrentPosition;

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

            // Position that the user selected
            sCurrentPosition = (int) intent.getLongExtra(STEP_ID, 0) - 1;
        } else {
            Log.d(TAG, "No URI passed");
        }

        mPagerAdapter = new StepSectionAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mBinding.stepDetailsVp.setAdapter(mPagerAdapter);
        mBinding.stepDetailsTs.setViewPager(mBinding.stepDetailsVp);
        mBinding.stepDetailsTs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // Set the current position whenever the user changes it so it can be persisted
                // through state changes
                sCurrentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
        mPagerAdapter.swapCursor(mCursor);

        // Move to the page the user selected
        mBinding.stepDetailsVp.setCurrentItem(sCurrentPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
