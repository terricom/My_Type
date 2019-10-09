package com.terricom.mytype.achievement

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.tools.FORMAT_YYYY_MM_DD
import com.terricom.mytype.data.Shape
import com.terricom.mytype.databinding.ItemAchievementShapeBinding
import com.terricom.mytype.tools.toDateFormat
import com.terricom.mytype.tools.toDemicalPoint

class ShapeAdapter(
    val viewModel: AchievementViewModel,
    private val onClickListener: OnClickListener

) : ListAdapter<Shape, ShapeAdapter.ProductViewHolder>(DiffCallback) {

    class OnClickListener( val clickListener: (shape: Shape) -> Unit ) {

        fun onClick(shape: Shape) = clickListener(shape)
    }

    class ProductViewHolder(
        private var binding: ItemAchievementShapeBinding
    ) : RecyclerView.ViewHolder(binding.root), LifecycleOwner {

        @SuppressLint("SimpleDateFormat")
        fun bind(shape: Shape, viewModel: AchievementViewModel) {

            binding.lifecycleOwner = this
            binding.shape = shape
            binding.viewModel = viewModel

            binding.time.text = shape.timestamp.toDateFormat(FORMAT_YYYY_MM_DD).replace("-",".")

            binding.numberBodyAge.text = shape.bodyAge.toDemicalPoint(0)

            binding.numberBodyFat.text = shape.bodyFat.toDemicalPoint(1)

            binding.numberBodyWater.text = shape.bodyWater.toDemicalPoint(1)

            binding.numberMuscle.text = shape.muscle.toDemicalPoint(1)

            binding.numberTdee.text = shape.tdee.toDemicalPoint(0)

            binding.numberWeight.text = shape.weight.toDemicalPoint(1)

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
        return ProductViewHolder(ItemAchievementShapeBinding.inflate(LayoutInflater
            .from(parent.context), parent, false))
    }


    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
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

