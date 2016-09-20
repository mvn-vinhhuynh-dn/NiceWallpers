package com.mobiapp.nicewallpapers.asynctask;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.mobiapp.nicewallpapers.R;
import com.mobiapp.nicewallpapers.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveWallpaperTask extends AsyncTask<Void, Void, Bitmap> {

    public static final int TYPE_SAVE = 0;
    public static final int TYPE_SAVE_AND_SET_WALLPAPER = 1;

    private Context mContext;
    private Bitmap mBitmap;
    private String mPath;
    private int mType;
    private KProgressHUD mKProgressHUD;
    private OnSaveWallpapersListener mOnSaveWallpapersListener;

    public SaveWallpaperTask(Context context, Bitmap bitmap, String path,
                             int type, OnSaveWallpapersListener onSaveWallpapersListener) {
        mContext = context;
        mBitmap = bitmap;
        mPath = path;
        mType = type;
        mOnSaveWallpapersListener = onSaveWallpapersListener;
        mKProgressHUD = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(context.getString(R.string.please_wait))
                .setCancellable(true);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (null != mKProgressHUD) {
            mKProgressHUD.show();
        }
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        return saveBitmap(mContext, mPath, mBitmap);
    }

    private Bitmap saveBitmap(Context context, String path, Bitmap bitmap) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
            String pathSave = Environment.getExternalStorageDirectory()
                    + File.separator + "/" + context.getString(R.string.app_name)
                    + "/" + context.getString(R.string.blur);
            File folder = new File(pathSave);
            if (!folder.exists()) {
                folder.mkdir();
            }
            File f = new File(pathSave + "/" + new File(path).getName());

            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap aVoid) {
        super.onPostExecute(aVoid);
        if (null != mKProgressHUD) {
            mKProgressHUD.dismiss();
        }
        if (mType == TYPE_SAVE_AND_SET_WALLPAPER) {
            Utils.setWallpaper(mContext, aVoid);
        }
        if (null != mOnSaveWallpapersListener) {
            mOnSaveWallpapersListener.saveComplete();
        }
    }

    public interface OnSaveWallpapersListener {
        void saveComplete();
    }
}
