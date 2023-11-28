package com.example.menutest.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.menutest.databinding.FragmentHomeBinding
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.menutest.MainActivity
import com.example.menutest.R

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var textView1: TextView
    private lateinit var textView2: TextView
    private lateinit var emptyBattery: ImageView
    private lateinit var battery1Bar: ImageView
    private lateinit var battery2Bar: ImageView
    private lateinit var battery3Bar: ImageView
    private lateinit var battery4Bar: ImageView
    private lateinit var battery5Bar: ImageView
    private lateinit var battery6Bar: ImageView
    private lateinit var batteryFull: ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]


        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView1 = root.findViewById<TextView>(R.id.myTextView)
        val textView2 = root.findViewById<TextView>(R.id.textView2)
        val gifImageView = root.findViewById<ImageView>(R.id.gifwindturbine)
        val gifImageView2 = root.findViewById<ImageView>(R.id.gifhairdrier)

        emptyBattery = root.findViewById<ImageView>(R.id.empty_battery)
        battery1Bar = root.findViewById<ImageView>(R.id.battery_1_bar)
        battery2Bar = root.findViewById<ImageView>(R.id.battery_2_bar)
        battery3Bar = root.findViewById<ImageView>(R.id.battery_3_bar)
        battery4Bar = root.findViewById<ImageView>(R.id.battery_4_bar)
        battery5Bar = root.findViewById<ImageView>(R.id.battery_5_bar)
        battery6Bar = root.findViewById<ImageView>(R.id.battery_6_bar)
        batteryFull = root.findViewById<ImageView>(R.id.battery_full)

//        emptyBattery.visibility = View.VISIBLE


        homeViewModel.getData1().observe(viewLifecycleOwner) { newData ->
            // Update the first TextView with live updates
//            Log.d("NewData", "Value: $newData")

            textView1.text = newData
            updateBatteryImageVisibility(newData)

        }

        // Observe the data from the HomeViewModel for LiveData2
        homeViewModel.getData2().observe(viewLifecycleOwner) { newData ->
            // Update the second TextView with live updates
            textView2.text = newData
        }

        // Load and animate the GIF using Glide
        Glide.with(this)
            .asGif()
            .load(R.drawable.windturbine) // Replace with your GIF resource
            .into(gifImageView)

        Glide.with(this)
            .asGif()
            .load(R.drawable.hairdryer) // Replace with your GIF resource
            .into(gifImageView2)

        return root

    }
    private fun updateBatteryImageVisibility(text: String) {
        // Parse the text as a Float
        val cleanedText = text.replace("%", "").trim()


        val value = cleanedText.toFloatOrNull()
//        Log.d("HomeFragment", "Received text: $value") // Log the cleaned text
        // Check the value and set visibility accordingly
        if (value != null) {
//        Log.d("HomeFragment", "Value: $value") // Log the value

            if (value in 0.0..5.0) {
                emptyBattery.visibility = View.VISIBLE
                battery1Bar.visibility = View.GONE
                battery2Bar.visibility = View.GONE
                battery3Bar.visibility = View.GONE
                battery4Bar.visibility = View.GONE
                battery5Bar.visibility = View.GONE
                battery6Bar.visibility = View.GONE
                batteryFull.visibility = View.GONE
            } else if (value > 5.0 && value <= 16.0) {
                emptyBattery.visibility = View.GONE
                battery1Bar.visibility = View.VISIBLE
                battery2Bar.visibility = View.GONE
                battery3Bar.visibility = View.GONE
                battery4Bar.visibility = View.GONE
                battery5Bar.visibility = View.GONE
                battery6Bar.visibility = View.GONE
                batteryFull.visibility = View.GONE
            } else if (value > 16.0 && value <= 32.0) {
                emptyBattery.visibility = View.GONE
                battery1Bar.visibility = View.GONE
                battery2Bar.visibility = View.VISIBLE
                battery3Bar.visibility = View.GONE
                battery4Bar.visibility = View.GONE
                battery5Bar.visibility = View.GONE
                battery6Bar.visibility = View.GONE
                batteryFull.visibility = View.GONE
            } else if (value > 32.0 && value <= 48.0) {
                emptyBattery.visibility = View.GONE
                battery1Bar.visibility = View.GONE
                battery2Bar.visibility = View.GONE
                battery3Bar.visibility = View.VISIBLE
                battery4Bar.visibility = View.GONE
                battery5Bar.visibility = View.GONE
                battery6Bar.visibility = View.GONE
                batteryFull.visibility = View.GONE
            } else if (value > 48.0 && value <= 64.0) {
                emptyBattery.visibility = View.GONE
                battery1Bar.visibility = View.GONE
                battery2Bar.visibility = View.GONE
                battery3Bar.visibility = View.GONE
                battery4Bar.visibility = View.VISIBLE
                battery5Bar.visibility = View.GONE
                battery6Bar.visibility = View.GONE
                batteryFull.visibility = View.GONE
            } else if (value > 64.0 && value <= 80.0) {
                emptyBattery.visibility = View.GONE
                battery1Bar.visibility = View.GONE
                battery2Bar.visibility = View.GONE
                battery3Bar.visibility = View.GONE
                battery4Bar.visibility = View.GONE
                battery5Bar.visibility = View.VISIBLE
                battery6Bar.visibility = View.GONE
                batteryFull.visibility = View.GONE
            } else if (value > 80.0 && value <= 95.0) {
                emptyBattery.visibility = View.GONE
                battery1Bar.visibility = View.GONE
                battery2Bar.visibility = View.GONE
                battery3Bar.visibility = View.GONE
                battery4Bar.visibility = View.GONE
                battery5Bar.visibility = View.GONE
                battery6Bar.visibility = View.VISIBLE
                batteryFull.visibility = View.GONE
            } else if (value > 95.0 && value <= 100.0) {
                emptyBattery.visibility = View.GONE
                battery1Bar.visibility = View.GONE
                battery2Bar.visibility = View.GONE
                battery3Bar.visibility = View.GONE
                battery4Bar.visibility = View.GONE
                battery5Bar.visibility = View.GONE
                battery6Bar.visibility = View.GONE
                batteryFull.visibility = View.VISIBLE
            } else {
                // Handle the case where value is out of the specified ranges
                // For example, you can hide the ImageView in this case
                emptyBattery.visibility = View.GONE
                battery1Bar.visibility = View.GONE
                battery2Bar.visibility = View.GONE
                battery3Bar.visibility = View.GONE
                battery4Bar.visibility = View.GONE
                battery5Bar.visibility = View.GONE
                battery6Bar.visibility = View.GONE
                batteryFull.visibility = View.VISIBLE
            }
    } else {
        Log.d("HomeFragment", "Invalid value: $cleanedText") // Log that the value is invalid
        // Handle the case where value is not a valid float
        // For example, you can hide the ImageView in this case
            emptyBattery.visibility = View.VISIBLE
            battery1Bar.visibility = View.GONE
            battery2Bar.visibility = View.GONE
            battery3Bar.visibility = View.GONE
            battery4Bar.visibility = View.GONE
            battery5Bar.visibility = View.GONE
            battery6Bar.visibility = View.GONE
            batteryFull.visibility = View.GONE
    }
    }
}



