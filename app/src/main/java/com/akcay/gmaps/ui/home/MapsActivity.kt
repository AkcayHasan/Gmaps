package com.akcay.gmaps.ui.home

import android.annotation.SuppressLint
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import com.akcay.gmaps.R
import com.akcay.gmaps.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.akcay.gmaps.util.Constants.DEFAULT_ZOOM
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    // TODO: kullanıcının konumu izlenecek ve her 100 metrede marker güncellenecek  ----> DONE
    // TODO: konum izleme ön planda ve arka planda çalışacak
    // TODO: kullanıcı manuel olarak izlemeyi açıp kapatabilecek ----> DONE
    // TODO: kullanıcı ekranda iki nokta arasında rota belirleyebilmeli ve bu rotayı istediğinde silebilmeli
    // TODO: kullanıcı rota belirlediğinde bunu silmedikçe uygulama arka plana alındığında da ekranda kalmalı

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallBack: LocationCallback
    private var lastKnownLocation: Location? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Gmaps)
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            smallestDisplacement = 100f
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallBack = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation.let {
                    lastKnownLocation = it
                    updateLocationUi()
                }
            }
        }

        setListeners()
    }

    private fun setListeners() {
        with(binding) {
            btnStart.setOnClickListener {
                startLocationUpdates()
            }

            btnStop.setOnClickListener {
                stopLocationUpdates()
            }
        }
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun updateLocationUi() {
        lastKnownLocation?.let {
            val currentLatLng = LatLng(it.latitude, it.longitude)
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(currentLatLng).title("${it.latitude} / ${it.longitude}"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM))
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallBack)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallBack,
            Looper.getMainLooper()
        )
    }
}