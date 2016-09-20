package com.mobiapp.nicewallpapers.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobiapp.nicewallpapers.R;
import com.mobiapp.nicewallpapers.model.PhotoGoup;
import com.mobiapp.nicewallpapers.util.ScreenUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RandomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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

    private List<PhotoGoup> mPhotos;
    private int mHeightItem;
    private View.OnClickListener mOnClickListener;
    private String mCurrentDateandTime;
    private Context mContext;

    public RandomAdapter(Context context, List<PhotoGoup> files, View.OnClickListener onClickListener) {
        mContext = context;
        mPhotos = files;
        mOnClickListener = onClickListener;
        mHeightItem = ((int) (ScreenUtil.getWidthScreen(context)
                - (4 * context.getResources().getDimension(R.dimen.padding_item)))) / 3;
        SimpleDateFormat formatter = new SimpleDateFormat("dd LLLL yyyy", Locale.getDefault());
        mCurrentDateandTime = formatter.format(new Date());
        int mWidthItem = ((int) (ScreenUtil.getWidthScreen(context)
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
            PhotoGoup photo = mPhotos.get(position);
            String urlImage = String.format(mContext.getString(R.string.image_url),
                    photo.getFarm(), photo.getServer(), photo.getId(), photo.getSecret());
            Glide.with(mContext).load(urlImage)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop()
                    .dontAnimate()
                    .into(viewHolderItem.imgBackground);
            viewHolderItem.itemView.setOnClickListener(mOnClickListener);
        } else if (holder instanceof ViewHolderHeader) {
            final ViewHolderHeader viewHolderHeader = (ViewHolderHeader) holder;
            String dateCreate = mPhotos.get(position).getDateCreated();
            if (dateCreate.equals(mCurrentDateandTime)) {
                dateCreate = mContext.getString(R.string.to_day);
            }
            viewHolderHeader.tvDateDownload.setText(dateCreate);
        }
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    public boolean isPositionHeader(int position) {
        return null != mPhotos.get(position).getDateCreated();
    }
}
