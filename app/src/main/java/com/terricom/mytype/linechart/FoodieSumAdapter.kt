package com.terricom.mytype.linechart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.data.FoodieSum
import com.terricom.mytype.databinding.ItemLinechartFoodiesumBinding
import com.terricom.mytype.tools.toDemicalPoint

class FoodieSumAdapter(
    val viewModel: LineChartViewModel
) : ListAdapter<FoodieSum, FoodieSumAdapter.ProductViewHolder>(DiffCallback) {

    class ProductViewHolder(private var binding: ItemLinechartFoodiesumBinding):
        RecyclerView.ViewHolder(binding.root), LifecycleOwner {

        fun bind(foodieSum: FoodieSum, viewModel: LineChartViewModel) {

            binding.lifecycleOwner =this
            binding.foodieSum = foodieSum
            binding.viewModel = viewModel
            binding.time.text = foodieSum.day.replace("-", ".")
            binding.numberWater.text = foodieSum.water.toDemicalPoint(1)
            binding.numberFruit.text = foodieSum.fruit.toDemicalPoint(1)
            binding.numberVegetable.text = foodieSum.vegetable.toDemicalPoint(1)
            binding.numberOil.text = foodieSum.oil.toDemicalPoint(1)
            binding.numberProtein.text = foodieSum.protein.toDemicalPoint(1)
            binding.numberCarbon.text = foodieSum.carbon.toDemicalPoint(1)

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


    companion object DiffCallback : DiffUtil.ItemCallback<FoodieSum>() {
        override fun areItemsTheSame(oldItem: FoodieSum, newItem: FoodieSum): Boolean {
            return (oldItem == newItem)
        }

        override fun areContentsTheSame(oldItem: FoodieSum, newItem: FoodieSum): Boolean {
            return oldItem == newItem
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ItemLinechartFoodiesumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)

        product.let {
            holder.bind(product, viewModel)
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