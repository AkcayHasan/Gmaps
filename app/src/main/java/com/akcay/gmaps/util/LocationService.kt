package com.akcay.gmaps.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.akcay.gmaps.R
import com.akcay.gmaps.location.GMapsLocationClient
import com.akcay.gmaps.util.Constants.ACTION_START
import com.akcay.gmaps.util.Constants.ACTION_STOP
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService: Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: GMapsLocationClient

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location",
                "Lokasyon",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Takip ediyor...")
            .setContentText("Lokasyonunuz: null")
            .setSmallIcon(R.drawable.baseline_map_24)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient.getLocationUpdates()
            .catch { e -> e.printStackTrace() }
            .onEach {
                val lat = it.latitude.toString()
                val lng = it.longitude.toString()
                val updatedNotification = notification.setContentText(
                    "Lokasyon: $lat / $lng"
                )
                notificationManager.notify(1, updatedNotification.build())
            }.launchIn(serviceScope)

        startForeground(1, notification.build())
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }


    override fun onCreate() {
        super.onCreate()
        locationClient = GMapsLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }
}