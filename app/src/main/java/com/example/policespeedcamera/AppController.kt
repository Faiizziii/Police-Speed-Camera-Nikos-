package com.example.policespeedcamera

import android.app.Application
import com.example.policespeedcamera.adsManager.InterstitialsAdClass
import com.google.android.gms.ads.MobileAds

class AppController : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {}
        InterstitialsAdClass.loadFacebookInterstitial(this)
    }
}