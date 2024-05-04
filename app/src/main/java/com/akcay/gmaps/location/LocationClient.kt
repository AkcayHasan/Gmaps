package com.akcay.gmaps.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {

    fun getLocationUpdates(): Flow<Location>
}