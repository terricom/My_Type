package com.terricom.mytype.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.data.Goal
import com.terricom.mytype.databinding.ItemProfileGoalBinding


class GoalAdapter(
    val viewModel: ProfileViewModel , private val onClickListener: OnClickListener
) : ListAdapter<Goal, GoalAdapter.GoalViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (goal : Goal) -> Unit) {
        fun onClick(goal: Goal) = clickListener(goal)
    }

    class GoalViewHolder(private var binding: ItemProfileGoalBinding, val viewModel: ProfileViewModel):
        RecyclerView.ViewHolder(binding.root), LifecycleOwner {
        fun bind(goal: Goal) {
                binding.lifecycleOwner = this
                binding.goal = goal
                binding.viewModel = viewModel

                binding.profileDateOfGoal.setOnClickListener {

                    if (viewModel.isGoalExpanded.value == true){

                        viewModel.closeGoal()

                    } else if (viewModel.isGoalExpanded.value == false){

                        viewModel.expandGoal()
                    }
                }

                viewModel.isGoalExpanded.observe(this, Observer {

                    if (it == true){

                        binding.buttonExpandArrow.animate().rotation(180.0f)
                        binding.recyclerProfileGoalSetting.maxHeight = App.applicationContext().resources.getDimension(R.dimen.profile_goal_animation).toInt()
                        binding.goalFoodieFirst.animate().translationY(-App.applicationContext().resources.getDimension(R.dimen.standard_155))
                        binding.goalFoodieSecond.animate().translationY(-App.applicationContext().resources.getDimension(R.dimen.standard_105))
                        binding.goalShape.animate().translationY(-App.applicationContext().resources.getDimension(R.dimen.standard_155))

                    } else if (it == false){

                        binding.buttonExpandArrow.animate().rotation(0.0f)
                        binding.recyclerProfileGoalSetting.maxHeight = App.applicationContext().resources.getDimension(R.dimen.profile_goal_expand).toInt()
                        binding.goalFoodieFirst.animate().translationY(App.applicationContext().resources.getDimension(R.dimen.standard_0))
                        binding.goalFoodieSecond.animate().translationY(App.applicationContext().resources.getDimension(R.dimen.standard_0))
                        binding.goalShape.animate().translationY(App.applicationContext().resources.getDimension(R.dimen.standard_0))
                    }
                })
                binding.executePendingBindings()
        }
        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        fun markAttach() {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun markDetach() {
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<Goal>() {
        override fun areItemsTheSame(oldItem: Goal, newItem: Goal): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: Goal, newItem: Goal): Boolean {
            return oldItem == newItem
        }
    }

    override fun onViewAttachedToWindow(holder: GoalViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: GoalViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        return GoalViewHolder(ItemProfileGoalBinding.inflate(
            LayoutInflater.from(parent.context), parent, false), viewModel)
    }


    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val product = getItem(position)

        product.let {
            holder.bind(product)
            holder.itemView.setOnClickListener{
                onClickListener.onClick(product)
            }
        }
    }
}