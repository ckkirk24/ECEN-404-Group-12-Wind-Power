package com.example.menutest.ui.gallery

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.menutest.R
import com.example.menutest.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val gifImageView = root.findViewById<ImageView>(R.id.gifturbineorientations) //gif with all orientations


       // val textView: TextView = binding.textGallery
        galleryViewModel.text.observe(viewLifecycleOwner) {
            //textView.text = it
        }

        // gif animation
        Glide.with(this)
            .asGif()
            .load(R.drawable.orientations) // Replace with your GIF resource
            .into(gifImageView)

        val comparisonTextView = root.findViewById<TextView>(R.id.scrollableTextView)
        val comparisonText = resources.getString(R.string.hawt_vawt_comparison)

        // Set the TextView to interpret HTML
        comparisonTextView.text = Html.fromHtml(comparisonText, Html.FROM_HTML_MODE_LEGACY)


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}