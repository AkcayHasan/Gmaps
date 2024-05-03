package com.akcay.gmaps.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.akcay.gmaps.ui.home.MapsActivity
import com.akcay.gmaps.permissions.CheckForPermission
import com.akcay.gmaps.util.Constants.PERMISSION_REQUEST_CODE

@SuppressLint("CustomSplashScreen")
class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { true }

        // checking for permissions
        CheckForPermission(this) {
            startActivity(Intent(this, MapsActivity::class.java))
            finish()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startActivity(Intent(this, MapsActivity::class.java))
                    finish()
                } else {
                    // TODO: alertDialog göster
                }
                return
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
}