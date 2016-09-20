package com.mobiapp.nicewallpapers.adapter;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobiapp.nicewallpapers.R;
import com.mobiapp.nicewallpapers.model.DrawerItem;
import com.mobiapp.nicewallpapers.util.ScreenUtil;
import com.mobiapp.nicewallpapers.util.Utils;

import java.util.List;

public class DrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    public class ViewHolderHeader extends RecyclerView.ViewHolder {

        private ImageView imgDrawerHeader;

        private ViewHolderHeader(View v) {
            super(v);
            imgDrawerHeader = (ImageView) v.findViewById(R.id.imgHeader);
        }
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private ImageView imgDrawer;

        private ViewHolderItem(View v) {
            super(v);
            imgDrawer = (ImageView) v.findViewById(R.id.imgDrawer);
            tvTitle = (TextView) v.findViewById(R.id.tvTitleDrawer);
        }
    }

    public class ViewHolderFooter extends RecyclerView.ViewHolder {

        private TextView tvHelpFeedback;
        private TextView tvShare;
        private TextView tvRate;

        private ViewHolderFooter(View v) {
            super(v);
            tvHelpFeedback = (TextView) v.findViewById(R.id.tvHelpFeedback);
            tvShare = (TextView) v.findViewById(R.id.tvShare);
            tvRate = (TextView) v.findViewById(R.id.tvRate);
        }
    }

    private List<DrawerItem> mDrawerItems;
    private View.OnClickListener mOnClickListener;
    private int mWidthItem;
    private Context mContext;
    private Bitmap mBitmap;

    public DrawerAdapter(Context context, List<DrawerItem> drawerItems, View.OnClickListener onClickListener) {
        mContext = context;
        mDrawerItems = drawerItems;
        mOnClickListener = onClickListener;
        mWidthItem = ScreenUtil.getWidthScreen(context) * 3 / 4;
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        Drawable drawable = wallpaperManager.getDrawable();
        if (drawable != null) {
            mBitmap = ((BitmapDrawable) drawable).getBitmap();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_header, parent, false);
            return new ViewHolderHeader(view);
        } else if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_drawer, parent, false);
            return new ViewHolderItem(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.footer_drawer, parent, false);
            return new ViewHolderFooter(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderHeader) {
            final ViewHolderHeader viewHolderHeader = (ViewHolderHeader) holder;
            viewHolderHeader.imgDrawerHeader.setImageBitmap(mBitmap);
            viewHolderHeader.imgDrawerHeader.setOnClickListener(null);
        } else if (holder instanceof ViewHolderItem) {
            final ViewHolderItem viewHolderItem = (ViewHolderItem) holder;
            DrawerItem drawerItem = mDrawerItems.get(position - 1);
            viewHolderItem.itemView.getLayoutParams().width = mWidthItem;
            viewHolderItem.tvTitle.setText(drawerItem.getTitle());
            viewHolderItem.imgDrawer.setImageResource(drawerItem.getIcon());
            viewHolderItem.itemView.setOnClickListener(mOnClickListener);
        } else {
            final ViewHolderFooter viewHolderFooter = (ViewHolderFooter) holder;
            viewHolderFooter.tvHelpFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.feedBack(mContext);
                }
            });
            viewHolderFooter.tvShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.shareApp(mContext);
                }
            });
            viewHolderFooter.tvRate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.rateApp(mContext);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDrawerItems.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return TYPE_HEADER;
        } else if (isFooterPosition(position)) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    private boolean isHeaderPosition(int position) {
        return position == 0;
    }

    private boolean isFooterPosition(int position) {
        return position == mDrawerItems.size() + 1;
    }
}
