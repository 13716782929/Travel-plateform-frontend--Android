package iss.nus.edu.sg.mygo.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.home.AttractionSearchActivity
import iss.nus.edu.sg.mygo.home.HotelMainActivity

class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = inflater.inflate(R.layout.search_fragment, container, false)

        // 获取 TextView 组件
        val hotelsCategoryText: TextView = binding.findViewById(R.id.search_hotels_category_txt)
        // 获取 TextView 组件
        val attractionsCategoryText: TextView = binding.findViewById(R.id.search_attractions_category_txt)

        // 设置点击监听器
        hotelsCategoryText.setOnClickListener {
            // 跳转到 HotelMainActivity
            val intent = Intent(requireContext(), HotelMainActivity::class.java)
            startActivity(intent)
        }

        // 设置点击监听器
        attractionsCategoryText.setOnClickListener {
            // 跳转到 HotelMainActivity
            val intent = Intent(requireContext(), AttractionSearchActivity::class.java)
            startActivity(intent)
        }

        return binding
    }
}
