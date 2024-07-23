package com.example.weatherforecast.ui.viewmodel


//Create a singleton object that will hold the instance of SharedViewModel and initialize it.
//that instance we'll use for access livedata in many Fragments as necessary
object SharedViewModelHolder {
    lateinit var sharedViewModel: SharedViewModel
        private set

    fun initialize(sharedViewModel: SharedViewModel) {
        this.sharedViewModel = sharedViewModel
    }
}
