package com.akcay.gmaps.ui.home.ui

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.akcay.gmaps.R
import com.akcay.gmaps.databinding.ActivityMapsBinding
import com.akcay.gmaps.location.GMapsLocationClient
import com.akcay.gmaps.util.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.akcay.gmaps.util.Constants.DEFAULT_ZOOM
import com.akcay.gmaps.util.LocationService
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    // TODO: kullanıcının konumu izlenecek ve her 100 metrede marker güncellenecek  ----> DONE
    // TODO: konum izleme ön planda ve arka planda çalışacak ----> DONE
    // TODO: kullanıcı manuel olarak izlemeyi açıp kapatabilecek ----> DONE
    // TODO: kullanıcı ekranda iki nokta arasında rota belirleyebilmeli ve bu rotayı istediğinde silebilmeli
    // TODO: kullanıcı rota belirlediğinde bunu silmedikçe uygulama arka plana alındığında da ekranda kalmalı

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var gMapsLocationClient: GMapsLocationClient

    private var locationFlow: Job? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Gmaps)
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        gMapsLocationClient = GMapsLocationClient(
            this,
            LocationServices.getFusedLocationProviderClient(this)
        )

        setListeners()
    }

    private fun setListeners() {
        with(binding) {
            btnStart.setOnClickListener {
                startLocationUpdates()
                Intent(applicationContext, LocationService::class.java).apply {
                    action = Constants.ACTION_START
                    startService(this)
                }
            }

            btnStop.setOnClickListener {
                stopLocationUpdates()
                Intent(applicationContext, LocationService::class.java).apply {
                    action = Constants.ACTION_STOP
                    startService(this)
                }
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapClickListener {
            Log.d("osman", "onMapReady: $it")
        }
    }

    private fun updateLocationUi(location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)
        mMap.clear()
        mMap.addMarker(
            MarkerOptions().position(currentLatLng).title("${location.latitude} / ${location.longitude}")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM))
    }

    private fun stopLocationUpdates() {
        locationFlow?.cancel()
    }


    private fun startLocationUpdates() {
        locationFlow = gMapsLocationClient.getLocationUpdates()
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            updateLocationUi(it)
        }.launchIn(lifecycleScope)
    }
}