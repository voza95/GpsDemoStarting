package com.example.gpsdemostarting

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory


import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    var savedLocations: ArrayList<Location> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        var myApplication: MyApplication = applicationContext as MyApplication
        savedLocations = myApplication.myLocation

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        var lastLocationPlace: LatLng = sydney
        for (location in savedLocations){
            var latlon = LatLng(location.latitude, location.longitude)
            var markerOptions = MarkerOptions()
            markerOptions.position(latlon)
            markerOptions.title("Lat: ${location.latitude} Lon: ${location.longitude}")
            mMap.addMarker(markerOptions)
            lastLocationPlace = latlon
        }

        //Zoom to last location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocationPlace, 12.0f))

        mMap.setOnMarkerClickListener {
            //lets count the number of times the pin is clicked
            var clicks:Int = it.tag as Int
            if (clicks == null){
                clicks = 0
            }
            clicks++
            it.tag
            Toast.makeText(this, "Maker ${it.title} was $clicks clicked!", Toast.LENGTH_SHORT)
                .show()

            false
        }

    }
}