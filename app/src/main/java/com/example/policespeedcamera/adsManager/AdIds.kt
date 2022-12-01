package com.example.policespeedcamera.adsManager

import com.example.policespeedcamera.BuildConfig


object AdIds {
    private const val admobBannerId = ""
    private const val admobBannerTestId = "ca-app-pub-3940256099942544/6300978111"
    private const val admobInterstitialId = ""
    private const val admobInterstitialTestId = "ca-app-pub-3940256099942544/1033173712"
    private const val appLovinBannerId = "db9d90ff2f117f22"
    private const val appLovinInterstitialId = "45c8143380cc5386"
    fun AdmobBannerId(): String {
        return if (BuildConfig.DEBUG) {
            admobBannerTestId
        } else admobBannerId
    }

    fun AdmobInterstitialId(): String {
        return if (BuildConfig.DEBUG) {
            admobInterstitialTestId
        } else admobInterstitialId
    }
}