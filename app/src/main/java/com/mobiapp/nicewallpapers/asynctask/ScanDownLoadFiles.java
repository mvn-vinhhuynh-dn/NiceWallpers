package com.mobiapp.nicewallpapers.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.mobiapp.nicewallpapers.R;
import com.mobiapp.nicewallpapers.model.PhotoGoup;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScanDownLoadFiles extends AsyncTask<String, Integer, List<PhotoGoup>> {

    private Context mContext;
    private OnScanDownloadFilesListener mOnScanLargeFilesListener;
    private List<String> mDates = new ArrayList<>();

    public ScanDownLoadFiles(Context context, OnScanDownloadFilesListener onActionListener) {
        mContext = context;
        mOnScanLargeFilesListener = onActionListener;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected List<PhotoGoup> doInBackground(String... params) {
        List<PhotoGoup> filesResult = new ArrayList<>();
        File downloadDir = new File(params[0]);
        File files[] = downloadDir.listFiles();
        if (null == files) {
            return filesResult;
        }
        for (File file : files) {
            if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png")) {
                Date lastModDate = new Date(file.lastModified());
                SimpleDateFormat formatter = new SimpleDateFormat("dd LLLL yyyy", Locale.getDefault());
                String formattedDateString = formatter.format(lastModDate);

                String currentDateandTime = formatter.format(new Date());
                if (currentDateandTime.equals(formattedDateString)) {
                    formattedDateString = mContext.getString(R.string.to_day);
                }

                if (!mDates.contains(formattedDateString)) {
                    if (formattedDateString.equals(mContext.getString(R.string.to_day))) {
                        mDates.add(0, formattedDateString);
                    } else {
                        mDates.add(formattedDateString);
                    }
                }
                PhotoGoup photo = new PhotoGoup();
                photo.setDateCreated(formattedDateString);
                photo.setFile(file);
                filesResult.add(photo);
            }
        }
        return filesResult;
    }

    @Override
    protected void onPostExecute(List<PhotoGoup> result) {
        if (mOnScanLargeFilesListener != null) {
            mOnScanLargeFilesListener.onScanCompleted(result, mDates);
        }
    }

    public interface OnScanDownloadFilesListener {
        void onScanCompleted(List<PhotoGoup> result, List<String> dates);
    }
}
