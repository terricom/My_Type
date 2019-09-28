package com.terricom.mytype.query

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.NavigationDirections
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.databinding.ItemQueryFoodieBinding

class QueryAdapter(val viewModel: QueryViewModel
                      , private val onClickListener: OnClickListener
) : ListAdapter<Foodie, QueryAdapter.ProductViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (foodie: Foodie) -> Unit) {
        fun onClick(foodie: Foodie) = clickListener(foodie)
    }


    class ProductViewHolder(private var binding: ItemQueryFoodieBinding): RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {

        fun bind(food: Foodie, viewModel: QueryViewModel) {

            binding.lifecycleOwner =this
            binding.foodie = food
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
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


    companion object DiffCallback : DiffUtil.ItemCallback<Foodie>() {
        override fun areItemsTheSame(oldItem: Foodie, newItem: Foodie): Boolean {
            return (oldItem == newItem)
        }

        override fun areContentsTheSame(oldItem: Foodie, newItem: Foodie): Boolean {
            return oldItem == newItem
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ItemQueryFoodieBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        //// to pass onClicklistener into adapter in CartFragment
        val product = getItem(position)

        holder.bind(product, viewModel)
        holder.itemView.setOnClickListener {
            Navigation.findNavController(it).navigate(NavigationDirections.navigateToFoodieFragment(product))
        }
    }
    override fun onViewAttachedToWindow(holder: ProductViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: ProductViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }
}