package com.example.gpsdemostarting

import android.location.Location
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_save_location_list.*

class SaveLocationListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_location_list)

        var myApplication: MyApplication = applicationContext as MyApplication
        var savedLocation: List<Location> = myApplication.myLocation

        lv_wayPoints.adapter =
            ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1, savedLocation)

    }
}