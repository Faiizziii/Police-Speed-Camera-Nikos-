package com.example.policespeedcamera.callbacks

import com.example.policespeedcamera.models.PointsModel

interface NearbyPointDataListener {
    fun onDataError(str: String): String

    fun onDataReceived(arrayList: ArrayList<PointsModel>)
}