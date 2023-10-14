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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]


        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView1 = root.findViewById<TextView>(R.id.myTextView)
        val textView2 = root.findViewById<TextView>(R.id.textView2)
        val gifImageView = root.findViewById<ImageView>(R.id.gifwindturbine)

        homeViewModel.getData1().observe(viewLifecycleOwner) { newData ->
            // Update the first TextView with live updates
            textView1.text = newData
//            Log.d("HomeFragment", "LiveData1 observer triggered with data: $newData")
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

        return root

    }

//    override fun onResume() {
//        super.onResume()
//        Log.d("HomeFragment", "onResume called")
//        // Observe the data from the HomeViewModel for LiveData1
//    }
}

//    private var _binding: FragmentHomeBinding? = null
////    private var ChargeLevel = 0
//    // This property is only valid between onCreateView and
//    // onDestroyView.
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val mainActivity = activity as MainActivity
//
//        val chargePercent = mainActivity.chargePercent
//        val powerOutput = mainActivity.powerOutput
//
//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)
//
//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//        binding.myTextView.text = String.format("%5.1f %%", chargePercent)
//        binding.textView2.text = String.format("%5.3f W", powerOutput)
//        val thread2 = Thread(Runnable{
//            activity?.runOnUiThread {
//                // Update UI components here
//                updateData()
//            }
//        })
//        thread2.start()
//        return root
//    }
//    // empty battery display
//    private fun updateData() {
//        val mainActivity = activity as MainActivity
////        super.onResume()
////        while (true) {
//            val chargePercent = mainActivity.chargePercent
//            val powerOutput = mainActivity.powerOutput
////            activity?.runOnUiThread  {
//                binding.myTextView.text = String.format("%5.1f %%", chargePercent)
//                binding.textView2.text = String.format("%5.3f W", powerOutput)
////            }
////        }
//    }
//    fun batteryUpdate(
//         ChargeLevel: Int)
//    {
//        val myImageView: ImageView = findViewById(R.id.baseline_battery)

//    if(ChargeLevel = 0){
//        battery0.setVisibility(View.VISIBLE);
//    }
//    else{
//        battery0.setVisibility(View.GONE);
//    }
//    }
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}