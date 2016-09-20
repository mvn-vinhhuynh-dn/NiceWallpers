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
import com.mobiapp.nicewallpapers.adapter.RandomAdapter;
import com.mobiapp.nicewallpapers.ads.AdMobBannerHelper;
import com.mobiapp.nicewallpapers.model.PhotoGoup;
import com.mobiapp.nicewallpapers.model.PhotoSave;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@EFragment(R.layout.fragment_download)
public class RandomFragment extends BaseFragment {

    @ViewById(R.id.recyclerViewDownload)
    protected RecyclerView mRecyclerViewDownload;
    @ViewById(R.id.frameLayoutAds)
    protected FrameLayout mFrameLayoutAds;
    @ViewById(R.id.viewLoadFailed)
    protected LinearLayout mViewLoadFailed;
    @ViewById(R.id.tvTitleLoadFailed)
    protected TextView mTvTitleLoadFailed;

    private RandomAdapter mAdapter;
    private List<PhotoGoup> mPhotos = new ArrayList<>();
    private List<String> mDates = new ArrayList<>();

    @Override
    protected void init() {
        mAdMobBannerHelper = new AdMobBannerHelper(getActivity(), mFrameLayoutAds);
        mAdMobBannerHelper.loadAds();
        mPhotos.clear();
        List<PhotoSave> photoSaves = PhotoSave.listAll(PhotoSave.class);
        SimpleDateFormat formatter = new SimpleDateFormat("dd LLLL yyyy", Locale.getDefault());
        String currentDateandTime = formatter.format(new Date());

        for (PhotoSave photoSave : photoSaves) {
            String formattedDateString = photoSave.getDateCreated();

            if (!mDates.contains(photoSave.getDateCreated())) {
                if (formattedDateString.equals(currentDateandTime)) {
                    mDates.add(0, formattedDateString);
                } else {
                    mDates.add(formattedDateString);
                }
            }
        }
        for (String date : mDates) {
            PhotoGoup photoHeader = new PhotoGoup();
            photoHeader.setDateCreated(date);
            mPhotos.add(photoHeader);
            for (PhotoSave photoSave : photoSaves) {
                if (date.equals(photoSave.getDateCreated())) {
                    PhotoGoup photoItem = new PhotoGoup();
                    photoItem.setId(String.valueOf(photoSave.getId()));
                    photoItem.setSecret(photoSave.getSecret());
                    photoItem.setServer(photoSave.getServer());
                    photoItem.setFarm(photoSave.getFarm());
                    photoItem.setTitle(photoSave.getTitle());
                    photoItem.setIspublic(photoSave.getIspublic());
                    photoItem.setIsfriend(photoSave.getIsfriend());
                    photoItem.setIsfamily(photoSave.getIsfamily());
                    photoItem.setDateCreated(null);
                    mPhotos.add(photoItem);
                }
            }
        }
        if (mPhotos.size() == 0) {
            showLoadError(mViewLoadFailed, mTvTitleLoadFailed,
                    null, mRecyclerViewDownload, getString(R.string.no_wallpaper));
            return;
        }
        mRecyclerViewDownload.setVisibility(View.VISIBLE);
        mViewLoadFailed.setVisibility(View.GONE);
        mAdapter = new RandomAdapter(getActivity(), mPhotos, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mRecyclerViewDownload.getChildAdapterPosition(v);
                int countFile = 0;
                List<PhotoGoup> files = new ArrayList<>();
                for (int i = 0; i < mPhotos.size(); i++) {
                    if (mPhotos.get(i).getDateCreated() == null) {
                        files.add(mPhotos.get(i));
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
    }

    @Override
    public void onResume() {
        super.onResume();
        setHeader(getString(R.string.random_history), MainActivity.HeaderBarType.TYPE_HOME);
    }
}
