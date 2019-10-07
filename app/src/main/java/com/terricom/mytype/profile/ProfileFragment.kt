package com.terricom.mytype.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SimpleItemAnimator
import com.terricom.mytype.*
import com.terricom.mytype.calendar.SpaceItemDecoration
import com.terricom.mytype.databinding.FragmentProfileBinding


class ProfileFragment: Fragment() {

    private val viewModel: ProfileViewModel by lazy {
        ViewModelProviders.of(this).get(ProfileViewModel::class.java)
    }

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner= this

        val linearSnapHelper = LinearSnapHelper().apply {
            attachToRecyclerView(binding.recyclerPuzzle)
        }

        binding.recyclerPuzzle.adapter = PuzzleAdapter(viewModel)
        binding.recyclerPuzzle.setOnScrollChangeListener { _, _, _, _, _ ->
            viewModel.onGalleryScrollChange(
                binding.recyclerPuzzle.layoutManager,
                linearSnapHelper
            )
        }

        (binding.recyclerGoal.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        binding.recyclerGoal.setHasFixedSize(true)

        viewModel.isPuzzleGot.observe(this, Observer {
            if (it){
                binding.profileGoalSettingReference.setOnClickListener {
                    findNavController().navigate(NavigationDirections.navigateToGoalSettingDialog())
                }

            } else {
                binding.profileGoalSettingReference.setOnClickListener {
                    findNavController().navigate(NavigationDirections.navigateToMessageDialog(MessageDialog.MessageType.MESSAGE.apply {
                        value.message = getString(R.string.profile_hint_add_goal)
                    }))
                }
            }
        })

        viewModel.isGoalExpanded.observe(this, Observer {

            if (it == true){

                viewModel.goal.observe(this, Observer {

                    it?.let {

                        when (it.size){

                            1 -> binding.recyclerGoal.minimumHeight =
                                App.applicationContext().resources.getDimension(R.dimen.min_height_goal_one).toInt()
                            else -> binding.recyclerGoal.minimumHeight =
                                App.applicationContext().resources.getDimension(R.dimen.min_height_goal_one).toInt()*it.size
                        }

                    }
                })

            } else if (it == false){

                binding.recyclerGoal.minimumHeight =
                    App.applicationContext().resources.getDimension(R.dimen.profile_recycler_goal_min).toInt()

            }
        })

        binding.profilePuzzleReference.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToMessageDialog(MessageDialog.MessageType.MESSAGE.apply {
                value.message = getString(R.string.profile_puzzle_info)
            }))
        }

        viewModel.puzzle.value?.let { puzzleList ->
            binding.recyclerPuzzle
                .scrollToPosition(puzzleList.size * 100)
        }

        viewModel.goal.observe(this, Observer {
            if (it.isNotEmpty()){
                binding.recyclerGoal.adapter = GoalAdapter(viewModel, GoalAdapter.OnClickListener{
                    findNavController().navigate(NavigationDirections.navigateToGoalSettingFragment(it))

                })
                (binding.recyclerGoal.adapter as GoalAdapter).submitList(it)
            }
        })

        if (!isConnected()){
            Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT).show()
            //告訴使用者網路無法使用
        }

        viewModel.isGoalGot.observe(this, Observer {
            if (it){
                binding.iconMyType.visibility = View.INVISIBLE
                binding.profileHintAddGoal.visibility = View.INVISIBLE
            } else if (!it){
                binding.iconMyType.visibility = View.VISIBLE
                binding.profileHintAddGoal.visibility = View.VISIBLE
            }
        })

        binding.recyclerPuzzle.addItemDecoration(
            SpaceItemDecoration(
                resources.getDimension(R.dimen.elevation_all).toInt(),
                true
            )
        )

        return binding.root
    }

}