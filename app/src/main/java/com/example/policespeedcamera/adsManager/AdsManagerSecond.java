package com.example.policespeedcamera.adsManager;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.policespeedcamera.BuildConfig;
import com.example.policespeedcamera.utils.Constants;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

public class AdsManagerSecond {

    private static AdsManagerSecond ourInstance = new AdsManagerSecond();
    public String TAG = AdsManagerSecond.class.getName();
    private Context context;
    private InterstitialAd interstitialAd;

    public static AdsManagerSecond getInstance() {
        return ourInstance;
    }

    public void LoadFacebookInterstitial(Activity activity, Context context) {
        AudienceNetworkAds.initialize(activity);

        if (BuildConfig.DEBUG) {
            interstitialAd = new InterstitialAd(context, Constants.INSTANCE.getInterstetialtest());
        } else {
            interstitialAd = new InterstitialAd(context, Constants.INSTANCE.getInterstetial2());
        }


//        AdSettings.addTestDevice("HASHED ID");


        // Set listeners for the Interstitial Ad

        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

                Log.d(TAG, "FB onInterstitialDisplayed");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {

                Log.d(TAG, "FB onInterstitialDismissed");

            }

            @Override
            public void onError(Ad ad, AdError adError) {

                Log.d(TAG, "FB onError " + adError.getErrorMessage());
//                loadFBInterstitialAds();

            }

            @Override
            public void onAdLoaded(Ad ad) {

                Log.d(TAG, "FB onAdLoaded");

            }

            @Override
            public void onAdClicked(Ad ad) {

                Log.d(TAG, "FB onAdClicked");

            }

            @Override
            public void onLoggingImpression(Ad ad) {

                Log.d(TAG, "FB onLoggingImpression");

            }
        };


        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().withAdListener(interstitialAdListener).build());


    }

    public void ShowFacebookInterstitial(Activity activity, Context context) {

        if (interstitialAd != null && interstitialAd.isAdLoaded()) {
            try {
                interstitialAd.show();
            } catch (Exception unused) {
            }
        }
        LoadFacebookInterstitial(activity, context);

    }

}

