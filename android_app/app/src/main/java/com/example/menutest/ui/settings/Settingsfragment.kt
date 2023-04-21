package com.example.menutest.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.menutest.R
import com.example.menutest.databinding.FragmentHomeBinding
import com.example.menutest.ui.home.HomeViewModel



class Settingsfragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        val root = inflater.inflate(R.layout.fragment_settings, container, false)


        return root
    }
}