package com.example.gpsdemostarting

import android.app.Application
import android.location.Location

class MyApplication: Application() {

    lateinit var myLocation: ArrayList<Location>



    override fun onCreate() {
        super.onCreate()
        myLocation = ArrayList()
    }
}