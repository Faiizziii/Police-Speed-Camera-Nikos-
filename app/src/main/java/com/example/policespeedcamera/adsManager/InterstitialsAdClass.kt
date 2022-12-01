package com.example.policespeedcamera.adsManager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.policespeedcamera.utils.Constants
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object InterstitialsAdClass {


    var adRequest: AdRequest? = null
    private var mAdIsLoading: Boolean = false
    var interstitialAd: InterstitialAd? = null
    var TAG = InterstitialsAdClass::class.java.name
    private var adDismiss: AdDismiss? = null

    interface AdDismiss {
        fun dismissed(dismiss: Boolean)
    }

    private val listener = object : InterstitialAdLoadCallback() {
        override fun onAdFailedToLoad(adError: LoadAdError) {
            Log.e(TAG, adError.message)
            interstitialAd = null
            mAdIsLoading = false
            val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                    "message: ${adError.message}"
            Log.e(TAG, "onAdFailedToLoad: $error")
        }

        override fun onAdLoaded(p0: InterstitialAd) {
            super.onAdLoaded(p0)
            Log.e(TAG, "Ad was loaded.")
            interstitialAd = p0
            mAdIsLoading = false
        }
    }

    fun loadFacebookInterstitial(context: Context) {
        adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            Constants.getIntersteitalId(), adRequest!!,
            listener
        )
    }

    fun showFacebookInterstitial(activity: Activity,adDismiss: AdDismiss?=null) {
        if (interstitialAd != null) {
            if (this.adDismiss != null) {
                this.adDismiss = adDismiss
            }
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

                override fun onAdDismissedFullScreenContent() {
                    Log.e(TAG, "Ad was dismissed.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    interstitialAd = null
                    adDismiss?.dismissed(true)
                    loadFacebookInterstitial(activity)
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    Log.e(TAG, "Ad failed to show.")
                    interstitialAd = null
                    adDismiss?.dismissed(true)
                }

                override fun onAdShowedFullScreenContent() {
                    Log.e(TAG, "Ad showed fullscreen content.")
                }
            }
            interstitialAd?.show(activity)
        } else {
            loadFacebookInterstitial(activity)
        }
    }
}
