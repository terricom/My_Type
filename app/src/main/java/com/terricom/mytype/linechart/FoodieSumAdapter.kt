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
import java.text.SimpleDateFormat

class FoodieSumAdapter(val viewModel: LinechartViewModel
//                    , private val onClickListener: OnClickListener
) : ListAdapter<FoodieSum, FoodieSumAdapter.ProductViewHolder>(DiffCallback) {

//    class OnClickListener(val clickListener: (foodie: Foodie) -> Unit) {
//        fun onClick(foodie: Foodie) = clickListener(foodie)
//    }


    class ProductViewHolder(private var binding: ItemLinechartFoodiesumBinding): RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {

        fun bind(foodieSum: FoodieSum, viewModel: LinechartViewModel) {

            binding.lifecycleOwner =this
            binding.foodieSum = foodieSum
            binding.viewModel = viewModel
            val sdf = SimpleDateFormat("yyyy.")
            binding.time.text = foodieSum.day!!.replace("-", ".")
            binding.numberWater.text = if (foodieSum.water.toString().split(".")[0] == null || foodieSum.water.toString().split(".")[0] == "null" ) " - " else "%.1f".format(foodieSum.water)
            binding.numberFruit.text = if (foodieSum.fruit.toString() == null || foodieSum.fruit.toString() == "null") " - " else "%.1f".format(foodieSum.fruit)
            binding.numberVegetable.text = if (foodieSum.vegetable.toString() == null || foodieSum.vegetable.toString() == "null")" - " else "%.1f".format(foodieSum.vegetable)
            binding.numberOil.text = if (foodieSum.oil.toString() == null || foodieSum.oil.toString() == "null")" - " else "%.1f".format(foodieSum.oil)
            binding.numberProtein.text = if (foodieSum.protein.toString().split(".")[0]==null || foodieSum.protein.toString().split(".")[0]=="null") " - " else "%.1f".format(foodieSum.protein)
            binding.numberCarbon.text = if (foodieSum.carbon.toString() == null || foodieSum.carbon.toString() == "null") " - " else "%.1f".format(foodieSum.carbon)

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
        //// to pass onClicklistener into adapter in CartFragment
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