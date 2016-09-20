package com.mobiapp.nicewallpapers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.mobiapp.nicewallpapers.ads.AdMobInterstitialHelper;
import com.mobiapp.nicewallpapers.eventbus.BusProvider;
import com.mobiapp.nicewallpapers.eventbus.SaveWallpapersCompleteEvent;
import com.mobiapp.nicewallpapers.fragments.BaseFragment;
import com.mobiapp.nicewallpapers.fragments.ImagePagerFragment;
import com.mobiapp.nicewallpapers.fragments.ImagePagerFragment_;
import com.mobiapp.nicewallpapers.model.OEMHome;
import com.mobiapp.nicewallpapers.model.PhotoGoup;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import java.util.ArrayList;


@EActivity(R.layout.content_main)
public class SimpleImageActivity extends AppCompatActivity implements BaseFragment.OnBaseFragmentListener {

    public static final int TYPE_DOWNLOAD = 0;
    public static final int TYPE_USE_AS = 1;
    public static final int TYPE_BLUR = 2;
    public static final int TYPE_SET_WALLPAPERS = 3;

    @Extra
    protected int mCurrentPosition;
    @Extra
    protected ArrayList<PhotoGoup> mPhotos = new ArrayList<>();
    @Extra
    protected ArrayList<OEMHome> mOemHomes = new ArrayList<>();

    private Fragment mFragment;
    private Menu mMenu;

    private AdMobInterstitialHelper mAdMobInterstitialHelper;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, getString(R.string.set_wallpaper_successfully), Toast.LENGTH_SHORT).show();
            mAdMobInterstitialHelper.showAds();
        }
    };

    @AfterViews
    void init() {
        BusProvider.getInstance().register(this);
        mAdMobInterstitialHelper = new AdMobInterstitialHelper(this);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new
                    ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent)));
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_image, menu);
        mMenu = menu;
        String tag = ImagePagerFragment_.class.getSimpleName();
        mFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (mFragment == null) {
            mFragment = ImagePagerFragment_.builder()
                    .mCurrentPosition(mCurrentPosition)
                    .mPhotos(mPhotos)
                    .mOemHomes(mOemHomes)
                    .build();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentFrame, mFragment, tag).commit();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_download:
                if (mFragment instanceof ImagePagerFragment_) {
                    ((ImagePagerFragment_) mFragment).clickDownload(TYPE_DOWNLOAD);
                }
                break;
            case R.id.action_use_as:
                if (mFragment instanceof ImagePagerFragment_) {
                    ((ImagePagerFragment_) mFragment).clickDownload(TYPE_USE_AS);
                }
                break;
            case R.id.action_blur:
                if (mFragment instanceof ImagePagerFragment_) {
                    ((ImagePagerFragment_) mFragment).clickDownload(TYPE_BLUR);
                }
                break;
            case R.id.action_delete:
                dialogDelete(SimpleImageActivity.this);
                break;
            case R.id.action_set_wallpapers:
                if (mFragment instanceof ImagePagerFragment_) {
                    ((ImagePagerFragment_) mFragment).clickDownload(TYPE_SET_WALLPAPERS);
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void dialogDelete(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.confirm_delete));
        builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mFragment instanceof ImagePagerFragment_) {
                    ((ImagePagerFragment_) mFragment).clickDelete();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void setTitleHeader(String title) {

    }

    @Override
    public void setTypeHeader(MainActivity.HeaderBarType type) {
        switch (type) {
            case TYPE_DOWNLOAD:
                if (null != mMenu) {
                    mMenu.findItem(R.id.action_download).setVisible(false);
                    mMenu.findItem(R.id.action_blur).setVisible(false);
                    mMenu.findItem(R.id.action_view).setVisible(true);
                    mMenu.findItem(R.id.action_delete).setVisible(true);
                }
                break;
            case TYPE_DOWNLOAD_AND_VIEW:
                if (null != mMenu) {
                    mMenu.findItem(R.id.action_download).setVisible(true);
                    mMenu.findItem(R.id.action_view).setVisible(true);
                    mMenu.findItem(R.id.action_blur).setVisible(true);
                    mMenu.findItem(R.id.action_delete).setVisible(false);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void setDrawerLayout(boolean isDraw) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_WALLPAPER_CHANGED);
        registerReceiver(mBroadcastReceiver, iFilter);
    }

    @Subscribe
    public void saveWallpapersComplete(SaveWallpapersCompleteEvent event) {
        mAdMobInterstitialHelper.showAds();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ImagePagerFragment.WRITE_EXTERNAL_STORAGE_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mFragment instanceof ImagePagerFragment_) {
                        ((ImagePagerFragment_) mFragment).downloadRequest();
                    }
                } else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
            }
        }
    }
}
