package com.mobiapp.nicewallpapers.fragments;

import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobiapp.nicewallpapers.MainActivity;
import com.mobiapp.nicewallpapers.R;
import com.mobiapp.nicewallpapers.SimpleImageActivity_;
import com.mobiapp.nicewallpapers.adapter.DownloadAdapter;
import com.mobiapp.nicewallpapers.ads.AdMobBannerHelper;
import com.mobiapp.nicewallpapers.asynctask.ScanDownLoadFiles;
import com.mobiapp.nicewallpapers.eventbus.BusProvider;
import com.mobiapp.nicewallpapers.eventbus.ImageRefreshEvent;
import com.mobiapp.nicewallpapers.model.PhotoGoup;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_download)
public class BlurFragment extends BaseFragment {

    @ViewById(R.id.recyclerViewDownload)
    protected RecyclerView mRecyclerViewDownload;
    @ViewById(R.id.frameLayoutAds)
    protected FrameLayout mFrameLayoutAds;
    @ViewById(R.id.viewLoadFailed)
    protected LinearLayout mViewLoadFailed;
    @ViewById(R.id.tvTitleLoadFailed)
    protected TextView mTvTitleLoadFailed;

    private DownloadAdapter mAdapter;
    private List<PhotoGoup> mFiles = new ArrayList<>();

    @Override
    protected void init() {
        mAdMobBannerHelper = new AdMobBannerHelper(getActivity(), mFrameLayoutAds);
        mAdMobBannerHelper.loadAds();
        BusProvider.getInstance().register(this);
        mAdapter = new DownloadAdapter(getActivity(), mFiles, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mRecyclerViewDownload.getChildAdapterPosition(v);
                int countFile = 0;
                List<PhotoGoup> files = new ArrayList<>();
                for (int i = 0; i < mFiles.size(); i++) {
                    if (mFiles.get(i).getFile() != null) {
                        files.add(mFiles.get(i));
                    } else {
                        if (i < position) {
                            countFile++;
                        }
                    }
                }

                SimpleImageActivity_.intent(getActivity())
                        .mCurrentPosition(position - countFile)
                        .mPhotos((ArrayList<PhotoGoup>) files).start();
            }
        });
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mAdapter.isPositionHeader(position) ? gridLayoutManager.getSpanCount() : 1;
            }
        });
        mRecyclerViewDownload.setLayoutManager(gridLayoutManager);
        mRecyclerViewDownload.setAdapter(mAdapter);
        requestData();

    }

    private void requestData() {
        String path = Environment.getExternalStorageDirectory().getPath() +
                "/" + getString(R.string.app_name) + "/" + getString(R.string.blur);
        new ScanDownLoadFiles(getActivity(), new ScanDownLoadFiles.OnScanDownloadFilesListener() {
            @Override
            public void onScanCompleted(List<PhotoGoup> result, List<String> dates) {
                if (result.size() != 0 && dates.size() != 0) {
                    mFiles.clear();
                    for (String date : dates) {
                        PhotoGoup photoHeader = new PhotoGoup();
                        photoHeader.setDateCreated(date);
                        mFiles.add(photoHeader);
                        for (PhotoGoup photo : result) {
                            if (date.equals(photo.getDateCreated())) {
                                mFiles.add(photo);
                            }
                        }
                    }
                    mAdapter.notifyDataSetChanged();

                    mRecyclerViewDownload.setVisibility(View.VISIBLE);
                    mViewLoadFailed.setVisibility(View.GONE);
                } else {
                    showLoadError(mViewLoadFailed, mTvTitleLoadFailed,
                            null, mRecyclerViewDownload, getString(R.string.no_wallpaper));
                }
            }
        }).execute(path);
    }

    @Subscribe
    public void updateRefresh(ImageRefreshEvent imageRefreshEvent) {
        mFiles.clear();
        requestData();
    }

    @Override
    public void onResume() {
        super.onResume();
        setHeader(getString(R.string.blur_history), MainActivity.HeaderBarType.TYPE_HOME, true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
    }
}
