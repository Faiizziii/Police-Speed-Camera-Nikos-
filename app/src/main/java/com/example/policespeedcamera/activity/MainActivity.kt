package com.example.policespeedcamera.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.policespeedcamera.R
import com.example.policespeedcamera.adsManager.InterstitialsAdClass
import com.example.policespeedcamera.callbacks.NearbyPointDataListener
import com.example.policespeedcamera.databinding.ActivityMainBinding
import com.example.policespeedcamera.models.PointsModel
import com.example.policespeedcamera.service.NetworkService
import com.example.policespeedcamera.utils.Constants.lat
import com.example.policespeedcamera.utils.Constants.lng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng


class MainActivity : BaseActivity<ActivityMainBinding>(), NearbyPointDataListener {

    private var type: String = "police"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViews(R.layout.activity_main)

        binding?.customLayoutBannerTop?.shimmerViewContainer?.startShimmer()
        binding?.customLayoutBannerBottom?.shimmerViewContainer?.startShimmer()
        showBannerAd(binding?.customLayoutBannerTop)
        showBannerAd(binding?.customLayoutBannerBottom)

        binding?.mapView?.onCreate(savedInstanceState)
        binding?.mapView?.onResume()
        fetch()
        try {
            MapsInitializer.initialize(applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding?.mapView?.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(p0: GoogleMap) {
                googleMap = p0
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                p0.isMyLocationEnabled = true

                val sydney = LatLng(lat, lng)
                val cameraPosition = CameraPosition.Builder().target(sydney).zoom(15f).build()
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        })
        fetching(this@MainActivity)

        binding?.speedcircle?.visibility = View.GONE
        binding?.cameracircle?.visibility = View.GONE
        binding?.maintenancecircle?.visibility = View.GONE
        binding?.addlocationcircle?.visibility = View.GONE

        binding?.police?.setOnClickListener {
            type = "police"
            binding?.policecircle?.visibility = View.VISIBLE
            binding?.imgpolice?.setImageResource(R.drawable.ic_policeman)
            binding?.imgspeed?.setImageResource(R.drawable.un_ic_speed)
            binding?.imgcamera?.setImageResource(R.drawable.un_ic_camera)
            binding?.imgmaintenance?.setImageResource(R.drawable.un_ic_maintenance)
            binding?.imgaddlocation?.setImageResource(R.drawable.un_ic_addpoint)
            binding?.speedcircle?.visibility = View.GONE
            binding?.cameracircle?.visibility = View.GONE
            binding?.maintenancecircle?.visibility = View.GONE
            binding?.addlocationcircle?.visibility = View.GONE
            showInterstitialAd(timer, object : InterstitialsAdClass.AdDismiss {
                override fun dismissed(dismiss: Boolean) {
                    fetching(this@MainActivity)
                    fetch()
                }
            })
            /*AdsManager.getInstance().ShowFacebookInterstitial(this, applicationContext)*/
        }

        binding?.speed?.setOnClickListener {
            Log.e(TAG, "speed: ")
            type = "speed"
            showInterstitialAd(timer, object : InterstitialsAdClass.AdDismiss {
                override fun dismissed(dismiss: Boolean) {
                    fetching(this@MainActivity)
                    fetch()
                }
            })
            binding?.speedcircle?.visibility = View.VISIBLE
            binding?.imgspeed?.setImageResource(R.drawable.ic_speed)
            binding?.imgpolice?.setImageResource(R.drawable.un_ic_policeman)
            binding?.imgcamera?.setImageResource(R.drawable.un_ic_camera)
            binding?.imgmaintenance?.setImageResource(R.drawable.un_ic_maintenance)
            binding?.imgaddlocation?.setImageResource(R.drawable.un_ic_addpoint)
            binding?.cameracircle?.visibility = View.GONE
            binding?.maintenancecircle?.visibility = View.GONE
            binding?.addlocationcircle?.visibility = View.GONE
            binding?.policecircle?.visibility = View.GONE
            /*AdsManager.getInstance().ShowFacebookInterstitial(this, applicationContext)*/

        }
        binding?.camera?.setOnClickListener {
            type = "camera"
            fetching(this@MainActivity)
            fetch()
            binding?.cameracircle?.visibility = View.VISIBLE
            binding?.imgcamera?.setImageResource(R.drawable.ic_camera)
            binding?.imgpolice?.setImageResource(R.drawable.un_ic_policeman)
            binding?.imgspeed?.setImageResource(R.drawable.un_ic_speed)
            binding?.imgmaintenance?.setImageResource(R.drawable.un_ic_maintenance)
            binding?.imgaddlocation?.setImageResource(R.drawable.un_ic_addpoint)
            binding?.speedcircle?.visibility = View.GONE
            binding?.maintenancecircle?.visibility = View.GONE
            binding?.addlocationcircle?.visibility = View.GONE
            binding?.policecircle?.visibility = View.GONE
        }

        binding?.maintenance?.setOnClickListener {
            Log.e(TAG, "maintenance: ")
            type = "maintenance"
            fetching(this@MainActivity)
            fetch()
            binding?.maintenancecircle?.visibility = View.VISIBLE
            binding?.imgmaintenance?.setImageResource(R.drawable.ic_maintenance)
            binding?.imgpolice?.setImageResource(R.drawable.un_ic_policeman)
            binding?.imgspeed?.setImageResource(R.drawable.un_ic_speed)
            binding?.imgcamera?.setImageResource(R.drawable.un_ic_camera)
            binding?.imgaddlocation?.setImageResource(R.drawable.un_ic_addpoint)
            binding?.speedcircle?.visibility = View.GONE
            binding?.cameracircle?.visibility = View.GONE
            binding?.addlocationcircle?.visibility = View.GONE
            binding?.policecircle?.visibility = View.GONE
        }

        binding?.addlocation?.setOnClickListener {
            val intent = Intent(applicationContext, AddPoint::class.java)
            startActivity(intent)
            finish()

        }

    }

    private fun fetch() {
        NetworkService.getNearbyPoints(lat, lng, this)
    }

    override fun onDataError(str: String): String {
        Log.e(TAG, "onDataError:$str")
        dialog.dismiss()
        Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
        return str
    }

    override fun onDataReceived(arrayList: ArrayList<PointsModel>) {
        googleMap.clear()
        dialog.dismiss()
        for (i in 0 until arrayList.size) {
            val lat: Double = arrayList[i].lt
            val lng: Double = arrayList[i].ln


            markerOptions.position(LatLng(lat, lng))
            markerOptions.title(arrayList[i].category)
            if (type == "police") {
                if (arrayList[i].category.contains("police") || arrayList[i].category.contains("Police Van")) {
                    Log.e(TAG, "***onDataReceived: police : ${arrayList[i].ln}")
                    Log.e(TAG, "***onDataReceived: police: ${arrayList[i].lt}")
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.police_pin))
                    googleMap.addMarker(markerOptions)
                } else {
                    Log.e(TAG, "onDataReceived: police else")
                }
            } else if (type == "maintenance") {
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


    override fun onBackPressed() {
        ExitDialog(this@MainActivity)
    }

    private val timer = object : CountDownTimer(1500, 1500) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            adLoader?.dismissDialog()
            if (InterstitialsAdClass.interstitialAd != null) {
                InterstitialsAdClass.showFacebookInterstitial(this@MainActivity,
                    object : InterstitialsAdClass.AdDismiss {
                        override fun dismissed(dismiss: Boolean) {
                            fetching(this@MainActivity)
                            fetch()
                        }
                    })
            }
        }
    }


}