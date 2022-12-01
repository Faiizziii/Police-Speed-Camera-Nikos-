package com.example.policespeedcamera.service

import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.example.policespeedcamera.callbacks.AddPointDataListener
import com.example.policespeedcamera.callbacks.NearbyPointDataListener
import com.example.policespeedcamera.models.PointsModel
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object NetworkService {

    val TAG = NetworkService::class.java.simpleName
    private var nearbyPointModels = ArrayList<PointsModel>()


    fun addPoint(
        lat: Double,
        lng: Double,
        category: String,
        disLikes: Int,
        likes: Int,
        phoneNumber: String,
        addPointDataListener: AddPointDataListener
    ) {

        val jsonObject = JSONObject()
        try {
//            jsonObject.put("lat", lat)
//            jsonObject.put("lng", lng)
            jsonObject.put("lat", lng)
            jsonObject.put("lng", lat)
            jsonObject.put("category", category)
            jsonObject.put("disLikes", disLikes)
            jsonObject.put("like", likes)
            jsonObject.put("phoneNumber", phoneNumber)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

//        AndroidNetworking.post("https://k3e3rjuskf.execute-api.us-east-1.amazonaws.com/dev/")
//        AndroidNetworking.post("https://fifrlplp12.execute-api.us-east-1.amazonaws.com/prod")
        AndroidNetworking.post("https://6mt9tuh20j.execute-api.us-east-1.amazonaws.com/live")
            .addHeaders("x-api-key", "ooKkszTQsg6ZtcIzFJsFW2GBrtSy45Fb1uukgm4Y")
            .addJSONObjectBody(jsonObject)
            .addBodyParameter("content-type", "application/json")
            .setTag("test")
            .setPriority(Priority.HIGH)
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    Log.d(TAG, "onResponse Called $response")
                    addPointDataListener.onPointAdded()
                }

                override fun onError(anError: ANError?) {
                    Log.d(TAG, "anError Called $anError")
                    addPointDataListener.onError(anError!!.message!!)
                }

            })
    }


    fun getNearbyPoints(
        lat: Double,
        lng: Double,
        nearbyPointDataListener: NearbyPointDataListener
    ) {
        val jsonObject = JSONObject()
        try {
//            jsonObject.put("lat", lat)
//            jsonObject.put("lng", lng)
            jsonObject.put("lat", lat)
            jsonObject.put("lng", lng)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        nearbyPointModels.clear()

//        AndroidNetworking.post("https://1kbyuclmv5.execute-api.us-east-1.amazonaws.com/prod")
//        AndroidNetworking.post("https://g0i2v3mq4c.execute-api.us-east-1.amazonaws.com/dev/")
        AndroidNetworking.post("https://8ovbdrm6fa.execute-api.us-east-1.amazonaws.com/live")
            .addHeaders("x-api-key", "ooKkszTQsg6ZtcIzFJsFW2GBrtSy45Fb1uukgm4Y")
            .addJSONObjectBody(jsonObject)
            .addBodyParameter("content-type", "application/json")
            .setTag("test")
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {

                override fun onResponse(response: JSONArray?) {
//                        Log.d(TAG, "onResponse Called " + response.toString())
                    if (response != null) {
                        val gson = Gson()
//                        val dataModel = gson.fromJson(response.toString(), Array<Main_Data_Modal>::class.java)

                        if (response.length() > 0) {
                            for (value in 0 until response.length()) {
//                                Log.e("Number", response.getJSONObject(value).getString("phoneNumber"))
//                                Log.e("hashKey", response.getJSONObject(value).getString("category"))

                                var st: String =
                                    response.getJSONObject(value).getJSONObject("geoJson").get("S")
                                        .toString()
                                st = st.substring(st.indexOf("[") + 1, st.indexOf("]"))
                                val lt = (st.substring(0, st.indexOf(",")-1)).toDouble()
                                val ln =
                                    (st.substring(st.indexOf(",") + 1, st.length - 1)).toDouble()

                                val pinModal = PointsModel(
                                    lt,
                                    ln,
                                    response.getJSONObject(value).getJSONObject("category").get("S")
                                        .toString(),
                                    response.getJSONObject(value).getJSONObject("like")
                                        .getString("N").toString(),
                                    response.getJSONObject(value).getJSONObject("disLikes")
                                        .getString(
                                            "N"
                                        ).toString(),
                                    response.getJSONObject(value).getJSONObject("phoneNumber")
                                        .get("S").toString()
                                )
                                nearbyPointModels.add(pinModal)

//                                if (Utility.getInstance().getSelectedPinType() != "none") {
//                                    if (Utility.getInstance().getSelectedPinType() == pinModal.category) {
//                                        pinDataReceivedListener.onPinReceived(pinModal)
//                                    }
//                                } else {
//                                    pinDataReceivedListener.onPinReceived(pinModal)
//                                }
                            }


                        }
                    }

                    Log.d(TAG, "NearbyPointModel Size " + nearbyPointModels.size)

                    nearbyPointDataListener.onDataReceived(nearbyPointModels)
                }

                override fun onError(anError: ANError?) {
                    Log.d(TAG, "onError Called " + anError!!.message)
                    nearbyPointDataListener.onDataError(anError.message!!)

                }

            })
    }
}