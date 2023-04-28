package com.example.menutest.ui.slideshow

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

class SlideshowFragment : Fragment() {
    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!
    private var callcount = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

//        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root
        doPlot()
        return root


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun doPlot() {
        //val plot = view.findViewById<XYPlot>(R.id.plotCharge)
        //callcount++
        val plot = binding.plotCharge
        //binding.textTest.text = String.format("%5d", callcount)
        val chargedArray = arrayOf<Number>(0, 15, 30, 45, 60, 50, 100)
        val timeArray = arrayOf<Number>(0, 4, 8, 12, 16, 20, 24)
        val chargedSeries : XYSeries = SimpleXYSeries(
            timeArray.asList(), //X vals
            chargedArray.asList(), //Y vals

            "Charge (%) vs Time (hours)"
        )

        val chargedFormat = LineAndPointFormatter(Color.RED, Color.GREEN, null, null)
        try {
            plot.addSeries(chargedSeries, chargedFormat)
        }
        catch (e: java.lang.NullPointerException){
            return
        }

        PanZoom.attach(plot) //add zooming and panning capabilities
    }
}
