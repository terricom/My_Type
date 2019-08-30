package com.terricom.mytype.diary

import android.content.ClipData
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.databinding.ItemDiaryNutritionBinding


class NutritionAdapter(val viewModel: DiaryViewModel, private val onTouchListener: MyTouchListener )
    : ListAdapter<String, NutritionAdapter.NutritionViewHolder>(DiffCallback) {

    private var gestureDetectorCompat: GestureDetectorCompat? = null


//    class OnTouchListener(val touchListener: (nutrition: String) -> Unit) {
//        fun onTouch(nutrition: String) = touchListener(nutrition)
//    }


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


    class NutritionViewHolder(private var binding: ItemDiaryNutritionBinding): RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {

        fun bind(nutrition: String, viewModel: DiaryViewModel) {

            binding.lifecycleOwner =this
            binding.nutrition.text = nutrition
            binding.viewModel = viewModel
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NutritionViewHolder {
        return NutritionViewHolder(ItemDiaryNutritionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: NutritionViewHolder, position: Int) {
        //// to pass onClicklistener into adapter in CartFragment
        val product = getItem(position)
        val motionEvent = onTouchListener
        product.let {
            holder.itemView.setOnTouchListener(onTouchListener)
            holder.bind(product, viewModel)
        }
    }

    override fun onViewAttachedToWindow(holder: NutritionViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: NutritionViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }
}