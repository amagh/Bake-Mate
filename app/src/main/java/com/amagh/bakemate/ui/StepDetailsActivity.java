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

import java.util.ArrayList;
import java.util.List;

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
    private List<PageChangeCallBack> mPageChangeCallBacks;

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
            sCurrentPosition = (int) intent.getLongExtra(STEP_ID, 0);
        } else {
            Log.d(TAG, "No URI passed");
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
                sCurrentPosition = position;

                for (PageChangeCallBack pageChangeCallBack : mPageChangeCallBacks) {
                    pageChangeCallBack.onPageChanged(sCurrentPosition);
                }
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

    interface PageChangeCallBack {
        void onPageChanged(int currentPage);
    }

    /**
     * Registers a PageChangeCallBack to be notified when the user changes pages
     *
     * @param pageChangeCallBack PageChangeCallBack to be registered
     */
    void setPageChangeCallBack(PageChangeCallBack pageChangeCallBack) {
        // If the List of PageChangeCallBacks has not been initialized, init
        if (mPageChangeCallBacks == null) {
            mPageChangeCallBacks = new ArrayList<>();
        }

        // If the List does not contain the PageChangeCallBack to be registered, add it to the List
        // of CallBacks to notify
        if (!mPageChangeCallBacks.contains(pageChangeCallBack)) {
            mPageChangeCallBacks.add(pageChangeCallBack);
        }
    }

    /**
     * Removes a PageChangeCallBack from the List of PageChangeCallBacks to be notified
     *
     * @param pageChangeCallBack PageChangeCallBack to be removed
     */
    void removePageChangeCallBack(PageChangeCallBack pageChangeCallBack) {
        // If the List contains the PageChangeCallBack, remove it
        if (mPageChangeCallBacks.contains(pageChangeCallBack)) {
            mPageChangeCallBacks.remove(pageChangeCallBack);
        }
    }
}
