package com.terricom.mytype.foodie

import android.content.ClipData
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.databinding.ItemFoodieFoodBinding
import com.terricom.mytype.diary.DiaryViewModel

class FoodAdapter (viewModel: FoodieViewModel, private val onTouchListener: MyTouchListener )
: ListAdapter<String, FoodAdapter.FoodViewHolder>(DiffCallback) {


    class MyTouchListener: View.OnTouchListener {
        override fun onTouch(p0: View, p1: MotionEvent): Boolean {
            if (p1.action == MotionEvent.ACTION_DOWN) {
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(
                    p0
                )
                p0.startDrag(data, shadowBuilder, p0, 0)
                p0.visibility = View.INVISIBLE
                return true
            } else {
                return false
            }
        }
    }


    class FoodViewHolder(private var binding: ItemFoodieFoodBinding): RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {

        fun bind(food: String, viewModel: DiaryViewModel) {

            binding.lifecycleOwner =this
            binding.food.text = food
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

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [Product]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return (oldItem == newItem)
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        return FoodViewHolder(ItemFoodieFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        //// to pass onClicklistener into adapter in CartFragment
        val product = getItem(position)
        product.let {
            holder.itemView.hasOnClickListeners()
            holder.itemView.setOnTouchListener(onTouchListener)
        }
    }

    override fun onViewAttachedToWindow(holder: FoodViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: FoodViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }
}