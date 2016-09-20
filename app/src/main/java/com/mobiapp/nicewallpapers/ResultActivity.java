package com.mobiapp.nicewallpapers;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;

import com.mobiapp.nicewallpapers.asynctask.SaveWallpaperTask;
import com.mobiapp.nicewallpapers.eventbus.BusProvider;
import com.mobiapp.nicewallpapers.eventbus.SaveWallpapersCompleteEvent;
import com.mobiapp.nicewallpapers.util.BlurUtils;
import com.mobiapp.nicewallpapers.view.DiscreteSeekBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;

@EActivity(R.layout.activity_result)
public class ResultActivity extends AppCompatActivity {

    private static final String TAG = ResultActivity.class.getName();

    private static final int BLUR_RADIUS = 10;
    private static final int BLUR_DIM = 50;
    private static final int BLUR_GRAY = 0;

    @Extra
    protected String mPath;

    @ViewById(R.id.imgBlur)
    protected ImageView mImgBlur;
    @ViewById(R.id.seekBarBlur)
    protected DiscreteSeekBar mDiscreteSeekBar;
    @ViewById(R.id.seekBarDim)
    protected DiscreteSeekBar mDiscreteSeekBarDim;
    @ViewById(R.id.seekBarGray)
    protected DiscreteSeekBar mDiscreteSeekBarGray;

    private Bitmap mBitmap;

    private Bitmap mBitmapFirst;

    @AfterViews
    void init() {
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new
                    ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent)));
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        try {
            mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(mPath)));
            mBitmapFirst = mBitmap;
            if (null != mBitmap) {
                mBitmap = BlurUtils.getBlurBitmap(mBitmapFirst, BLUR_DIM, BLUR_RADIUS, BLUR_GRAY);
                mImgBlur.setImageBitmap(mBitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "error convert bitmap: " + e);
        }
        mDiscreteSeekBar.setMax(25);
        mDiscreteSeekBar.setMin(1);
        mDiscreteSeekBar.setProgress(BLUR_RADIUS);

        mDiscreteSeekBarGray.setMax(500);
        mDiscreteSeekBarGray.setProgress(BLUR_GRAY);

        mDiscreteSeekBarDim.setMax(150);
        mDiscreteSeekBarDim.setProgress(BLUR_DIM);

        mDiscreteSeekBar.setOnProgressChangeListener(onProgressChangeListener);
        mDiscreteSeekBarDim.setOnProgressChangeListener(onProgressChangeListener);
        mDiscreteSeekBarGray.setOnProgressChangeListener(onProgressChangeListener);
    }

    private DiscreteSeekBar.OnProgressChangeListener onProgressChangeListener = new DiscreteSeekBar.OnProgressChangeListener() {
        @Override
        public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
            if (null != mBitmap) {
                mBitmap = BlurUtils.getBlurBitmap(mBitmapFirst, mDiscreteSeekBarDim.getProgress(),
                        mDiscreteSeekBar.getProgress(), mDiscreteSeekBarGray.getProgress());
                mImgBlur.setImageBitmap(mBitmap);
            }
        }
    };

    @Click(R.id.actionSave)
    void clickSave() {
        new SaveWallpaperTask(ResultActivity.this, mBitmap, mPath,
                SaveWallpaperTask.TYPE_SAVE, onSaveWallpapersListener).execute();
    }

    @Click(R.id.actionSaveAndSet)
    void clickSaveAndSet() {
        new SaveWallpaperTask(ResultActivity.this, mBitmap, mPath,
                SaveWallpaperTask.TYPE_SAVE_AND_SET_WALLPAPER, onSaveWallpapersListener).execute();
    }

    private SaveWallpaperTask.OnSaveWallpapersListener onSaveWallpapersListener = new SaveWallpaperTask.OnSaveWallpapersListener() {
        @Override
        public void saveComplete() {
            BusProvider.getInstance().post(new SaveWallpapersCompleteEvent());
            finish();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }
}
