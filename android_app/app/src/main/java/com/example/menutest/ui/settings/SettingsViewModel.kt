package com.example.menutest.ui.settings

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    var desiredChargeLevel: MutableLiveData<Float> = MutableLiveData()

    // Add a custom setter to the LiveData
    fun setDesiredChargeLevel(newValue: Float) {
        desiredChargeLevel.value = newValue
//        Log.d("SettingsViewModel", "desiredChargeLevel updated: $newValue")
    }
}
