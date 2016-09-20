package com.mobiapp.nicewallpapers.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobiapp.nicewallpapers.R;
import com.mobiapp.nicewallpapers.adapter.DrawerAdapter;
import com.mobiapp.nicewallpapers.model.DrawerItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_drawer)
public class FragmentDrawer extends Fragment {

    @ViewById(R.id.recyclerViewDrawer)
    protected RecyclerView mRecyclerViewDrawer;

    private OnDrawerListener mOnDrawerListener;

    private ImageView mImageViewSelected;
    private TextView mTextViewSelected;

    @AfterViews
    protected void afterViews() {
        final List<DrawerItem> drawerItems = new ArrayList<>();
        drawerItems.add(new DrawerItem(getString(R.string.categories), R.drawable.bg_drawer_categories,
                CategoriesFragment_.builder().build()));
        drawerItems.add(new DrawerItem(getString(R.string.oem_wallpapers), R.drawable.bg_drawer_ome,
                OEMFragment_.builder().build()));
        drawerItems.add(new DrawerItem(getString(R.string.download), R.drawable.bg_drawer_download,
                DownloadFragment_.builder().build()));
        drawerItems.add(new DrawerItem(getString(R.string.random_history), R.drawable.bg_drawer_random,
                RandomFragment_.builder().build()));
        drawerItems.add(new DrawerItem(getString(R.string.blur_history), R.drawable.bg_drawer_blur,
                BlurFragment_.builder().build()));
        drawerItems.add(new DrawerItem(getString(R.string.action_settings), R.drawable.bg_drawer_settings,
                SettingFragment_.builder().build()));

        DrawerAdapter drawerAdapter = new DrawerAdapter(getActivity(), drawerItems, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = mRecyclerViewDrawer.getChildAdapterPosition(view) - 1;
                if (mOnDrawerListener != null) {
                    mOnDrawerListener.OnClickDrawerItem(drawerItems.get(position).getFragment());
                }
                if (null != mTextViewSelected) {
                    mTextViewSelected.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                }
                if (null != mImageViewSelected) {
                    mImageViewSelected.setSelected(false);
                }
                mTextViewSelected = (TextView) view.findViewById(R.id.tvTitleDrawer);
                mImageViewSelected = (ImageView) view.findViewById(R.id.imgDrawer);

                mTextViewSelected.setTextColor(ContextCompat.getColor(getActivity(), R.color.green_700));
                mImageViewSelected.setSelected(true);
            }
        });
        mRecyclerViewDrawer.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViewDrawer.setAdapter(drawerAdapter);
    }

    public void setDrawerListener(OnDrawerListener listener) {
        this.mOnDrawerListener = listener;
    }

    public interface OnDrawerListener {
        void OnClickDrawerItem(Fragment fragment);
    }
}
