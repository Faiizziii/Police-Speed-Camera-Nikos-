package com.example.policespeedcamera.utils

import android.location.Location
import com.example.policespeedcamera.BuildConfig

object Constants {

    var FBBannerIDBottom = "687527072023412_687528402023279"
    var FBBannerIDTop = "687527072023412_687527618690024"
    var FBBannerIDtest = "IMG_16_9_APP_INSTALL#2327287874001751_2327291030668102"
    var interstetial = "687527072023412_687528175356635"
    var interstetial2 = "687527072023412_687528575356595"
    var interstetialtest = "YOUR_PLACEMENT_ID"

 /*   Banner: ca-app-pub-3940256099942544/6300978111
    Interstitial : ca-app-pub-3940256099942544/1033173712
    Reward Video: ca-app-pub-3940256099942544/5224354917
    Native Advanced: ca-app-pub-3940256099942544/2247696110
    Native Express (small): ca-app-pub-3940256099942544/2793859312
    Native Express (large): ca-app-pub-3940256099942544/2177258514
*/
    var liveinterstetial = "ca-app-pub-3940256099942544/1033173712"
    var debuginterstetial = "ca-app-pub-3940256099942544/1033173712"

    var livebanner = "ca-app-pub-3940256099942544/6300978111"
    var debugbanner = "ca-app-pub-3940256099942544/6300978111"

    val LOCATION_REQUEST = 1000
    val GPS_REQUEST = 1001

    var location: Location? = null


    var lat: Double= 0.0
    var lng: Double= 0.0

    fun getIntersteitalId(): String {
        return if (BuildConfig.DEBUG) {
            debuginterstetial
        } else {
            liveinterstetial
        }
    }

    fun getBannerId(): String {
        return if (BuildConfig.DEBUG) {
            debugbanner
        } else {
            livebanner
        }
    }
}