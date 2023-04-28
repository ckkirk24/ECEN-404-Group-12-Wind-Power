package com.example.menutest.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.menutest.databinding.FragmentHomeBinding
import android.widget.ImageView
import com.example.menutest.MainActivity
import com.example.menutest.R

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
//    private var ChargeLevel = 0
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mainActivity = activity as MainActivity

        val chargePercent = mainActivity.chargePercent
        val powerOutput = mainActivity.powerOutput

        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
//        binding.myTextView.text = String.format("%5.1f %%", chargePercent)
//        binding.textView2.text = String.format("%5.3f W", powerOutput)
        return root
    }
    // empty battery display

    fun batteryUpdate(
         ChargeLevel: Int)
    {
//        val myImageView: ImageView = findViewById(R.id.baseline_battery)

//    if(ChargeLevel = 0){
//        battery0.setVisibility(View.VISIBLE);
//    }
//    else{
//        battery0.setVisibility(View.GONE);
//    }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}