package com.mobiapp.nicewallpapers.ads;

import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.mobiapp.nicewallpapers.R;
import com.mobiapp.nicewallpapers.util.Utils;

public class AdMobInterstitialHelper {

    private InterstitialAd mInterstitialAd;
    private com.facebook.ads.InterstitialAd interstitialAdFb;
    private Context mContext;

    public AdMobInterstitialHelper(Context context) {
        mContext = context;
        if (Utils.appInstalledOrNot(context, "com.facebook.katana")) {
            interstitialAdFb = new com.facebook.ads.InterstitialAd(context, context.getString(R.string.ad_unit_id_fb));
        } else {
            mInterstitialAd = new InterstitialAd(context);
            mInterstitialAd.setAdUnitId(context.getString(R.string.ad_unit_id));
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
        }

    }

    public void showAds() {
        if (Utils.appInstalledOrNot(mContext, "com.facebook.katana")) {
            interstitialAdFb.loadAd();
        } else {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }
}
