package com.mobiapp.nicewallpapers.ads;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.mobiapp.nicewallpapers.BuildConfig;
import com.mobiapp.nicewallpapers.R;
import com.mobiapp.nicewallpapers.util.Utils;

public class AdMobBannerHelper {

    private Context mContext;
    private AdView adView;
    private com.facebook.ads.AdView adViewFb;
    private static final String TAG = AdMobBannerHelper.class.getSimpleName();

    public AdMobBannerHelper(Context context, FrameLayout parent) {

        mContext = context;
        if (Utils.appInstalledOrNot(context, "com.facebook.katana")) {
            loadFacebookADv(context, parent);
        } else {
            loadGoogleADv(context, parent);
        }
    }

    private void loadFacebookADv(Context context, FrameLayout parent) {
        if (adViewFb != null) {
            adViewFb.destroy();
        }
        adViewFb = new com.facebook.ads.AdView(context, context.getString(R.string.banner_ad_unit_id_fb),
                com.facebook.ads.AdSize.BANNER_HEIGHT_50);
        if (!"release".equals(BuildConfig.BUILD_TYPE)) {
            AdSettings.addTestDevice("0975e6197d30eb2a1e759376cc545189");
        }
        parent.addView(adViewFb);
    }

    private void loadGoogleADv(Context context, FrameLayout parent) {
        adView = new AdView(mContext);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(context.getString(R.string.banner_ad_unit_id));
        parent.addView(adView);
    }

    public void loadAds() {
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device.
        if (Utils.appInstalledOrNot(mContext, "com.facebook.katana")) {
            adViewFb.setAdListener(new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {
                    Log.d(TAG, "onError: " + adError);
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    Log.d(TAG, "onAdLoaded: ");
                }

                @Override
                public void onAdClicked(Ad ad) {
                    Log.d(TAG, "onAdClicked: ");
                }
            });
            adViewFb.loadAd();
        } else {
            AdRequest adRequest = new AdRequest.Builder().build();
            // Start loading the ad in the background.
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    adView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    super.onAdFailedToLoad(errorCode);
                    adView.setVisibility(View.GONE);
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                    adView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    adView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    adView.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
