package com.example.menutest.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
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
        //reference edit_text and textview in the settings fragment
        val editText = root.findViewById<EditText>(R.id.edit_text)
        val displayText = root.findViewById<TextView>(R.id.display_text)
//        editText.setText("Enter Max Charge Percentage (0-100%): ")
        //confirm user input is valid
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //take user input text and convert to float or null
                val desiredChargeLevel = editText.text.toString().toFloatOrNull()
                //condition for valid user input
                if (desiredChargeLevel != null && desiredChargeLevel <= 100) {
                    displayText.text = "Max Charge Level is Set: ${desiredChargeLevel}%"
                } else {
                    displayText.text = "Invalid Charge Level"
                }
                Log.d("SETTINGS", "Settings Fragment: $desiredChargeLevel" )
                true
            } else {
                false
            }
        }
        return root
    }
}