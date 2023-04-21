package com.example.menutest.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.menutest.R
import com.example.menutest.databinding.FragmentHomeBinding
import com.example.menutest.databinding.FragmentSlideshowBinding
import com.example.menutest.ui.home.HomeViewModel

class SlideshowFragment : Fragment() {
    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!
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

        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
