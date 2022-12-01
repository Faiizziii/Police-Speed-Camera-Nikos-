package com.example.policespeedcamera.activity

import android.app.Activity
import android.app.Dialog
import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.policespeedcamera.AdLoadingDialog
import com.example.policespeedcamera.BuildConfig
import com.example.policespeedcamera.R
import com.example.policespeedcamera.adsManager.InterstitialsAdClass
import com.example.policespeedcamera.utils.Constants
import com.example.policespeedcamera.databinding.CustomBannerBinding
import com.example.policespeedcamera.databinding.ExitBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions

abstract class BaseActivity<Binding : ViewDataBinding> : AppCompatActivity() {

    private var mSettingsClient: SettingsClient? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var locationManager: LocationManager? = null
    var binding: Binding? = null
    private var getActivity: Activity? = null
    var TAG: String? = null
    var adLoader: AdLoadingDialog? = null
    val markerOptions = MarkerOptions()
    lateinit var googleMap: GoogleMap
    var mFusedLocationClient: FusedLocationProviderClient? = null
    var locationRequest: LocationRequest? = null
    var locationCallback: LocationCallback? = null



    lateinit var exitdial: Dialog
    lateinit var dialog: Dialog


    fun fetching(activity: Activity) {
        dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.fetching)
        dialog.show()
    }

    fun bindViews(layoutID: Int) {
        binding = DataBindingUtil.setContentView(this, layoutID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivity = this
        TAG = getActivity!!::class.java.simpleName
        adLoader = AdLoadingDialog(this)
    }


    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        // if no network is availablgoButton_ide networkInfo will be null, otherwise check if we are connected
        try {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        } catch (e: Exception) {
            Log.e("UtilsClass", "isNetworkAvailable()::::" + e.message)
        }
        return false
    }

    fun showInterstitialAd(
        timer: CountDownTimer,
        returnBack: InterstitialsAdClass.AdDismiss? = null
    ) {
        if (isNetworkAvailable(this)) {
            /* if (BuildConfig.DEBUG) {
                 returnBack.dismissed(true)
             } else {*/
            adLoader?.showDialog()
            timer.start()
            /* }*/
        } else {
            returnBack?.dismissed(true)
        }
    }


    fun showBannerAd(
        bannerLayout: CustomBannerBinding?
    ) {
        if (isNetworkAvailable(this)) {
            val adRequest = AdRequest.Builder().build()
            bannerLayout?.bannerTop?.loadAd(adRequest)

            bannerLayout?.bannerTop?.adListener = object : AdListener() {
                override fun onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                override fun onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    bannerLayout?.shimmerViewContainer?.stopShimmer()
                    bannerLayout?.bannerTop?.visibility = View.GONE
                    // Code to be executed when an ad request fails.
                }

                override fun onAdImpression() {
                    // Code to be executed when an impression is recorded
                    // for an ad.
                }

                override fun onAdLoaded() {
                    bannerLayout?.shimmerViewContainer?.stopShimmer()
                    bannerLayout?.bannerTop?.visibility = View.VISIBLE
                    // Code to be executed when an ad finishes loading.
                }

                override fun onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }
            }
        }
    }

    fun GpsUtils(context: Context) {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mSettingsClient = LocationServices.getSettingsClient(context)
        locationRequest = LocationRequest.create().apply {
            interval = (10 * 1000).toLong()
            fastestInterval = (2 * 1000).toLong()  // 10 seconds
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)
        mLocationSettingsRequest = builder.build()

        //**************************
        builder.setAlwaysShow(true) //this is the key ingredient
        //**************************
    }

    // method for turn on GPS
    fun turnGPSOn(onGpsListener: OnGpsListener?) {
        if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onGpsListener?.gpsStatus(true)
        } else {
            mLocationSettingsRequest?.let {
                mSettingsClient!!.checkLocationSettings(it)
                    .addOnSuccessListener((this as Activity?)!!) { //  GPS is already enable, callback GPS status through listener
                        onGpsListener?.gpsStatus(true)
                    }
                    .addOnFailureListener(
                        (this as Activity?)!!
                    ) { e ->
                        val statusCode = (e as ApiException).statusCode
                        when (statusCode) {
                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                val rae = e as ResolvableApiException
                                (this as Activity?)?.let { it1 ->
                                    rae.startResolutionForResult(
                                        it1,
                                        Constants.GPS_REQUEST
                                    )
                                }
                            } catch (sie: IntentSender.SendIntentException) {
                                Log.i(
                                    ContentValues.TAG,
                                    "PendingIntent unable to execute request."
                                )
                            }
                            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                                val errorMessage =
                                    "Location settings are inadequate, and cannot be " +
                                            "fixed here. Fix in Settings."
                                Log.e(ContentValues.TAG, errorMessage)
                                Toast.makeText(this as Activity?, errorMessage, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }
            }
        }
    }

    interface OnGpsListener {
        fun gpsStatus(isGPSEnable: Boolean)
    }


    fun gotoActivity(intent: Intent, finish: Boolean? = null) {
        Log.e(TAG, "gotoActiivyt: $finish")
        startActivity(intent)
        if (finish!!) {
            finish()
        }
    }


    fun ExitDialog(context: Context) {
        val bindDialog = ExitBinding.inflate(layoutInflater)
        exitdial = Dialog(context)
        exitdial.setContentView(bindDialog.root)
        exitdial.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        exitdial.setCancelable(false)
        exitdial.show()

        bindDialog.yes.setOnClickListener {
            exitdial.dismiss()
            finishAffinity()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask()
            }
        }
        bindDialog.no.setOnClickListener {
            exitdial.dismiss()
        }
        bindDialog.rate.setOnClickListener {
            val uri: Uri = Uri.parse("market://details?id=$packageName")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
        }
    }
}