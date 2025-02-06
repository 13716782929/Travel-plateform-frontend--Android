package iss.nus.edu.sg.mygo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import iss.nus.edu.sg.mygo.R

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 直接显示 ProfileFragment，不再进行登录检查
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }
}
