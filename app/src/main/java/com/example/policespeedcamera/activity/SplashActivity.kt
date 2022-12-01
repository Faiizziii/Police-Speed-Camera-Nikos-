package com.example.policespeedcamera.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.example.policespeedcamera.R
import com.example.policespeedcamera.databinding.ActivitySplashBinding
import java.util.*

class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViews(R.layout.activity_splash)
        timer.start()
    }


    private val timer = object : CountDownTimer(2000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            gotoActivity(Intent(applicationContext, WelcomeScreen::class.java), true)
        }
    }

}


/*
Adloadingdialog.showPopUp(this@SatelliteFinderMethodsActivity, "Show")
val timer = object : CountDownTimer(2000, 1000) {
    override fun onTick(millisUntilFinished: Long) {
    }

    override fun onFinish() {
        FbAdsManager.getInstance().ShowFacebookInterstitial(
            this@SatelliteFinderMethodsActivity,
            applicationContext
        ) {
            if (satelliteSelected) {
                //HSB
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        sendSatelliteData(SatelliteActivity::class.java, "map")

                    }
                }, 500)

            } else {
                builder.setTitle("Alert")
                    .setMessage("Select the satellite first, please.")
                    .setCancelable(true)
                    .setPositiveButton("OK") { dialogInterface, it ->
                        dialogInterface.cancel()
                    }
                    .show()
            }
        }

    }
}
timer.start()
*/
