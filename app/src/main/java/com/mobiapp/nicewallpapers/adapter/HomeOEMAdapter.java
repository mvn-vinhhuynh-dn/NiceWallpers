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
import com.mobiapp.nicewallpapers.model.OEMHome;
import com.mobiapp.nicewallpapers.util.ScreenUtil;

import java.util.ArrayList;

public class HomeOEMAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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

    private ArrayList<OEMHome> mPhotos;
    private Context mContext;
    private int mHeightItem;
    private View.OnClickListener mOnClickListener;

    public HomeOEMAdapter(Context context, ArrayList<OEMHome> photos, View.OnClickListener onClickListener) {
        mContext = context;
        mPhotos = photos;
        mOnClickListener = onClickListener;
        int widthItem = ((int) (ScreenUtil.getWidthScreen(context)
                - (4 * context.getResources().getDimension(R.dimen.padding_item)))) / 3;
        int widthScreen = ScreenUtil.getWidthScreen(context);
        int heightScreen = ScreenUtil.getHeightScreen(context);
        mHeightItem = widthItem * heightScreen / widthScreen;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_header_download, parent, false);
            return new ViewHolderHeader(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_home, parent, false);
            return new ViewHolderItem(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OEMHome oemHome = mPhotos.get(position);
        if (holder instanceof ViewHolderHeader) {
            final ViewHolderHeader viewHolderHeader = (ViewHolderHeader) holder;
            viewHolderHeader.tvDateDownload.setText(oemHome.getHeader());
        } else if (holder instanceof ViewHolderItem) {
            final ViewHolderItem viewHolderItem = (ViewHolderItem) holder;
            viewHolderItem.itemView.getLayoutParams().height = mHeightItem;
            String urlImage = String.format(mContext.getString(R.string.base_url_oem_photo),
                    oemHome.getOemDetail().getThumb());
            Glide.with(mContext).load(urlImage)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop()
                    .dontAnimate()
                    .into(viewHolderItem.imgBackground);
            viewHolderItem.itemView.setOnClickListener(mOnClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    public boolean isPositionHeader(int position) {
        return mPhotos.get(position).getOemDetail() == null;
    }
}
