package com.akcay.gmaps.api.entities

import com.google.gson.annotations.SerializedName


data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    val legs: List<Leg>,
    val summary: String
)

data class Leg(
    val distance: Distance,
    val duration: Duration,
    val steps: List<Step>
)

data class Distance(
    val value: Int,
    val text: String
)

data class Duration(
    val value: Int,
    val text: String
)

data class Step(
    val distance: Distance,
    val duration: Duration,
    @SerializedName("start_location")
    val startLocation: LatLng,
    @SerializedName("end_location")
    val endLocation: LatLng,
    @SerializedName("overview_polyline")
    val polyline: Polyline,
    val maneuver: String?
)

data class LatLng(
    val lat: Double,
    val lng: Double
)

data class Polyline(
    val points: String
)
