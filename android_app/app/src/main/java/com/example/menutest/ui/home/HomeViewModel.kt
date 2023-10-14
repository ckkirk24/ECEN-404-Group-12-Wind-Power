package com.example.menutest.ui.home

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.menutest.R

class HomeViewModel : ViewModel() {
    private val dataLiveData1 = MutableLiveData<String>()
    private val dataLiveData2 = MutableLiveData<String>()

    fun setData1(newData: String) {
        dataLiveData1.value = newData
//        Log.d("HomeViewModel", "setData1 called with value: $newData")
    }

    fun setData2(newData: String) {
        dataLiveData2.value = newData
//        Log.d("HomeViewModel", "setData2 called with value: $newData")
    }

    fun getData1(): LiveData<String> {
        return dataLiveData1
    }

    fun getData2(): LiveData<String> {
        return dataLiveData2
    }
}