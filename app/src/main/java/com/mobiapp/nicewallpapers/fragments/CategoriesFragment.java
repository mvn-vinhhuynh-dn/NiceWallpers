package com.mobiapp.nicewallpapers.fragments;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobiapp.nicewallpapers.MainActivity;
import com.mobiapp.nicewallpapers.R;
import com.mobiapp.nicewallpapers.adapter.CategoriesAdapter;
import com.mobiapp.nicewallpapers.api.ApiRequest;
import com.mobiapp.nicewallpapers.api.XMLParsingDOM;
import com.mobiapp.nicewallpapers.api.core.ApiCallback;
import com.mobiapp.nicewallpapers.api.core.ApiError;
import com.mobiapp.nicewallpapers.model.Categories;
import com.mobiapp.nicewallpapers.model.CategoriesRequest;
import com.mobiapp.nicewallpapers.model.ChooseSources;
import com.mobiapp.nicewallpapers.model.ResponePhoto;
import com.mobiapp.nicewallpapers.util.ConnectionUtil;
import com.mobiapp.nicewallpapers.util.PreferenceUtil;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

@EFragment(R.layout.fragment_categories)
public class CategoriesFragment extends BaseFragment {

    @ViewById(R.id.recyclerViewCategories)
    protected RecyclerView mRecyclerViewCategories;
    @ViewById(R.id.viewLoadFailed)
    protected LinearLayout mViewLoadFailed;
    @ViewById(R.id.tvTitleLoadFailed)
    protected TextView mTvTitleLoadFailed;

    private List<Categories> mCategories = new ArrayList<>();
    private CategoriesAdapter mAdapter;

    private Handler mHandlerLocal = new Handler(Looper.getMainLooper());
    private int mCountFail;

    @Override
    protected void init() {
        mViewLoadFailed.setVisibility(View.GONE);
        mRecyclerViewCategories.setVisibility(View.VISIBLE);
        mAdapter = new CategoriesAdapter(getActivity(), mCategories, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mRecyclerViewCategories.getChildAdapterPosition(v);
                replaceFragment(HomeFragment_.builder()
                        .mGroupId(mCategories.get(position).getGroupId())
                        .mGoupName(mCategories.get(position).getTitle()).build(), false);
            }
        });
        mRecyclerViewCategories.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViewCategories.setAdapter(mAdapter);

        if (mCategories.size() != 0) {
            if (null != mKProgressHUD) {
                mKProgressHUD.dismiss();
            }
            mAdapter.notifyDataSetChanged();
        } else {
            loadCollection();
        }
    }

    private void loadCollection() {
        if (!ConnectionUtil.isConnected(getActivity())) {
            showLoadError(mViewLoadFailed, mTvTitleLoadFailed,
                    mKProgressHUD, mRecyclerViewCategories, getString(R.string.can_not_load));
            return;
        }
        if (null != mKProgressHUD) {
            mKProgressHUD.show();
        }
        XMLParsingDOM xmlParsingDOM = new XMLParsingDOM();
        xmlParsingDOM.parsingDOM(new XMLParsingDOM.OnXMLParsingDOMListener() {
            @Override
            public void parseResult(final List<CategoriesRequest> datas) {
                Runnable runnableLocal = new Runnable() {
                    @Override
                    public void run() {
                        if (datas.size() != 0) {
                            boolean isChooseSourceShow = PreferenceUtil.getBoolean(getActivity(),
                                    PreferenceUtil.CHOOSE_SOURCE_FIRST_SHOW, false);
                            if (!isChooseSourceShow) {
                                PreferenceUtil.saveBoolean(getActivity(),
                                        PreferenceUtil.CHOOSE_SOURCE_FIRST_SHOW, true);
                                saveChooseSources(datas);
                            }
                            for (CategoriesRequest categoriesRequest : datas) {
                                loadData(datas.size(), categoriesRequest);
                            }
                        } else {
                            showLoadError(mViewLoadFailed, mTvTitleLoadFailed,
                                    mKProgressHUD, mRecyclerViewCategories, getString(R.string.can_not_load));
                        }
                    }
                };
                mHandlerLocal.postDelayed(runnableLocal, 100);
            }
        });
    }

    private void saveChooseSources(List<CategoriesRequest> datas) {
        //download
        ChooseSources chooseSourcesDownload = new ChooseSources();
        chooseSourcesDownload.setId((long) 0);
        chooseSourcesDownload.setChecked(true);
        chooseSourcesDownload.setTitle(getString(R.string.download));
        chooseSourcesDownload.save();
        //download
        ChooseSources chooseSourcesBlur = new ChooseSources();
        chooseSourcesBlur.setId((long) 1);
        chooseSourcesBlur.setChecked(false);
        chooseSourcesBlur.setTitle(getString(R.string.blur_history));
        chooseSourcesBlur.save();
        for (int i = 0; i < datas.size(); i++) {
            CategoriesRequest categoriesRequest = datas.get(i);
            ChooseSources chooseSources = new ChooseSources();
            chooseSources.setId((long) (i + 2));
            if (categoriesRequest.getTitle().equals(getString(R.string.cars_girl))
                    || categoriesRequest.getTitle().equals(getString(R.string.editor_choice))
                    || categoriesRequest.getTitle().equals(getString(R.string.material_wallpapers))
                    || categoriesRequest.getTitle().equals(getString(R.string.animals))) {
                chooseSources.setChecked(true);
            } else {
                chooseSources.setChecked(false);
            }
            chooseSources.setTitle(categoriesRequest.getTitle());
            chooseSources.save();
        }
    }

    private void loadData(final int countCategories, final CategoriesRequest categoriesRequest) {
        mCountFail = 0;
        ApiRequest.getPhotosCategories(categoriesRequest.getGroupId(), new ApiCallback<ResponePhoto>() {
            @Override
            public void failure(RetrofitError retrofitError, ApiError apiError) {
                mCountFail++;
                if (mCountFail == countCategories) {
                    showLoadError(mViewLoadFailed, mTvTitleLoadFailed,
                            mKProgressHUD, mRecyclerViewCategories, getString(R.string.can_not_load));
                }
            }

            @Override
            public void success(ResponePhoto responePhoto, Response response) {
                if (null != responePhoto && null != responePhoto.getPhotos()) {
                    if (responePhoto.getPhotos().getPhoto().size() != 0) {
                        mCategories.add(new Categories(categoriesRequest.getGroupId(),
                                categoriesRequest.getTitle(),
                                responePhoto.getPhotos().getPhoto().get(0)));
                        mAdapter.notifyDataSetChanged();
                    }
                }
                if (null != mKProgressHUD) {
                    mKProgressHUD.dismiss();
                }
                mRecyclerViewCategories.setVisibility(View.VISIBLE);
                mViewLoadFailed.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setHeader(getString(R.string.app_name), MainActivity.HeaderBarType.TYPE_HOME, true);
    }
}
