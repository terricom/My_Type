package com.terricom.mytype.query

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.NavigationDirections
import com.terricom.mytype.databinding.FragmentQueryBinding

class QueryFragment: Fragment() {

    private val viewModel: QueryViewModel by lazy {
        ViewModelProviders.of(this).get(QueryViewModel::class.java)
    }
    private lateinit var binding: FragmentQueryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQueryBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val listFoodie = QueryFragmentArgs.fromBundle(arguments!!).selectedProperty

        if (!listFoodie.queryList.isNullOrEmpty()){
            viewModel.setQueryFoodie(listFoodie.queryList)
            binding.queryTotalArticle.setText("${listFoodie.queryList.size} 篇食記")
            binding.foodieTitle.setText("#${listFoodie.foodie}")
        }

        binding.buttonBack2Main.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
        }

        binding.recyclerQuery.adapter = QueryAdapter(viewModel, QueryAdapter.OnClickListener{
            findNavController().navigate(NavigationDirections.navigateToFoodieFragment(it))
        })

        binding.queryAverageShowInfo.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToQueryDialog())
        }

        viewModel.queryFoodie.observe(this, Observer {
            viewModel.calculateAverage(it)
            (binding.recyclerQuery.adapter as QueryAdapter).submitList(it)
        })





        return binding.root
    }
}