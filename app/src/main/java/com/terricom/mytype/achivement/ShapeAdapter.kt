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
import java.text.SimpleDateFormat

class ShapeAdapter(val viewModel: AchievementViewModel
                    , private val onClickListener: OnClickListener
) : ListAdapter<Shape, ShapeAdapter.ProductViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (shape: Shape) -> Unit) {
        fun onClick(shape: Shape) = clickListener(shape)
    }


    class ProductViewHolder(private var binding: com.terricom.mytype.databinding.ItemAchivementShapeBinding): RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {

        fun bind(shape: Shape, viewModel: AchievementViewModel) {

            binding.lifecycleOwner =this
            binding.shape = shape
            binding.viewModel = viewModel
            val sdf = SimpleDateFormat("yyyy.MM.dd")
            binding.time.text = sdf.format(shape.timestamp!!.time)
            binding.numberBodyAge.text = if (shape.bodyAge.toString().split(".")[0] == null ||
                shape.bodyAge.toString().split(".")[0] == "null" ||
                shape.bodyAge.toString().split(".")[0] == "0.0" ) " - "
            else shape.bodyAge.toString().split(".")[0]
            binding.numberBodyFat.text = if (shape.bodyFat.toString() == null || shape.bodyFat.toString() == "null"
                || shape.bodyFat.toString() == "0.0") " - " else "%.1f".format(shape.bodyFat)
            binding.numberBodyWater.text = if (shape.bodyWater.toString() == null || shape.bodyWater.toString() == "null" || shape.bodyWater.toString() == "0.0")" - " else "%.1f".format(shape.bodyWater)
            binding.numberMuscle.text = if (shape.muscle.toString() == null || shape.muscle.toString() == "null" || shape.muscle.toString() == "0.0")" - " else "%.1f".format(shape.muscle)
            binding.numberTdeet.text = if (shape.tdee.toString().split(".")[0]==null || shape.tdee.toString().split(".")[0]=="null" || shape.tdee.toString().split(".")[0]=="0") " - " else shape.tdee.toString().split(".")[0]
            binding.numberWeight.text = if (shape.weight.toString() == null || shape.weight.toString() == "null" || shape.weight.toString() == "0.0") " - " else "%.1f".format(shape.weight)

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
            holder.itemView.setOnClickListener{
                onClickListener.onClick(product)
            }
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