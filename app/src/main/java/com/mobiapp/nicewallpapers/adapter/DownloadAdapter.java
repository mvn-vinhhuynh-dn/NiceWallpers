package com.mobiapp.nicewallpapers.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobiapp.nicewallpapers.R;
import com.mobiapp.nicewallpapers.model.PhotoGoup;
import com.mobiapp.nicewallpapers.util.ScreenUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DownloadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = DownloadAdapter.class.getName();

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public class ViewHolderItem extends RecyclerView.ViewHolder {

        private ImageView imgBackground;

        private ViewHolderItem(View v) {
            super(v);
            imgBackground = (ImageView) v.findViewById(R.id.imgBackground);
        }
    }

    public class ViewHolderHeader extends RecyclerView.ViewHolder {

        private TextView tvDateDownload;

        private ViewHolderHeader(View v) {
            super(v);
            tvDateDownload = (TextView) v.findViewById(R.id.tvDateDownload);
        }
    }

    private Context mContext;
    private List<PhotoGoup> mFiles;
    private int mWidthItem;
    private int mHeightItem;
    private View.OnClickListener mOnClickListener;

    public DownloadAdapter(Context context, List<PhotoGoup> files, View.OnClickListener onClickListener) {
        mContext = context;
        mFiles = files;
        mOnClickListener = onClickListener;
        mWidthItem = ((int) (ScreenUtil.getWidthScreen(context)
                - (4 * context.getResources().getDimension(R.dimen.padding_item)))) / 3;
        int widthScreen = ScreenUtil.getWidthScreen(context);
        int heightScreen = ScreenUtil.getHeightScreen(context);
        mHeightItem = mWidthItem * heightScreen / widthScreen;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_home, parent, false);
            return new ViewHolderItem(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_header_download, parent, false);
            return new ViewHolderHeader(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderItem) {
            final ViewHolderItem viewHolderItem = (ViewHolderItem) holder;
            viewHolderItem.itemView.getLayoutParams().height = mHeightItem;
            File file = mFiles.get(position).getFile();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.fromFile(file));
                if (null != bitmap) {
                    viewHolderItem.imgBackground.setImageBitmap(Bitmap.createScaledBitmap(bitmap,
                            mWidthItem, mHeightItem, false));
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "error convert bitmap: " + e);
            }
            viewHolderItem.itemView.setOnClickListener(mOnClickListener);
        } else if (holder instanceof ViewHolderHeader)

        {
            final ViewHolderHeader viewHolderHeader = (ViewHolderHeader) holder;
            viewHolderHeader.tvDateDownload.setText(mFiles.get(position).getDateCreated());
        }

    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    public boolean isPositionHeader(int position) {
        return null == mFiles.get(position).getFile();
    }
}
