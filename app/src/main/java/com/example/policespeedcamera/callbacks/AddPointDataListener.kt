package com.example.policespeedcamera.callbacks

interface AddPointDataListener {

    fun onError(str: String): String

    fun onPointAdded()
}