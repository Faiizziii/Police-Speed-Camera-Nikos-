package com.example.policespeedcamera.adsManager

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdMobAdsManager {


    private var mAdmobInterstitialAd: InterstitialAd? = null

    fun loadAdmobBanner(activity: Activity, bannerContainer: LinearLayout) {
        bannerContainer.gravity = Gravity.CENTER
        val mAdmobBanner = AdView(activity)
        val adSize = getAdSize(activity)
        mAdmobBanner.setAdSize(adSize)
        mAdmobBanner.adUnitId = AdIds.AdmobBannerId()
        val adRequest1 = AdRequest.Builder().build()
        mAdmobBanner.loadAd(adRequest1)
        mAdmobBanner.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.e("TAG", "onAdLoaded: ")
                super.onAdLoaded()
                bannerContainer.removeAllViews()
                bannerContainer.addView(mAdmobBanner)
            }

            override fun onAdClosed() {
                super.onAdClosed()
                Log.e("TAG", "Admob Banner Ad onAdClosed: ")
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.e("TAG", "Admob Banner Ad onAdFailedToLoad with error: " + loadAdError.message)
            }

            override fun onAdOpened() {
                super.onAdOpened()
                Log.e("TAG", "Admob Banner Ad onAdOpened: ")
            }

            override fun onAdClicked() {
                super.onAdClicked()
                Log.e("TAG", "Admob Banner Ad onAdClicked: ")
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Log.e("TAG", "Admob Banner Ad onAdImpression: ")
            }
        }
    }

    fun loadAdmobInterstitial(mContext: Context?) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            mContext!!,
            AdIds.AdmobInterstitialId(),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    mAdmobInterstitialAd = interstitialAd
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                }
            })
    }

    fun showAdmobInterstitial(activity: Activity?, completeListener: InterstitialCompleteListener) {
        if (mAdmobInterstitialAd != null) {
            mAdmobInterstitialAd!!.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        super.onAdFailedToShowFullScreenContent(adError)
                        Log.e( "TAG", "The ad failed to show.")
                        completeListener.onInterstitialDismissed()
                    }

                    override fun onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent()
                        Log.d( "TAG" , "The ad was shown.")
                    }

                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        Log.e( "TAG" , "The ad was dismissed.")
                        mAdmobInterstitialAd = null
                        loadAdmobInterstitial(activity)
                        completeListener.onInterstitialDismissed()
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                    }
                }
            mAdmobInterstitialAd!!.show(activity!!)
        } else {
            completeListener.onInterstitialDismissed()
        }
    }

    private fun getAdSize(activity: Activity): AdSize {
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }

    interface InterstitialCompleteListener {
        fun onInterstitialDismissed()
    }

}