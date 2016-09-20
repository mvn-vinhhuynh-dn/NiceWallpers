package com.mobiapp.nicewallpapers.fragments;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobiapp.nicewallpapers.MainActivity;
import com.mobiapp.nicewallpapers.R;
import com.mobiapp.nicewallpapers.adapter.ChooseSourcesAdapter;
import com.mobiapp.nicewallpapers.model.ChooseSources;
import com.mobiapp.nicewallpapers.receiver.AutoChangeWallpaperReceiver;
import com.mobiapp.nicewallpapers.util.PreferenceUtil;
import com.mobiapp.nicewallpapers.util.ScreenUtil;
import com.mobiapp.nicewallpapers.view.DiscreteSeekBar;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EFragment(R.layout.fragment_setting)
public class SettingFragment extends BaseFragment {

    private static final String TAG = SettingFragment.class.getName();
    @ViewById(R.id.switchCompatDuration)
    protected SwitchCompat mSwitchCompatDuration;
    @ViewById(R.id.checkboxWifi)
    protected CheckBox mCheckboxWifi;

    @ViewById(R.id.tvDuration)
    protected TextView mTvDuration;
    @ViewById(R.id.tvTimeDuration)
    protected TextView mTvTimeDuration;
    @ViewById(R.id.tvWifi)
    protected TextView mTvWifi;

    @ViewById(R.id.viewWifi)
    protected LinearLayout mViewWifi;

    @ViewById(R.id.seekBarDuration)
    protected DiscreteSeekBar mSeekBarDuration;

    private int mProgress;

    private PendingIntent mPendingIntent;

    @Override
    protected void init() {
        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(getActivity(), AutoChangeWallpaperReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, 0);


        final boolean isAutoChange = PreferenceUtil.getBoolean(getActivity(),
                PreferenceUtil.AUTO_CHANGE, false);
        mSwitchCompatDuration.setChecked(isAutoChange);
        setColorText(isAutoChange);
        final boolean isAutoChangeWithWifi = PreferenceUtil.getBoolean(getActivity(),
                PreferenceUtil.AUTO_CHANGE_WITH_WIFI, true);
        mCheckboxWifi.setChecked(isAutoChangeWithWifi);

        int duration = PreferenceUtil.getInt(getActivity(), PreferenceUtil.DURATION, 12);
        mTvTimeDuration.setText(String.format(getString(R.string.hours), duration));
        mSeekBarDuration.setProgress(duration);

        mSwitchCompatDuration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setColorText(isChecked);
                PreferenceUtil.saveBoolean(getActivity(), PreferenceUtil.AUTO_CHANGE, isChecked);
                if (isChecked) {
                    startAlarm(mSeekBarDuration.getProgress());
                } else {
                    AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    manager.cancel(mPendingIntent);
                }
            }
        });
        mSeekBarDuration.setMin(1);
        mSeekBarDuration.setMax(72);

        mSeekBarDuration.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                mTvTimeDuration.setText(String.format(getString(R.string.hours), value));
                mProgress = value;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
                Log.i(TAG, "---onStartTrackingTouch--->>>>>>" + mProgress);
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                Log.i(TAG, "---onStopTrackingTouch--->>>>>>" + mProgress);
                startAlarm(mProgress);
                PreferenceUtil.saveInt(getActivity(), PreferenceUtil.DURATION, mProgress);
            }
        });

        mCheckboxWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceUtil.saveBoolean(getActivity(), PreferenceUtil.AUTO_CHANGE_WITH_WIFI, isChecked);
            }
        });
    }

    private void setColorText(boolean isChecked) {
        if (isChecked) {
            mTvDuration.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            mTvWifi.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            mSeekBarDuration.setEnabled(true);
            mCheckboxWifi.setEnabled(true);
            mViewWifi.setEnabled(true);
        } else {
            mTvDuration.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_800));
            mTvWifi.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_800));
            mSeekBarDuration.setEnabled(false);
            mCheckboxWifi.setEnabled(false);
            mViewWifi.setEnabled(false);
        }
    }

    private void startAlarm(int duration) {
        PreferenceUtil.saveBoolean(getActivity(), PreferenceUtil.AUTO_CHANGE_WALLPAPERS, false);
        AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        int interval = duration * 60 * 60 * 1000;
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                interval, mPendingIntent);
    }

    @Click(R.id.viewAutoChange)
    void clickAutoChange() {
        mSwitchCompatDuration.performClick();
    }

    @Click(R.id.viewWifi)
    void clickAutoChangeWithWifi() {
        mCheckboxWifi.performClick();
    }

    @Click(R.id.viewChooseSources)
    void clickChooseSources() {
        dialogChooseSources(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        setHeader(getString(R.string.action_settings), MainActivity.HeaderBarType.TYPE_HOME);
    }

    private void dialogChooseSources(Context context) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choose_sources);

        dialog.findViewById(R.id.viewMainDialog).getLayoutParams().width
                = (int) (ScreenUtil.getWidthScreen(context) - getResources().getDimension(R.dimen.fab_size_normal));
        final List<ChooseSources> datas = ChooseSources.listAll(ChooseSources.class);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerViewChooseSources);
        ChooseSourcesAdapter adapter = new ChooseSourcesAdapter(datas, new ChooseSourcesAdapter.OnSelectedItemListener() {
            @Override
            public void onSelected(int position, boolean isChecked) {
                ChooseSources chooseSources = datas.get(position);
                chooseSources.setChecked(isChecked);
                datas.set(position, chooseSources);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        TextView tvCancel = (TextView) dialog
                .findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView tvSave = (TextView) dialog.findViewById(R.id.tvOk);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ChooseSources chooseSources : datas) {
                    ChooseSources chooseSourcesUpdate = ChooseSources.findById(ChooseSources.class,
                            chooseSources.getId());
                    chooseSourcesUpdate.setChecked(chooseSources.isChecked());
                    chooseSourcesUpdate.save();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
