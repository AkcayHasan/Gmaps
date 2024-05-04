package com.akcay.gmaps.permissions

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.akcay.gmaps.util.Constants.PERMISSION_REQUEST_CODE
import com.akcay.gmaps.util.hasLocationPermission

object CheckForPermission {

    operator fun invoke(context: Activity, continueProcess: () -> Unit) {
        if (!context.hasLocationPermission()) {
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        } else {
            continueProcess.invoke()
        }
    }
}