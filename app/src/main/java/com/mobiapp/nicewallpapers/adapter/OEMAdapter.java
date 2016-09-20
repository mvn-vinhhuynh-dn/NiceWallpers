package com.mobiapp.nicewallpapers.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobiapp.nicewallpapers.R;
import com.mobiapp.nicewallpapers.model.OEM;

import java.util.List;

public class OEMAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public class ViewHolderItem extends RecyclerView.ViewHolder {

        private TextView tvTitleCategories;
        private ImageView imgBackgroundCategories;
        private ImageView imgIconCategories;

        private ViewHolderItem(View v) {
            super(v);
            imgBackgroundCategories = (ImageView) v.findViewById(R.id.imgBackgroundCategories);
            imgIconCategories = (ImageView) v.findViewById(R.id.imgIconCategories);
            tvTitleCategories = (TextView) v.findViewById(R.id.tvTitleCategories);
        }
    }

    private List<OEM> mOems;
    private View.OnClickListener mOnClickListener;
    private Context mContext;

    public OEMAdapter(Context context, List<OEM> oems, View.OnClickListener onClickListener) {
        mContext = context;
        mOems = oems;
        mOnClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_categories, parent, false);
        return new ViewHolderItem(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderItem) {
            final ViewHolderItem viewHolderItem = (ViewHolderItem) holder;
            OEM oem = mOems.get(position);

            String urlImage = String.format(mContext.getString(R.string.base_url_oem_photo), oem.getThumb());
            Glide.with(mContext).load(urlImage)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop()
                    .dontAnimate()
                    .into(viewHolderItem.imgBackgroundCategories);

            viewHolderItem.tvTitleCategories.setText(oem.getName());
            if (position % 2 == 0) {
                viewHolderItem.imgIconCategories.setBackgroundDrawable(ContextCompat.getDrawable(mContext,
                        R.drawable.bg_circle_red));
            } else {
                viewHolderItem.imgIconCategories.setBackgroundDrawable(ContextCompat.getDrawable(mContext,
                        R.drawable.bg_circle_blue));
            }
            viewHolderItem.imgIconCategories.setImageResource(R.drawable.ic_photo_album_white_24dp);
            viewHolderItem.itemView.setOnClickListener(mOnClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return mOems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
