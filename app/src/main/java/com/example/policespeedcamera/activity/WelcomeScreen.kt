package com.example.policespeedcamera.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.policespeedcamera.R
import com.example.policespeedcamera.adsManager.InterstitialsAdClass
import com.example.policespeedcamera.utils.Constants
import com.example.policespeedcamera.databinding.ActivityWelcomeBinding
import com.example.policespeedcamera.utils.Constants.lat
import com.example.policespeedcamera.utils.Constants.lng
import com.google.android.gms.location.*
import kotlin.system.exitProcess


class WelcomeScreen : BaseActivity<ActivityWelcomeBinding>() {

    private var permissionsGranted = false
    private var isGPS = false
    private val isContinue = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViews(R.layout.activity_welcome)
        binding?.customLayoutBannerTop?.shimmerViewContainer?.startShimmer()
        binding?.customLayoutBannerBottom?.shimmerViewContainer?.startShimmer()
        checkInternet()
        showBannerAd(binding?.customLayoutBannerTop)
        showBannerAd(binding?.customLayoutBannerBottom)

        binding?.customLayoutBannerTop?.shimmerViewContainer?.startShimmer()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding?.privacypolicyId?.setOnClickListener {
            val str =
                "https://docs.google.com/document/u/0/d/1tNr-YKT2LEWErSPcIsbO_VSubLeX8W_iUPQ9md8RYRU/mobilebasic"
            try {
                val intent = Intent("android.intent.action.MAIN")
                intent.component =
                    ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main")
                intent.addCategory("android.intent.category.LAUNCHER")
                intent.data = Uri.parse(str)
                this@WelcomeScreen.startActivity(intent)
            } catch (unused: ActivityNotFoundException) {
                this@WelcomeScreen.startActivity(
                    Intent(
                        "android.intent.action.VIEW",
                        Uri.parse(str)
                    )
                )
            }
        }

        locationRequest = LocationRequest.create().apply {
            interval = 100
            fastestInterval = (5 * 1000).toLong()  // 10 seconds
            priority = Priority.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 100
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        Constants.location = location
                        lat = location.latitude
                        lng = location.longitude
                        Log.e(TAG, "onLocationResult: ${location.latitude}")
                        Log.e(TAG, "onLocationResult: ${location.longitude}")
                    }
                }
            }
        }
        binding?.next?.setOnClickListener {
            showInterstitialAd(timer, object : InterstitialsAdClass.AdDismiss {
                override fun dismissed(dismiss: Boolean) {
                    Log.e(TAG, "dismissed: $dismiss")
                    gotoActivity(Intent(applicationContext, MainActivity::class.java), true)
                }
            })
        }
    }


    private val timer = object : CountDownTimer(1500, 1500) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            adLoader?.dismissDialog()
            if (InterstitialsAdClass.interstitialAd != null) {
                InterstitialsAdClass.showFacebookInterstitial(this@WelcomeScreen,
                    object : InterstitialsAdClass.AdDismiss {
                        override fun dismissed(dismiss: Boolean) {
                            gotoActivity(Intent(applicationContext, MainActivity::class.java), true)
                        }
                    })
            } else {
                gotoActivity(Intent(applicationContext, MainActivity::class.java), true)
            }
        }
    }


    private fun checkInternet() {
        if (isNetworkAvailable(this@WelcomeScreen)) {
            GpsUtils(this@WelcomeScreen)
            turnGPSOn(object : OnGpsListener {
                override fun gpsStatus(isGPSEnable: Boolean) {
                    isGPS = isGPSEnable
                }
            })
            getLocation()
            Log.e(TAG, "checkInternet: $isGPS")
        } else {
            showInternetDialog()
        }
    }

    private fun showInternetDialog() {
        val alertDialog = android.app.AlertDialog.Builder(this@WelcomeScreen)
        alertDialog.setTitle("Internet Error")
        alertDialog.setMessage("Internet is not enabled! ")
        alertDialog.setPositiveButton("Retry")
        { _, _ -> checkInternet() }
        alertDialog.setNegativeButton("Cancel")
        { dialog, _ ->
            dialog.cancel()
            exitProcess(0)
        }
        alertDialog.show()
    }


    @SuppressLint("MissingPermission")
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            var isGrantedOverAll = false
            permissions.entries.forEach {
                val isGranted = it.value
                if (isGranted) {
                    Log.e(TAG, "granted: ")
                    isGrantedOverAll = true
                    if (isContinue) {
                        mFusedLocationClient!!.requestLocationUpdates(
                            locationRequest!!,
                            locationCallback!!,
                            null
                        )
                        Log.e(TAG, "getLocation if: $isContinue")
                    } else {
                        Log.e(TAG, "getLocation else: $isContinue")
                        mFusedLocationClient!!.lastLocation.addOnSuccessListener(this@WelcomeScreen) { location ->
                            if (location != null) {
                                Log.e(TAG, "get: if")
                                Constants.location = location
                                lat = location.latitude
                                lng = location.longitude
                                Log.e(
                                    TAG,
                                    "onRequestPermissionsResult: ${location.latitude} :: ${location.longitude}"
                                )
                                binding?.next?.visibility = View.VISIBLE
                            } else {
                                Log.e(TAG, "get: else")
                                mFusedLocationClient!!.requestLocationUpdates(
                                    locationRequest!!,
                                    locationCallback!!,
                                    null
                                )
                                getLocation()
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "not granted: ")

                    isGrantedOverAll = false
                }
            }
            if (!isGrantedOverAll) {
                showPermissionDialog()
            }
        }

    private fun showPermissionDialog() {
        val alertDialog = android.app.AlertDialog.Builder(this@WelcomeScreen)
        alertDialog.setTitle("Permission Required")
        alertDialog.setMessage("Please Enable Permissions")
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton(
            "Ok"
        ) { dialog, _ ->
            dialog.cancel()
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        alertDialog.show()
    }

    private fun getLocation() {

        if (!permissionsGranted) {
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

}