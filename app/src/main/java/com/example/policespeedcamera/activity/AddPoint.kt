package com.example.policespeedcamera.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.policespeedcamera.R
import com.example.policespeedcamera.adsManager.InterstitialsAdClass
import com.example.policespeedcamera.callbacks.AddPointDataListener
import com.example.policespeedcamera.callbacks.NearbyPointDataListener
import com.example.policespeedcamera.databinding.ActivityAddPointBinding
import com.example.policespeedcamera.models.PointsModel
import com.example.policespeedcamera.service.NetworkService
import com.example.policespeedcamera.utils.Constants
import com.example.policespeedcamera.utils.Constants.lat
import com.example.policespeedcamera.utils.Constants.lng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng


class AddPoint : BaseActivity<ActivityAddPointBinding>(), AddPointDataListener,
    NearbyPointDataListener {

    private lateinit var alertDialog: AlertDialog.Builder
    private var type: String = "police"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViews(R.layout.activity_add_point)


        binding?.customLayoutBannerTop?.shimmerViewContainer?.startShimmer()
        binding?.customLayoutBannerBottom?.shimmerViewContainer?.startShimmer()
        showBannerAd(binding?.customLayoutBannerTop)
        showBannerAd(binding?.customLayoutBannerBottom)

        binding?.addmapView?.onCreate(savedInstanceState)
        binding?.addmapView?.onResume()

        try {
            MapsInitializer.initialize(applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding?.addmapView?.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(p0: GoogleMap) {
                googleMap = p0

                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                p0.isMyLocationEnabled = true


                val sydney =
                    LatLng(lat, lng)

                Log.e(TAG, "onMapReady: $sydney")

                val cameraPosition = CameraPosition.Builder().target(sydney).zoom(15f).build()
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        })

        binding?.addspeedcircle?.visibility = View.GONE
        binding?.addcameracircle?.visibility = View.GONE
        binding?.addmaintenancecircle?.visibility = View.GONE
        binding?.addpolicecircle?.visibility = View.GONE

        fetch()

        binding?.addpolice?.setOnClickListener {
            fetch()
            type = "police"
            addPoint()
            googleMap.clear()
            binding?.imgaddpolice?.setImageResource(R.drawable.ic_policeman)
            binding?.imgaddspeed?.setImageResource(R.drawable.un_ic_speed)
            binding?.imgaddcamera?.setImageResource(R.drawable.un_ic_camera)
            binding?.imgaddmaintenance?.setImageResource(R.drawable.un_ic_maintenance)
            binding?.addpolicecircle?.visibility = View.VISIBLE
            binding?.addspeedcircle?.visibility = View.GONE
            binding?.addcameracircle?.visibility = View.GONE
            binding?.addmaintenancecircle?.visibility = View.GONE
        }

        binding?.addmaintenance?.setOnClickListener {
            type = "maintenance"
            fetch()
            addPoint()
            googleMap.clear()
            binding?.imgaddpolice?.setImageResource(R.drawable.un_ic_policeman)
            binding?.imgaddspeed?.setImageResource(R.drawable.un_ic_speed)
            binding?.imgaddcamera?.setImageResource(R.drawable.un_ic_camera)
            binding?.imgaddmaintenance?.setImageResource(R.drawable.ic_maintenance)
            binding?.addpolicecircle?.visibility = View.GONE
            binding?.addspeedcircle?.visibility = View.GONE
            binding?.addcameracircle?.visibility = View.GONE
            binding?.addmaintenancecircle?.visibility = View.VISIBLE
        }

        binding?.addcamera?.setOnClickListener {
            type = "camera"
            fetch()
            addPoint()
            googleMap.clear()
            binding?.addcameracircle?.visibility = View.VISIBLE
            binding?.imgaddcamera?.setImageResource(R.drawable.ic_camera)
            binding?.imgaddpolice?.setImageResource(R.drawable.un_ic_policeman)
            binding?.imgaddspeed?.setImageResource(R.drawable.un_ic_speed)
            binding?.imgaddmaintenance?.setImageResource(R.drawable.un_ic_maintenance)
            binding?.addspeedcircle?.visibility = View.GONE
            binding?.addmaintenancecircle?.visibility = View.GONE
            binding?.addpolicecircle?.visibility = View.GONE
        }

        binding?.addspeed?.setOnClickListener {
            type = "speed"
            fetch()
            addPoint()
            googleMap.clear()
            binding?.imgaddpolice?.setImageResource(R.drawable.un_ic_policeman)
            binding?.imgaddspeed?.setImageResource(R.drawable.ic_speed)
            binding?.imgaddcamera?.setImageResource(R.drawable.un_ic_camera)
            binding?.imgaddmaintenance?.setImageResource(R.drawable.un_ic_maintenance)
            binding?.addpolicecircle?.visibility = View.GONE
            binding?.addspeedcircle?.visibility = View.VISIBLE
            binding?.addcameracircle?.visibility = View.GONE
            binding?.addmaintenancecircle?.visibility = View.GONE
        }

    }

    private fun addPoint() {
        alertDialog = AlertDialog.Builder(this)

        alertDialog.setTitle("Add $type")

        // Setting Dialog Message

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to add $type")
        alertDialog.setCancelable(false)
        // Setting Positive "Yes" Button
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton(
            "YES"
        ) { _, _ -> //        Toast.makeText(getApplicationContext(),"fun called",Toast.LENGTH_SHORT).show();
            NetworkService.addPoint(
                lat,
                lng,
                type,
                0,
                0,
                "+111",
                this
            )
            /*AdsManagerSecond.getInstance().ShowFacebookInterstitial(this, applicationContext)*/
        }
        // Setting Negative "NO" Button
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton(
            "NO"
        ) { dialog, _ -> // Write your code here to invoke NO event
            dialog.cancel()
        }
        // Showing Alert Message
        // Showing Alert Message
        alertDialog.show()
    }


    private val timer = object : CountDownTimer(1500, 1500) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            adLoader?.dismissDialog()
            if (InterstitialsAdClass.interstitialAd != null) {
                InterstitialsAdClass.showFacebookInterstitial(this@AddPoint,
                    object : InterstitialsAdClass.AdDismiss {
                        override fun dismissed(dismiss: Boolean) {
                            Toast.makeText(applicationContext, "Points Added", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                        }
                    })
            }
        }
    }

    private fun fetch() {
        NetworkService.getNearbyPoints(lat, lng, this)
    }


    override fun onBackPressed() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onError(str: String): String {
        return str
    }

    override fun onPointAdded() {
        showInterstitialAd(timer, object : InterstitialsAdClass.AdDismiss {
            override fun dismissed(dismiss: Boolean) {
                Log.e(TAG, "dismissed: $dismiss")
                gotoActivity(Intent(applicationContext, MainActivity::class.java), true)
            }
        })
    }

    override fun onDataError(str: String): String {
        return str
    }

    override fun onDataReceived(arrayList: ArrayList<PointsModel>) {
        for (i in 0 until arrayList.size) {
            val lat: Double = arrayList[i].lt
            val lng: Double = arrayList[i].ln

            markerOptions.position(LatLng(lat, lng))
            markerOptions.title(arrayList[i].category)
            if (type == "police") {

                if (arrayList[i].category == "Police Van") {
                    Log.e(TAG, "***onDataReceived: police : ${arrayList[i].ln}")
                    Log.e(TAG, "***onDataReceived: police: ${arrayList[i].lt}")
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.police_pin))
                    googleMap.addMarker(markerOptions)
                } else {
                    Log.e(TAG, "onDataReceived: police else")
                }

            }
