package com.mobiapp.nicewallpapers.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobiapp.nicewallpapers.MainActivity;
import com.mobiapp.nicewallpapers.R;
import com.mobiapp.nicewallpapers.adapter.OEMAdapter;
import com.mobiapp.nicewallpapers.api.core.ApiCallback;
import com.mobiapp.nicewallpapers.api.core.ApiError;
import com.mobiapp.nicewallpapers.model.OEM;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

@EFragment(R.layout.fragment_categories)
public class OEMFragment extends BaseOEMFragment {

    @ViewById(R.id.recyclerViewCategories)
    protected RecyclerView mRecyclerViewCategories;
    @ViewById(R.id.viewLoadFailed)
    protected LinearLayout mViewLoadFailed;
    @ViewById(R.id.tvTitleLoadFailed)
    protected TextView mTvTitleLoadFailed;

    private List<OEM> mOems = new ArrayList<>();
    private OEMAdapter mAdapter;

    @Override
    protected void init() {
        mAdapter = new OEMAdapter(getActivity(), mOems, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mRecyclerViewCategories.getChildAdapterPosition(v);
                replaceFragment(HomeOEMFragment_.builder()
                        .mName(mOems.get(position).getName()).build(), false);
            }
        });
        mRecyclerViewCategories.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViewCategories.setAdapter(mAdapter);

        mViewLoadFailed.setVisibility(View.GONE);
        mRecyclerViewCategories.setVisibility(View.VISIBLE);
        if (mOems.size() == 0) {
            requestData();
        } else {
            mAdapter.notifyDataSetChanged();
            mKProgressHUD.dismiss();
        }
    }

    private void requestData() {
        mKProgressHUD.show();
        mApiService.getCategoryOEM(new ApiCallback<List<OEM>>() {
            @Override
            public void failure(RetrofitError retrofitError, ApiError apiError) {
                showLoadError(mViewLoadFailed, mTvTitleLoadFailed,
                        mKProgressHUD, mRecyclerViewCategories, getString(R.string.can_not_load));
            }

            @Override
            public void success(List<OEM> oems, Response response) {
                mKProgressHUD.dismiss();
                if (oems.size() != 0) {
                    mOems.clear();
                    mOems.addAll(oems);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setHeader(getString(R.string.oem_wallpapers), MainActivity.HeaderBarType.TYPE_HOME, true);
    }
}
