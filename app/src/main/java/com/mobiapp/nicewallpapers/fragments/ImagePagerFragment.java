package com.mobiapp.nicewallpapers.fragments;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.mobiapp.nicewallpapers.MainActivity;
import com.mobiapp.nicewallpapers.R;
import com.mobiapp.nicewallpapers.ResultActivity_;
import com.mobiapp.nicewallpapers.SimpleImageActivity;
import com.mobiapp.nicewallpapers.ads.AdMobBannerHelper;
import com.mobiapp.nicewallpapers.api.ApiRequest;
import com.mobiapp.nicewallpapers.api.core.ApiCallback;
import com.mobiapp.nicewallpapers.api.core.ApiError;
import com.mobiapp.nicewallpapers.eventbus.BusProvider;
import com.mobiapp.nicewallpapers.eventbus.ImageRefreshEvent;
import com.mobiapp.nicewallpapers.model.OEMDetail;
import com.mobiapp.nicewallpapers.model.OEMHome;
import com.mobiapp.nicewallpapers.model.PhotoGoup;
import com.mobiapp.nicewallpapers.model.PhotoSize;
import com.mobiapp.nicewallpapers.util.Utils;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

@EFragment(R.layout.fragment_image_pager)
public class ImagePagerFragment extends BaseFragment {

    private static final String[] LOCATION_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST = 1337;

    @ViewById(R.id.pager)
    protected ViewPager mViewPager;
    @ViewById(R.id.frameLayoutAds)
    protected FrameLayout mFrameLayoutAds;

    @FragmentArg
    protected int mCurrentPosition;
    @FragmentArg
    protected ArrayList<PhotoGoup> mPhotos = new ArrayList<>();
    @FragmentArg
    protected ArrayList<OEMHome> mOemHomes = new ArrayList<>();

    private long mEnqueue;
    private DownloadManager mDownloadManager;

    private String mFileName;
    private Uri mUri;
    private int mType;

    private BroadcastReceiver mBroadcastReceiver;
    private ImageAdapter mAdapter;

    private KProgressHUD mKProgressHUD;
    private int mTypeDownload;

    @Override
    protected void init() {
        mAdMobBannerHelper = new AdMobBannerHelper(getActivity(), mFrameLayoutAds);
        mAdMobBannerHelper.loadAds();
        mAdapter = new ImageAdapter(getChildFragmentManager(), mPhotos, mOemHomes);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentPosition);

