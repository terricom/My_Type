package com.terricom.mytype.goalsetting

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.terricom.mytype.App
import com.terricom.mytype.Logger
import com.terricom.mytype.MainActivity
import com.terricom.mytype.R
import com.terricom.mytype.databinding.FragmentGoalSettingBinding
import kotlinx.android.synthetic.main.activity_main.*

class GoalSettingFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentGoalSettingBinding.inflate(inflater)

        binding.buttonShaperecordSave.setOnClickListener{
            if (isConnected()) {
                Logger.i("NetworkConnection Network Connected.")
                //執行下載任務
            }else{
                Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT)
                //告訴使用者網路無法使用
            }
        }

        return binding.root
    }

    private fun isConnected(): Boolean{
        val connectivityManager = App.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).fabLayout1.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout2.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout3.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout4.visibility = View.INVISIBLE
        (activity as MainActivity).isFABOpen = false

    }
}