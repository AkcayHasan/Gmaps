package com.akcay.gmaps.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.akcay.gmaps.util.hasLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class GMapsLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(): Flow<Location> = callbackFlow {
        if (!context.hasLocationPermission()) {
            throw Exception("Lokasyon izinleri eksik..")
        }

        val locationRequest = LocationRequest.create().apply {
            smallestDisplacement = 100f
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallBack = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.lastOrNull()?.let {
                    launch { send(it) }
                }
            }
        }

        client.requestLocationUpdates(
            locationRequest,
            locationCallBack,
            Looper.getMainLooper()
        )

        awaitClose {
            client.removeLocationUpdates(locationCallBack)
        }
    }

}