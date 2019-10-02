package com.terricom.mytype.diary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.databinding.ItemDiaryFoodlistBinding

class FoodlistAdapter(
    val viewModel: DiaryViewModel,
    private val onClickListener: OnClickListener
) : ListAdapter<String, FoodlistAdapter.FoodsViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (foodie: String) -> Unit) {
        fun onClick(foodie: String) = clickListener(foodie)
    }

    class FoodsViewHolder(
        private var binding: ItemDiaryFoodlistBinding
    ): RecyclerView.ViewHolder(binding.root), LifecycleOwner {

        fun bind(food: String, viewModel: DiaryViewModel) {

            binding.lifecycleOwner =this
            binding.food.text = food

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


    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return (oldItem == newItem)
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodsViewHolder {

        return FoodsViewHolder(ItemDiaryFoodlistBinding.inflate(LayoutInflater
            .from(parent.context), parent, false))
    }


    override fun onBindViewHolder(holder: FoodsViewHolder, position: Int) {
        val product = getItem(position)

        holder.bind(product, viewModel)
        holder.itemView.setOnClickListener {
            viewModel.queryFoodieFoods(product)
        }
    }
    override fun onViewAttachedToWindow(holder: FoodsViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: FoodsViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }
}