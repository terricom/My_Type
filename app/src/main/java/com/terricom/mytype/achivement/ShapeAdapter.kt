package com.terricom.mytype.achivement

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.data.Shape
import com.terricom.mytype.databinding.ItemAchivementShapeBinding
import java.text.SimpleDateFormat

class ShapeAdapter(
    val viewModel: AchievementViewModel,
    private val onClickListener: OnClickListener

) : ListAdapter<Shape, ShapeAdapter.ProductViewHolder>(DiffCallback) {

    class OnClickListener( val clickListener: (shape: Shape) -> Unit ) {

        fun onClick(shape: Shape) = clickListener(shape)
    }

    class ProductViewHolder(
        private var binding: ItemAchivementShapeBinding
    ) : RecyclerView.ViewHolder(binding.root), LifecycleOwner {

        @SuppressLint("SimpleDateFormat")
        fun bind(shape: Shape, viewModel: AchievementViewModel) {

            binding.lifecycleOwner = this
            binding.shape = shape
            binding.viewModel = viewModel
            val sdf = SimpleDateFormat(App.applicationContext()
                .getString(R.string.simpledateformat_yyyy_MM_dd).replace("-","."))
            binding.time.text = sdf.format(shape.timestamp!!.time)

            binding.numberBodyAge.text =
                when (App.applicationContext().getString(R.string.float_round_one)
                    .format(shape.bodyAge)){
                    "null" -> "-"
                    "0.0" -> "-"
                    else -> App.applicationContext().getString(R.string.float_round_one)
                        .format(shape.bodyAge).split(".")[0]
                }
            binding.numberBodyFat.text =
                when (App.applicationContext().getString(R.string.float_round_one)
                    .format(shape.bodyFat)){
                    "null" -> "-"
                    "0.0" -> "-"
                    else -> App.applicationContext().getString(R.string.float_round_one)
                        .format(shape.bodyFat)
                }
            binding.numberBodyWater.text =
                when (App.applicationContext().getString(R.string.float_round_one)
                    .format(shape.bodyWater)){
                    "null" -> "-"
                    "0.0" -> "-"
                    else -> App.applicationContext().getString(R.string.float_round_one).
                        format(shape.bodyWater)
                }
            binding.numberMuscle.text =
                when (App.applicationContext().getString(R.string.float_round_one)
                    .format(shape.muscle)){
                    "null" -> "-"
                    "0.0" -> "-"
                    else -> App.applicationContext().getString(R.string.float_round_one)
                        .format(shape.muscle)
                }
            binding.numberTdee.text =
                when (App.applicationContext().getString(R.string.float_round_one)
                    .format(shape.tdee)){
                    "null" -> "-"
                    "0.0" -> "-"
                    else -> App.applicationContext().getString(R.string.float_round_one)
                        .format(shape.tdee).split(".")[0]
                }
            binding.numberWeight.text =
                when (App.applicationContext().getString(R.string.float_round_one)
                    .format(shape.weight)){
                    "null" -> "-"
                    "0.0" -> "-"
                    else -> App.applicationContext().getString(R.string.float_round_one)
                        .format(shape.weight)
                }

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

