package com.example.menutest.ui.slideshow

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.androidplot.xy.*
import com.example.menutest.R
import com.example.menutest.databinding.FragmentHomeBinding
import com.example.menutest.databinding.FragmentSlideshowBinding
import com.example.menutest.ui.home.HomeViewModel
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
import java.util.*
import kotlin.math.roundToInt

//class SlideshowFragment : Fragment() {
//    private var _binding: FragmentSlideshowBinding? = null
//    private val binding get() = _binding!!
//    private var callcount = 0
//    private lateinit var homeViewModel: HomeViewModel
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        // using homeviewmodel to get the live data (battery charge level)
//        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
//
//        val slideshowViewModel =
//            ViewModelProvider(this).get(SlideshowViewModel::class.java)
//
////        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)
//        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
////        //get the real time battery charge level and initiate replot
////        homeViewModel.getData1().observe(viewLifecycleOwner) { newData ->
////
////            Log.d("SlideshowFragment", "New battery charge level: $newData")
////
////        }
//        //get the real time battery charge level and initiate replot
//        homeViewModel.getData1().observeForever { newData ->
//
//            Log.d("SlideshowFragment", "New battery charge level: $newData")
//            doPlot(newData)
//        }
//
////        doPlot()
//
//
//        return root
//
//
//    }
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    fun doPlot(newChargeLevel: String) {
//        //val plot = view.findViewById<XYPlot>(R.id.plotCharge)
//        //callcount++
//        val plot = binding.plotCharge
//        //binding.textTest.text = String.format("%5d", callcount)
////        val chargedArray = arrayOf<Number>(0, 15, 30, 45, 60, 50, 100)
//
//        val timeArray = arrayOf<Number>(0, 4, 8, 12, 16, 20, 24)
//        val chargedArray = timeArray.map { newChargeLevel.toFloatOrNull() ?: 0.0 }.toTypedArray()
//        val chargedSeries : XYSeries = SimpleXYSeries(
//            timeArray.asList(), //X vals
//            chargedArray.asList(), //Y vals
//
//            "Charge (%) vs Time (hours)"
//        )
//
//        val chargedFormat = LineAndPointFormatter(Color.RED, Color.GREEN, null, null)
////        try {
////            plot.addSeries(chargedSeries, chargedFormat)
////        }
////        catch (e: java.lang.NullPointerException){
////            return
////        }
//        plot.clear()  // Clear the existing series
//        plot.addSeries(chargedSeries, chargedFormat)
//        PanZoom.attach(plot) //add zooming and panning capabilities
//        plot.redraw()
//
//    }
//}
//
//class SlideshowFragment : Fragment() {
//    private var _binding: FragmentSlideshowBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var homeViewModel: HomeViewModel
//    private var timeCounter = 0
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
//
//        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        homeViewModel.getData1().observe(viewLifecycleOwner) { newData ->
////            Log.d("SlideshowFragment", "New battery charge level: $newData")
//            timeCounter++
//            val timeArray = generateTimeArray(timeCounter)
//            val chargedArray = generateChargedArray(newData, timeCounter)
//            doPlot(timeArray, chargedArray)
//        }
//
//        return root
//    }
//
//    private fun generateTimeArray(size: Int): Array<Number> {
//        return Array(size) { it + 1 } // Create an array counting up from 1 to size
//    }
//
//    private fun generateChargedArray(newChargeLevel: String, size: Int): Array<Number> {
//        val cleanedText = newChargeLevel.replace("%", "").trim()
//        Log.d("SlideshowFragment", "trim: $cleanedText")
//        val chargeLevel = cleanedText.toFloatOrNull() ?: 0.0
////        Log.d("SlideshowFragment", "New battery charge level: $chargeLevel")
//        return Array(size) { chargeLevel }
//    }
//
//    private fun doPlot(timeArray: Array<Number>, chargedArray: Array<Number>) {
//        val plot = binding.plotCharge
//
//        val chargedSeries: XYSeries = SimpleXYSeries(
//            timeArray.asList(),
//            chargedArray.asList(),
//            "Charge (%) vs Time (seconds)"
//        )
//
//        val chargedFormat = LineAndPointFormatter(Color.RED, Color.GREEN, null, null)
////        chargedFormat.setInterpolationParams(
////            CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)
////        )
//
//        plot.clear()
//        plot.addSeries(chargedSeries, chargedFormat)
//        plot.setRangeBoundaries(0, 100, BoundaryMode.FIXED) // Adjust the Y-axis range if needed
//        plot.redraw()
//    }
//}

class SlideshowFragment : Fragment() {
    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!

//    private var lastCounterValue: Int = 0
    private lateinit var homeViewModel: HomeViewModel
    private var timeCounter = 0
    private val timeArray: MutableList<Number> = mutableListOf()
    private val chargedArray: MutableList<Number> = mutableListOf()
    private var isPlotEnabled = false // Flag to track the plot state
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val toggleButton = root.findViewById<ToggleButton>(R.id.togglePlotButton)
        //button off by default
        toggleButton.isChecked = false
        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            isPlotEnabled = isChecked
//            if (isChecked) {
//                timeCounter = lastCounterValue // Resume where it left off
//            } else {
//                lastCounterValue = timeCounter // Save the last value
//            }
        }

        homeViewModel.getData1().observeForever { newData ->
//            if (isPlotEnabled) {
                timeCounter++
                val cleanedText = newData.replace("%", "").trim()
                val chargeLevel = cleanedText.toFloatOrNull() ?: 0.0f

                timeArray.add(timeCounter)
                chargedArray.add(chargeLevel)
                if(isPlotEnabled) {
                    doPlot(timeArray.toTypedArray(), chargedArray.toTypedArray())
                }
//            }
        }

        return root
    }

    private fun doPlot(timeArray: Array<Number>, chargedArray: Array<Number>) {
//        val plot = binding.plotCharge
        val plot = binding?.plotCharge ?: return // Handle null binding
        val chargedSeries: XYSeries = SimpleXYSeries(
            timeArray.asList(),
            chargedArray.asList(),
            "Charge (%) vs Time (seconds)"
        )

        val chargedFormat = LineAndPointFormatter(Color.RED, Color.GREEN, null, null)
        plot.clear()
        plot.addSeries(chargedSeries, chargedFormat)
        PanZoom.attach(plot) //add zooming and panning capabilities
        plot.setRangeBoundaries(0, 100, BoundaryMode.FIXED)
        plot.setDomainBoundaries(0,24,BoundaryMode.AUTO)
        plot.redraw()
    }

    override fun onDestroyView() {
        // Access the toggle button and set it to be off
        val root: View = binding.root
        val toggleButton = root.findViewById<ToggleButton>(R.id.togglePlotButton)
        toggleButton.isChecked = false

        super.onDestroyView()
        _binding = null

    }

    override fun onResume() {
        super.onResume()
        val root: View = binding.root
        val toggleButton = root.findViewById<ToggleButton>(R.id.togglePlotButton)
        toggleButton.isChecked = false
    }
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putIntegerArrayList("TIME_ARRAY", ArrayList(timeArray.map { it.toInt() }))
//        outState.putFloatArray("CHARGED_ARRAY", chargedArray.map { it.toFloat() }.toFloatArray())
//    }
//
//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        if (savedInstanceState != null) {
//            savedInstanceState.getIntegerArrayList("TIME_ARRAY")?.let { list ->
//                timeArray.clear()
//                timeArray.addAll(list.map { it })
//            }
//
//            savedInstanceState.getFloatArray("CHARGED_ARRAY")?.let { array ->
//                if (chargedArray.size == timeArray.size) {
//                    for (i in chargedArray.indices) {
//                        chargedArray[i] = array[i]
//                    }
//                } else {
//                    chargedArray.clear()
//                    chargedArray.addAll(array.map { it })
//                }
//            }
//
//            if (isPlotEnabled) {
//                doPlot(timeArray.toTypedArray(), chargedArray.toTypedArray())
//            }
//        }
//    }
}





