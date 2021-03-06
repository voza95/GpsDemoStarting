package com.example.gpsdemostarting

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val DEFAULT_UPDATE_INTERVAL = 30
    private val FAST_UPDATE_INTERVAL = 30
    private val PERMISSION_FINE_LOCATION = 99

    //    Location request is a config file for all settings related to FusedLocationProviderClient
    lateinit var mLocationRequest: LocationRequest

    //    Google's API for location services. The majority of the app functions using this class.
    lateinit var mFusedLocation: FusedLocationProviderClient

    lateinit var locationCallBack: LocationCallback

    //variable to remember if we are tracking location or not
    var updateOn: Boolean = false

    // current location
    lateinit var currentLocation: Location

    //list of saved locations
    var savedLocation: ArrayList<Location> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //set all properties of LocationRequest
        mLocationRequest = LocationRequest()

        mLocationRequest.interval = (DEFAULT_UPDATE_INTERVAL * 1000).toLong()
        mLocationRequest.fastestInterval = (FAST_UPDATE_INTERVAL * 1000).toLong()
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        locationCallBack = object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                p0?.lastLocation?.let { updateUIValues(location = it) }
            }
        }

        sw_gps.setOnClickListener {
            if (sw_gps.isChecked) {
                //most accurate - use GPS
                mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                tv_sensor.text = "Using GPS sensors"
            } else {
                mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                tv_sensor.text = "Using Towers + WIFI"
            }
        }
        sw_locationsupdates.setOnClickListener {
            if (sw_locationsupdates.isChecked){
                //turn on location tracking
                startLocationUpdates()
            }else{
                //turn off tracking
                stopLocationUpdates()
            }
        }

        btn_newWayPoint.setOnClickListener {
            //get the gps location
            if (::currentLocation.isInitialized){
                var myApplication: MyApplication = applicationContext as MyApplication
                savedLocation = myApplication.myLocation
                savedLocation.add(currentLocation)
            }
            //add the new location to the global list
        }

        btn_showWayPointList.setOnClickListener {
            val i = Intent(this@MainActivity,SaveLocationListActivity::class.java)
            startActivity(i)
        }

        btn_showMap.setOnClickListener {
            startActivity(Intent(this@MainActivity,MapsActivity::class.java))
        }

        updateGPS()
    }

    private fun startLocationUpdates() {
        tv_updates.text = "Location is being tracker"
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocation.requestLocationUpdates(mLocationRequest, locationCallBack, null)
            updateGPS()
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

    }

    private fun stopLocationUpdates() {
        tv_updates.text = "Location is NOT being tracked"
        tv_lat.text = "Not tracking location"
        tv_lon.text = "Not tracking location"
        tv_speed.text = "Not tracking location"
        tv_address.text = "Not tracking location"
        tv_accuracy.text = "Not tracking location"
        tv_altitude.text = "Not tracking location"
        tv_sensor.text = "Not tracking location"

        mFusedLocation.removeLocationUpdates(locationCallBack)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            PERMISSION_FINE_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "This app required permission to be granted",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }

    private fun updateGPS() {
        //get permissions from the user to track GPS
        // get the current location form the fused client
        // update the UI - i.e. set all properties in their associated text view items.
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this@MainActivity)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //user provided the permission
            mFusedLocation.lastLocation.addOnSuccessListener(this, object: OnSuccessListener<Location>{
                override fun onSuccess(location: Location?) {
                    // we got permission, Put the value of location. XXX into UI components.
                    if (location != null) {
                        currentLocation = location
                    }
                    location?.let { updateUIValues(location = it) }

                }
            })
        }else{
            //permission not yet provided
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_FINE_LOCATION)
            }
        }
    }

    private fun updateUIValues(location: Location) {
        //update all of the text view objects with new location
        tv_lat.text = location.latitude.toString()
        tv_lon.text = location.longitude.toString()
        tv_accuracy.text = location.accuracy.toString()

        if (location.hasAltitude()){
            tv_altitude.text  = location.altitude.toString()
        }else{
            tv_altitude.text = "N/A"
        }

        if (location.hasSpeed()){
            tv_speed.text = location.speed.toString()
        }else{
            tv_speed.text = "N/A"
        }

        var grocoder = Geocoder(this@MainActivity)
        try {
            var addresses: List<Address> = grocoder.getFromLocation(location.latitude, location.longitude, 1)
            tv_address.text = addresses[0].getAddressLine(0)
        }catch (e:Exception){
            tv_address.text = "N/A"
            e.printStackTrace()
        }

        var myApplication: MyApplication = applicationContext as MyApplication
        savedLocation = myApplication.myLocation

        // show the number of waypoints saved.
        tv_countOfCrumbs.text = savedLocation.size.toString()

    }
}
