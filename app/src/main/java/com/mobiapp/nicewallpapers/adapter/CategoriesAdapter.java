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
import com.mobiapp.nicewallpapers.model.Categories;
import com.mobiapp.nicewallpapers.model.PhotoGoup;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private class ViewHolderItem extends RecyclerView.ViewHolder {

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

    private List<Categories> mCategories;
    private View.OnClickListener mOnClickListener;
    private Context mContext;

    public CategoriesAdapter(Context context, List<Categories> categories, View.OnClickListener onClickListener) {
        mContext = context;
        mCategories = categories;
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
            Categories categories = mCategories.get(position);

            PhotoGoup photo = categories.getPhoto();
            String urlImage = String.format(mContext.getString(R.string.image_url),
                    photo.getFarm(), photo.getServer(), photo.getId(), photo.getSecret());

            Glide.with(mContext).load(urlImage)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop()
                    .dontAnimate()
                    .into(viewHolderItem.imgBackgroundCategories);

            viewHolderItem.tvTitleCategories.setText(categories.getTitle());
            if (position % 2 == 0) {
                viewHolderItem.imgIconCategories.setBackgroundDrawable(ContextCompat.getDrawable(mContext,
                        R.drawable.bg_circle_red));
            } else {
                viewHolderItem.imgIconCategories.setBackgroundDrawable(ContextCompat.getDrawable(mContext,
                        R.drawable.bg_circle_blue));
            }
            viewHolderItem.imgIconCategories.setImageResource(R.drawable.ic_wallpaper_white_24dp);
            viewHolderItem.itemView.setOnClickListener(mOnClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
