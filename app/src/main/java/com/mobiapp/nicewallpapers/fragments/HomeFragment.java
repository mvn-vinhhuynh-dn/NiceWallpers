package com.mobiapp.nicewallpapers.fragments;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobiapp.nicewallpapers.MainActivity;
import com.mobiapp.nicewallpapers.R;
import com.mobiapp.nicewallpapers.SimpleImageActivity_;
import com.mobiapp.nicewallpapers.adapter.HomeAdapter;
import com.mobiapp.nicewallpapers.ads.AdMobBannerHelper;
import com.mobiapp.nicewallpapers.api.ApiRequest;
import com.mobiapp.nicewallpapers.api.core.ApiCallback;
import com.mobiapp.nicewallpapers.api.core.ApiError;
import com.mobiapp.nicewallpapers.model.PhotoGoup;
import com.mobiapp.nicewallpapers.model.ResponePhoto;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;

@EFragment(R.layout.fragment_home)
public class HomeFragment extends BaseFragment {

    @ViewById(R.id.recyclerViewHome)
    RecyclerView mRecyclerViewHome;
    @ViewById(R.id.frameLayoutAds)
    FrameLayout mFrameLayoutAds;
    @ViewById(R.id.viewLoadFailed)
    protected LinearLayout mViewLoadFailed;
    @ViewById(R.id.tvTitleLoadFailed)
    protected TextView mTvTitleLoadFailed;

    private HomeAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;
    private ArrayList<PhotoGoup> mPhotos = new ArrayList<>();
    private int mCurrentPage = 1;

    @FragmentArg
    protected String mGroupId;
    @FragmentArg
    protected String mGoupName;

    @Override
    protected void init() {
        // Init ads
        mAdMobBannerHelper = new AdMobBannerHelper(getActivity(), mFrameLayoutAds);

        // Init
        mAdapter = new HomeAdapter(getActivity(), mPhotos, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mRecyclerViewHome.getChildAdapterPosition(v);
                SimpleImageActivity_.intent(getActivity())
                        .mCurrentPosition(position)
                        .mPhotos(mPhotos)
                        .mOemHomes(null)
                        .start();
            }
        });
        mGridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mAdapter.isPositionHeader(position) ? mGridLayoutManager.getSpanCount() : 1;
            }
        });
        mRecyclerViewHome.setLayoutManager(mGridLayoutManager);
        mRecyclerViewHome.setHasFixedSize(true);
        mRecyclerViewHome.setAdapter(mAdapter);
        requestData(mCurrentPage, true);
        mRecyclerViewHome.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int previousTotal = 0;
            private boolean loading = true;
            private int firstVisibleItem;
            private int visibleItemCount;
            private int totalItemCount;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = mRecyclerViewHome.getChildCount();
                totalItemCount = mGridLayoutManager.getItemCount();
                firstVisibleItem = mGridLayoutManager.findFirstVisibleItemPosition();
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= firstVisibleItem) {
                    requestData(mCurrentPage + 1, false);
                    loading = true;
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void requestData(int page, final boolean isShowProgress) {
        if (isShowProgress) {
            mKProgressHUD.show();
        }
        ApiRequest.getPhotos(page, mGroupId, new ApiCallback<ResponePhoto>() {
            @Override
            public void failure(RetrofitError retrofitError, ApiError apiError) {
                showLoadError(mViewLoadFailed, mTvTitleLoadFailed,
                        mKProgressHUD, mRecyclerViewHome, getString(R.string.can_not_load));
            }

            @Override
            public void success(ResponePhoto responePhoto, Response response) {
                if (null != mKProgressHUD) {
                    mKProgressHUD.dismiss();
                }
                if (null != responePhoto && null != responePhoto.getPhotos()) {
                    mCurrentPage = responePhoto.getPhotos().getPage();
                    if (responePhoto.getPhotos().getPhoto().size() != 0) {
                        mRecyclerViewHome.setVisibility(View.VISIBLE);
                        mViewLoadFailed.setVisibility(View.GONE);
                        mPhotos.addAll(responePhoto.getPhotos().getPhoto());
                        mAdapter.notifyDataSetChanged();
                    } else {
                        if (isShowProgress) {
                            showLoadError(mViewLoadFailed, mTvTitleLoadFailed,
                                    mKProgressHUD, mRecyclerViewHome, getString(R.string.can_not_load));
                        }
                    }
                } else {
                    showLoadError(mViewLoadFailed, mTvTitleLoadFailed,
                            mKProgressHUD, mRecyclerViewHome, getString(R.string.can_not_load));
                }
                if (isShowProgress) {
                    mAdMobBannerHelper.loadAds();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setHeader(mGoupName, MainActivity.HeaderBarType.TYPE_HOME, false);
    }
}
