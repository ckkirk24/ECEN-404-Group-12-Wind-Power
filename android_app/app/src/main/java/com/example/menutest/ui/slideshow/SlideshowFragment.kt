package com.example.menutest.ui.slideshow

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.androidplot.xy.LineAndPointFormatter
import com.androidplot.xy.SimpleXYSeries
import com.androidplot.xy.XYGraphWidget
import com.androidplot.xy.XYPlot
import com.example.menutest.R
import com.example.menutest.databinding.FragmentHomeBinding
import com.example.menutest.databinding.FragmentSlideshowBinding
import com.example.menutest.ui.home.HomeViewModel
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
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
        callcount++
        val plot = binding.plotCharge
        binding.textTest.text = String.format("%5d", callcount)
        val chargedArray = arrayOf<Number>(4.5, 7.5, 9.0, 7.5)
        val chargedSeries = SimpleXYSeries(
            chargedArray.asList(),
            SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
            "Charge vs Time"
        )
        val chargedFormat = LineAndPointFormatter(Color.RED, Color.GREEN, null, null)
        try {
            plot.addSeries(chargedSeries, chargedFormat)
        }
        catch (e: java.lang.NullPointerException){
            return
        }
        plot.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = object : Format() {
            override fun format(
                obj: Any?,
                toAppendTo: StringBuffer?,
                pos: FieldPosition?
            ): StringBuffer {
                val i = (obj as Number).toFloat().roundToInt()
                if (toAppendTo != null) {
                    return toAppendTo.append("")
                }
                return StringBuffer()
            }

            override fun parseObject(p0: String?, p1: ParsePosition?): Any? {
                return null

            }
        }
    }
}
