package com.mobiapp.nicewallpapers.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.mobiapp.nicewallpapers.MainActivity;
import com.mobiapp.nicewallpapers.ads.AdMobBannerHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

@EFragment
public abstract class BaseFragment extends Fragment {

    protected OnBaseFragmentListener mOnBaseFragmentListener;
    protected AdMobBannerHelper mAdMobBannerHelper;
    protected KProgressHUD mKProgressHUD;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnBaseFragmentListener = (OnBaseFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnBaseFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mKProgressHUD = KProgressHUD.create(getActivity()).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
    }

    @AfterViews
    protected abstract void init();

    /**
     * @param fragment is fragment next
     * @param isBack   is add stack
     */
    protected void replaceFragment(Fragment fragment, boolean isBack) {
        MainActivity act = (MainActivity) getActivity();
        act.replaceFragment(fragment, isBack);
    }

    protected void setHeader(String title, MainActivity.HeaderBarType type, boolean isDraw) {
        if (null != mOnBaseFragmentListener) {
            mOnBaseFragmentListener.setTitleHeader(title);
            mOnBaseFragmentListener.setTypeHeader(type);
            mOnBaseFragmentListener.setDrawerLayout(isDraw);
        }
    }

    protected void setHeader(String title, MainActivity.HeaderBarType type) {
        if (null != mOnBaseFragmentListener) {
            mOnBaseFragmentListener.setTitleHeader(title);
            mOnBaseFragmentListener.setTypeHeader(type);
        }
    }

    protected void showLoadError(View view, TextView textView, KProgressHUD progressBar,
                                 RecyclerView recyclerView, String text) {
        view.setVisibility(View.VISIBLE);
        textView.setText(text);
        if (null != progressBar) {
            progressBar.dismiss();
        }
        recyclerView.setVisibility(View.GONE);
    }

    public interface OnBaseFragmentListener {
        void setTitleHeader(String title);

        void setTypeHeader(MainActivity.HeaderBarType type);

        void setDrawerLayout(boolean isDraw);
    }
}