//
            else if (type == "maintenance") {
                if (arrayList[i].category == "Construction") {
                    Log.e(TAG, "***onDataReceived: construction : ${arrayList[i].ln}")
                    Log.e(TAG, "***onDataReceived: construction: ${arrayList[i].lt}")
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.work_pin))
                    googleMap.addMarker(markerOptions)

                } else {
                    Log.e(TAG, "onDataReceived: maintenance else")
                }
            }
//
            else if (type == "speed") {
                if (arrayList[i].category == "Speed Limit") {
                    Log.e(TAG, "***onDataReceived: speed : ${arrayList[i].ln}")
                    Log.e(TAG, "***onDataReceived: speed: ${arrayList[i].lt}")
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.speed_pin))
                    googleMap.addMarker(markerOptions)
                } else {
                    Log.e(TAG, "onDataReceived: speed else")
                }
            }
//
            else if (type == "camera") {
                if (arrayList[i].category == "Speed Camera") {
                    Log.e(TAG, "***onDataReceived: camera : ${arrayList[i].ln}")
                    Log.e(TAG, "***onDataReceived: camera: ${arrayList[i].lt}")
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.camea_pin))
                    googleMap.addMarker(markerOptions)
                } else {
                    Log.e(TAG, "onDataReceived: speed else")
                }

            }

        }
    }
}