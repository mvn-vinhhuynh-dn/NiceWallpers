package com.mobiapp.nicewallpapers.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mobiapp.nicewallpapers.R;
import com.mobiapp.nicewallpapers.model.ChooseSources;

import java.util.List;

public class ChooseSourcesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public class ViewHolderItem extends RecyclerView.ViewHolder {

        private CheckBox ckChooseSources;
        private TextView tvChooseSources;

        private ViewHolderItem(View v) {
            super(v);
            ckChooseSources = (CheckBox) v.findViewById(R.id.ckChooseSources);
            tvChooseSources = (TextView) v.findViewById(R.id.tvChooseSources);
        }
    }

    private List<ChooseSources> mChooseSourcess;
    private OnSelectedItemListener mOnSelectedItemListener;

    public ChooseSourcesAdapter(List<ChooseSources> chooseSources, OnSelectedItemListener onSelectedItemListener) {
        mChooseSourcess = chooseSources;
        mOnSelectedItemListener = onSelectedItemListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_choose_sources, parent, false);
        return new ViewHolderItem(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderItem) {
            final ViewHolderItem viewHolderItem = (ViewHolderItem) holder;
            final ChooseSources chooseSources = mChooseSourcess.get(position);

            viewHolderItem.tvChooseSources.setText(chooseSources.getTitle());
            viewHolderItem.ckChooseSources.setChecked(chooseSources.isChecked());
            viewHolderItem.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolderItem.ckChooseSources.performClick();
                }
            });
            viewHolderItem.ckChooseSources.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mOnSelectedItemListener.onSelected(position, isChecked);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mChooseSourcess.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface OnSelectedItemListener {
        void onSelected(int position, boolean isChecked);
    }
}
