package com.terricom.mytype.query

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.App
import com.terricom.mytype.NavigationDirections
import com.terricom.mytype.R
import com.terricom.mytype.databinding.FragmentQueryBinding
import com.terricom.mytype.tools.getVmFactory
import com.terricom.mytype.tools.toDemicalPoint

class QueryFragment: Fragment() {

    private val viewModel by viewModels<QueryViewModel> { getVmFactory() }
    private lateinit var binding: FragmentQueryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQueryBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.goal.observe(this, Observer {

            if (it.isNotEmpty()){

                viewModel.goalWater.value = it[0].water.toDemicalPoint(1)
                viewModel.goalCarbon.value = it[0].carbon.toDemicalPoint(1)
                viewModel.goalFruit.value = it[0].fruit.toDemicalPoint(1)
                viewModel.goalOil.value = it[0].oil.toDemicalPoint(1)
                viewModel.goalProtein.value = it[0].protein.toDemicalPoint(1)
                viewModel.goalVegetable.value = it[0].vegetable.toDemicalPoint(1)
            }else {
                viewModel.goalWater.value = "0.0"
                viewModel.goalCarbon.value = "0.0"
                viewModel.goalFruit.value = "0.0"
                viewModel.goalOil.value = "0.0"
                viewModel.goalProtein.value = "0.0"
                viewModel.goalVegetable.value = "0.0"
            }
        })

        QueryFragmentArgs.fromBundle(arguments!!).selectedProperty?.let {listFoodie ->

            listFoodie.queryList?.let {
                viewModel.setQueryFoodie(it)
                binding.queryTotalArticle.setText(App.applicationContext().getString(R.string.query_foodie_total_number, it.size))
            }
            binding.foodieTitle.text = App.applicationContext().getString(R.string.query_foodie_title, listFoodie.foodie)

        }

        binding.buttonBack2Main.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
        }

        binding.recyclerQuery.adapter = QueryAdapter(viewModel)

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