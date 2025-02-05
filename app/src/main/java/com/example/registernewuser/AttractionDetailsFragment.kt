package com.example.registernewuser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.registernewuser.databinding.FragmentAttractionDetailsBinding

class AttractionDetailsFragment : Fragment() {

    private var _binding: FragmentAttractionDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAttractionDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get data passed from AttractionActivity
        val name = arguments?.getString("attraction_name")
        val location = arguments?.getString("attraction_location")
        val description = arguments?.getString("attraction_description")
        val imageResId = arguments?.getInt("attraction_image")

        // Bind data to views
        binding.attractionName.text = name
        binding.attractionLocation.text = location
        binding.attractionDescription.text = description
        imageResId?.let {
            binding.attractionImage.setImageResource(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
