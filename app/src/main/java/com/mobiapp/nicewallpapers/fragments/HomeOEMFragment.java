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
import com.mobiapp.nicewallpapers.adapter.HomeOEMAdapter;
import com.mobiapp.nicewallpapers.ads.AdMobBannerHelper;
import com.mobiapp.nicewallpapers.api.core.ApiCallback;
import com.mobiapp.nicewallpapers.api.core.ApiError;
import com.mobiapp.nicewallpapers.model.OEMDetail;
import com.mobiapp.nicewallpapers.model.OEMHome;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

@EFragment(R.layout.fragment_home)
public class HomeOEMFragment extends BaseOEMFragment {

    @ViewById(R.id.recyclerViewHome)
    RecyclerView mRecyclerViewHome;
    @ViewById(R.id.frameLayoutAds)
    FrameLayout mFrameLayoutAds;
    @ViewById(R.id.viewLoadFailed)
    protected LinearLayout mViewLoadFailed;
    @ViewById(R.id.tvTitleLoadFailed)
    protected TextView mTvTitleLoadFailed;

    private HomeOEMAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;
    private ArrayList<OEMHome> mOemHomes = new ArrayList<>();

    @FragmentArg
    protected String mName;

    @Override
    protected void init() {
        mAdMobBannerHelper = new AdMobBannerHelper(getActivity(), mFrameLayoutAds);
        mAdapter = new HomeOEMAdapter(getActivity(), mOemHomes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mRecyclerViewHome.getChildAdapterPosition(v);
                int countFile = 0;
                ArrayList<OEMHome> oemHomes = new ArrayList<>();
                for (int i = 0; i < mOemHomes.size(); i++) {
                    OEMHome oemHome = mOemHomes.get(i);
                    if (oemHome.getOemDetail() != null) {
                        oemHomes.add(oemHome);
                    } else {
                        if (i < position) {
                            countFile++;
                        }
                    }
                }
                SimpleImageActivity_.intent(getActivity())
                        .mCurrentPosition(position - countFile)
                        .mPhotos(null)
                        .mOemHomes(oemHomes).start();
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
        requestData();
    }

    private void requestData() {
        mKProgressHUD.show();
        mApiService.getHomeOEM(mName, new ApiCallback<List<OEMDetail>>() {
                    @Override
                    public void failure(RetrofitError retrofitError, ApiError apiError) {
                        showLoadError(mViewLoadFailed, mTvTitleLoadFailed,
                                mKProgressHUD, mRecyclerViewHome, getString(R.string.can_not_load));
                    }

                    @Override
                    public void success(List<OEMDetail> oemDetails, Response response) {
                        if (oemDetails.size() != 0) {
                            Collections.reverse(oemDetails);
                            List<String> categorys = new ArrayList<>();
                            for (OEMDetail oemDetail : oemDetails) {
                                if (!categorys.contains(oemDetail.getCategory())) {
                                    categorys.add(oemDetail.getCategory());
                                }
                            }
                            for (String category : categorys) {
                                OEMHome oEMHomeHeader = new OEMHome();
                                oEMHomeHeader.setHeader(category);
                                oEMHomeHeader.setOemDetail(null);
                                mOemHomes.add(oEMHomeHeader);
                                for (OEMDetail oemDetail : oemDetails) {
                                    if (category.equals(oemDetail.getCategory())) {
                                        OEMHome oEMHomeItem = new OEMHome();
                                        oEMHomeItem.setHeader(null);
                                        oEMHomeItem.setOemDetail(oemDetail);
                                        mOemHomes.add(oEMHomeItem);
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            mKProgressHUD.dismiss();
                            mAdMobBannerHelper.loadAds();
                        } else {
                            showLoadError(mViewLoadFailed, mTvTitleLoadFailed,
                                    mKProgressHUD, mRecyclerViewHome, getString(R.string.can_not_load));
                        }
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        setHeader(mName, MainActivity.HeaderBarType.TYPE_HOME, false);
    }
}