        mKProgressHUD = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(getString(R.string.please_wait))
                .setCancellable(true);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    if (null != mKProgressHUD) {
                        mKProgressHUD.dismiss();
                    }
                    long downloadId = intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(mEnqueue);
                    Cursor c = mDownloadManager.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                            String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            mUri = Uri.parse(uriString);
                            if (mType == SimpleImageActivity.TYPE_DOWNLOAD) {
                                String servicestring = Context.DOWNLOAD_SERVICE;
                                DownloadManager downloadmanager = (DownloadManager)
                                        getActivity().getSystemService(servicestring);
                                if (mOemHomes == null || mOemHomes.size() == 0) {
                                    downloadmanager.addCompletedDownload(mFileName + ".jpg",
                                            getString(R.string.download_complete), true, "image/*", mUri.getPath(), downloadId, true);
                                } else {
                                    downloadmanager.addCompletedDownload(mFileName,
                                            getString(R.string.download_complete), true, "image/*", mUri.getPath(), downloadId, true);
                                }
                            } else if (mType == SimpleImageActivity.TYPE_USE_AS) {
                                Utils.setIntentWappaper(getActivity(), mUri);
                            } else if (mType == SimpleImageActivity.TYPE_SET_WALLPAPERS) {
                                Utils.setWallpapersFromUri(getActivity(), mUri);
                            } else {
                                ResultActivity_.intent(getActivity()).mPath(mUri.getPath()).start();
                            }
                        }
                    }
                }
            }
        };

        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private class ImageAdapter extends FragmentStatePagerAdapter {

        private List<PhotoGoup> mPhotos;
        private List<OEMHome> mOemHomes;

        public ImageAdapter(FragmentManager fm, List<PhotoGoup> photos, List<OEMHome> oemHomes) {
            super(fm);
            mPhotos = photos;
            mOemHomes = oemHomes;
        }

        @Override
        public int getCount() {
            if (mOemHomes == null || mOemHomes.size() == 0) {
                return mPhotos.size();
            } else {
                return mOemHomes.size();
            }
        }

        @Override
        public Fragment getItem(int position) {
            if (mOemHomes == null || mOemHomes.size() == 0) {
                PhotoGoup photo = mPhotos.get(position);
                return ImageDetailFragment_.builder().mPhoto(photo).mOemDetail(null).build();
            } else {
                OEMHome oemHome = mOemHomes.get(position);
                return ImageDetailFragment_.builder().mPhoto(null).mOemDetail(oemHome.getOemDetail()).build();
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    public void clickDownload(int type) {
        mTypeDownload = type;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!canAccessLocation()) {
                requestPermissions();
            } else {
                downloadRequest();
            }
        } else {
            downloadRequest();
        }
    }

    private void download(String url, String path, String fileName, int type, boolean isOEM) {
        url = url.replaceAll(" ", "%20");
        if (type != SimpleImageActivity.TYPE_DOWNLOAD && null != mKProgressHUD) {
            mKProgressHUD.show();
        }
        mDownloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        if (isOEM) {
            request.setDestinationInExternalPublicDir(path, fileName);
        } else {
            request.setDestinationInExternalPublicDir(path, fileName + ".jpg");
        }
        mEnqueue = mDownloadManager.enqueue(request);
    }

    public void clickDelete() {
        File file = mPhotos.get(mCurrentPosition).getFile();
        if (file == null) {
            return;
        }
        file.delete();
        mPhotos.remove(mCurrentPosition);
        mAdapter.notifyDataSetChanged();
        mCurrentPosition = mViewPager.getCurrentItem();
        BusProvider.getInstance().post(new ImageRefreshEvent());
        if (mPhotos.size() == 0) {
            getActivity().finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mOemHomes == null || mOemHomes.size() == 0) {
            if (null == mPhotos.get(mCurrentPosition).getFile()) {
                setHeader("", MainActivity.HeaderBarType.TYPE_DOWNLOAD_AND_VIEW);
            } else {
                setHeader("", MainActivity.HeaderBarType.TYPE_DOWNLOAD);
            }
        } else {
            setHeader("", MainActivity.HeaderBarType.TYPE_DOWNLOAD_AND_VIEW);
        }
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    private boolean canAccessLocation() {
        return (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    private boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && (PackageManager.PERMISSION_GRANTED == getActivity().checkSelfPermission(permission));
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity().requestPermissions(LOCATION_PERMS, WRITE_EXTERNAL_STORAGE_REQUEST);
        }
    }

    public void downloadRequest() {
        if (null != mPhotos && null != mPhotos.get(mCurrentPosition).getFile()) {
            Utils.setIntentWappaper(getActivity(), Uri.fromFile(mPhotos.get(mCurrentPosition).getFile()));
        } else {
            mType = mTypeDownload;
            int position = mViewPager.getCurrentItem();
            File f;
            String folder_main = getString(R.string.app_name);
            final String path = "/" + folder_main;
            if (mOemHomes == null || mOemHomes.size() == 0) {
                final PhotoGoup photo = mPhotos.get(position);
                mFileName = photo.getId() + photo.getSecret();
                f = new File(Environment.getExternalStorageDirectory(), folder_main + "/" + mFileName + ".jpg");
            } else {
                final OEMDetail oemDetail = mOemHomes.get(position).getOemDetail();
                mFileName = oemDetail.getCategory().trim() + oemDetail.getName();
                f = new File(Environment.getExternalStorageDirectory(), folder_main + "/" + mFileName);
            }

            if (f.exists()) {
                if (mTypeDownload == SimpleImageActivity.TYPE_DOWNLOAD) {
                    Toast.makeText(getActivity(), getString(R.string.has_been_download), Toast.LENGTH_LONG).show();
                } else if (mTypeDownload == SimpleImageActivity.TYPE_USE_AS) {
                    Utils.setIntentWappaper(getActivity(), Uri.fromFile(f));
                } else if (mTypeDownload == SimpleImageActivity.TYPE_SET_WALLPAPERS) {
                    Utils.setWallpapersFromUri(getActivity(), Uri.fromFile(f));
                } else {
                    ResultActivity_.intent(getActivity()).mPath(f.getPath()).start();
                }
                return;
            }
            if (mOemHomes == null || mOemHomes.size() == 0) {
                final PhotoGoup photo = mPhotos.get(position);
                ApiRequest.getPhotoSize(String.valueOf(photo.getId()), new ApiCallback<PhotoSize>() {
                            @Override
                            public void failure(RetrofitError retrofitError, ApiError apiError) {
                                // no op
                            }

                            @Override
                            public void success(PhotoSize photoSize, Response response) {
                                if (null == photoSize
                                        || null == photoSize.getSizes()
                                        || null == photoSize.getSizes().getSize()
                                        || photoSize.getSizes().getSize().size() == 0) {
                                    return;
                                }

                                String urlImage = photoSize.getSizes().getSize()
                                        .get(photoSize.getSizes().getSize().size() - 1).getSource();
                                download(urlImage, path, mFileName, mType, false);
                            }
                        }
                );
            } else {
                String urlImage = String.format(getString(R.string.base_url_oem_photo),
                        mOemHomes.get(position).getOemDetail().getLink());
                download(urlImage, path, mFileName, mType, true);
            }
        }
    }
}