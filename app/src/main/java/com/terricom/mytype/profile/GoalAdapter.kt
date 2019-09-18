package com.terricom.mytype.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.data.Goal
import com.terricom.mytype.databinding.ItemProfileGoalBinding

class GoalAdapter(val viewModel: ProfileViewModel) : ListAdapter<Goal, GoalAdapter.GoalViewHolder>(DiffCallback) {

    class GoalViewHolder(private var binding: ItemProfileGoalBinding, val viewModel: ProfileViewModel):
        RecyclerView.ViewHolder(binding.root), LifecycleOwner {
        fun bind(goal: Goal) {
                binding.lifecycleOwner = this
                binding.goal = goal
                binding.viewModel = viewModel
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
        holder.bind(getItem(position))
    }
}