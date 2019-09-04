package com.terricom.mytype.achivement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.data.Shape
import com.terricom.mytype.databinding.ItemAchivementShapeBinding

class ShapeAdapter(val viewModel: AchievementViewModel
//                    , private val onClickListener: OnClickListener
) : ListAdapter<Shape, ShapeAdapter.ProductViewHolder>(DiffCallback) {

//    class OnClickListener(val clickListener: (foodie: Foodie) -> Unit) {
//        fun onClick(foodie: Foodie) = clickListener(foodie)
//    }


    class ProductViewHolder(private var binding: com.terricom.mytype.databinding.ItemAchivementShapeBinding): RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {

        fun bind(shape: Shape, viewModel: AchievementViewModel) {

            binding.lifecycleOwner =this
            binding.shape = shape
            binding.viewModel = viewModel
//            binding.time.text = viewModel.getTime(shape.timestamp)
            binding.numberBodyAge.text = shape.bodyAge.toString()
            binding.numberBodyFat.text = shape.bodyFat.toString()
            binding.numberBodyWater.text = shape.bodyWater.toString()
            binding.numberMuscle.text = shape.muscle.toString()
            binding.numberTdeet.text = shape.tdee.toString()
            binding.numberWeight.text = shape.weight.toString()

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


    companion object DiffCallback : DiffUtil.ItemCallback<Shape>() {
        override fun areItemsTheSame(oldItem: Shape, newItem: Shape): Boolean {
            return (oldItem == newItem)
        }

        override fun areContentsTheSame(oldItem: Shape, newItem: Shape): Boolean {
            return oldItem == newItem
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ItemAchivementShapeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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