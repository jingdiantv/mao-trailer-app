package com.movie.maotrailer.ui.main.fragments.tv;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.movie.maotrailer.R;
import com.movie.maotrailer.api.ITMDBService;
import com.movie.maotrailer.api.NetworkState;
import com.movie.maotrailer.callback.IItemClickListener;
import com.movie.maotrailer.callback.IRetryListener;
import com.movie.maotrailer.data.remote.item.Results;
import com.movie.maotrailer.databinding.FragmentMutualBinding;
import com.movie.maotrailer.helper.Constants;
import com.movie.maotrailer.ui.detail.DetailActivity;
import com.movie.maotrailer.ui.main.fragments.MutualAdapter;
import com.movie.maotrailer.utils.ColumnUtils;
import com.movie.maotrailer.utils.SharedPreferencesUtil;

import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class TvFragment extends DaggerFragment implements IRetryListener, IItemClickListener<Results>, View.OnClickListener {

    private FragmentMutualBinding mFragmentMutualBinding;
    private MutualAdapter mMutualAdapter;
    private TvViewModel mTvViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    ColumnUtils columnUtils;

    @Inject
    SharedPreferencesUtil sharedPreferencesUtil;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        mFragmentMutualBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.fragment_mutual,
                container,
                false);

        return mFragmentMutualBinding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTvViewModel = ViewModelProviders.of(this, viewModelFactory).get(TvViewModel.class);

        int spanCount = columnUtils.getOptimalNumberOfColumn(getActivity());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), spanCount);
        mFragmentMutualBinding.rvMutual.setLayoutManager(layoutManager);

        mMutualAdapter = new MutualAdapter(this, this);

        mTvViewModel.setPreconditions(ITMDBService.Type.TV, ITMDBService.Category.TOP_RATED.getValue());
        fetchResults();

        mFragmentMutualBinding.tvErrButtonMutual.setOnClickListener(this);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.now_playing_sub_item).setVisible(false);
        menu.findItem(R.id.top_rated_sub_item).setVisible(true);

        Objects.requireNonNull(getActivity()).invalidateOptionsMenu();

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClicked = item.getItemId();

        if (itemThatWasClicked == R.id.top_rated_sub_item) {
            mTvViewModel.setPreconditions(ITMDBService.Type.TV, ITMDBService.Category.TOP_RATED.getValue());
            sharedPreferencesUtil.putStringData(getString(R.string.tv_category_key), ITMDBService.Category.TOP_RATED.getValue());
            refreshResults();
            return true;
        }
        if (itemThatWasClicked == R.id.popular_sub_item) {
            mTvViewModel.setPreconditions(ITMDBService.Type.TV, ITMDBService.Category.POPULAR.getValue());
            sharedPreferencesUtil.putStringData(getString(R.string.tv_category_key), ITMDBService.Category.POPULAR.getValue());
            refreshResults();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupIndicators() {
        mTvViewModel.getInitialLoading().observe(this, networkState -> {
            if (networkState != null) {
                if (networkState.getStatus() == NetworkState.Status.SUCCESS) {
                    mFragmentMutualBinding.pbMutual.setVisibility(View.GONE);
                    mFragmentMutualBinding.tvErrTextMutual.setVisibility(View.GONE);
                    mFragmentMutualBinding.tvErrButtonMutual.setVisibility(View.GONE);
                } else if (networkState.getStatus() == NetworkState.Status.FAILED) {
                    mFragmentMutualBinding.pbMutual.setVisibility(View.GONE);
                    mFragmentMutualBinding.tvErrTextMutual.setVisibility(View.VISIBLE);
                    mFragmentMutualBinding.tvErrButtonMutual.setVisibility(View.VISIBLE);
                    mFragmentMutualBinding.tvErrTextMutual.setText(getString(R.string.response_error_text));
                } else if (networkState.getStatus() == NetworkState.Status.NO_ITEM) {
                    mFragmentMutualBinding.pbMutual.setVisibility(View.GONE);
                    mFragmentMutualBinding.tvErrTextMutual.setVisibility(View.VISIBLE);
                    mFragmentMutualBinding.tvErrTextMutual.setText(getString(R.string.no_result_error_text));
                    mFragmentMutualBinding.tvErrButtonMutual.setVisibility(View.GONE);
                } else {
                    mFragmentMutualBinding.pbMutual.setVisibility(View.VISIBLE);
                    mFragmentMutualBinding.tvErrTextMutual.setVisibility(View.GONE);
                    mFragmentMutualBinding.tvErrButtonMutual.setVisibility(View.GONE);
                }
            }
        });

        mTvViewModel.getNetworkState().observe(this, networkState -> {
            if (networkState != null) {
                mMutualAdapter.setNetworkState(networkState);
            }
        });
    }

    private void fetchResults() {
        mTvViewModel.setNetworkState();
        setupIndicators();
        mTvViewModel.getResult().observe(this, results -> {
            if (results != null) {
                mMutualAdapter.submitList(null);
                mMutualAdapter.submitList(results);
            }
        });

        mFragmentMutualBinding.rvMutual.setAdapter(mMutualAdapter);
    }

    private void refreshResults() {
        mTvViewModel.setNetworkState();
        setupIndicators();
        mTvViewModel.setPreconditions(ITMDBService.Type.TV,
                sharedPreferencesUtil.getStringData(getString(R.string.tv_category_key),
                        ITMDBService.Category.TOP_RATED.getValue()));
        mTvViewModel.refreshResult().observe(this, results -> {
            if (results != null) {
                mMutualAdapter.submitList(null);
                mMutualAdapter.submitList(results);
            }
        });

        mFragmentMutualBinding.rvMutual.setAdapter(mMutualAdapter);
    }

    @Override
    public void onRefresh() {
        refreshResults();
    }

    @Override
    public void onItemClick(Results results, ImageView imageView) {
        Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
        detailIntent.putExtra(Constants.PARCELABLE_RESULTS, results);
        detailIntent.putExtra(Constants.TYPE_EXTRA, ITMDBService.Type.TV.getValue());
        detailIntent.putExtra(Constants.SHARED_ELEMENT_TRANSITION_INTENT_KEY, ViewCompat.getTransitionName(imageView));

        ActivityOptionsCompat option = ActivityOptionsCompat
                .makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()),
                        imageView,
                        ViewCompat.getTransitionName(imageView));

        startActivity(detailIntent, option.toBundle());
    }

    @Override
    public void onDestroy() {
        if (mTvViewModel != null) {
            mTvViewModel.onCleared();
        }

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvErrButtonMutual) {
            refreshResults();
        }
    }
}