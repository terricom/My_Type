package com.terricom.mytype.diary

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
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
import com.terricom.mytype.databinding.ItemDiaryNutritionBinding


class NutritionAdapter(val viewModel: DiaryViewModel
                       , private val onTouchListener: MyTouchListener
)
    : ListAdapter<String, NutritionAdapter.NutritionViewHolder>(DiffCallback) {


    class MyTouchListener: View.OnTouchListener {
        override fun onTouch(p0: View, p1: MotionEvent): Boolean {
            return if (p1.action == MotionEvent.ACTION_DOWN) {
//                val data = ClipData.newPlainText("", "")
                val data = ClipData.Item(p0.tag as? CharSequence)
                val dragData = ClipData(
                    p0.tag as CharSequence,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    data)
                val shadowBuilder = View.DragShadowBuilder(
                    p0
                )
                p0.startDrag(dragData, shadowBuilder, p0, 0)
                true
            } else {
                false
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

    private class MyDragShadowBuilder(v: View) : View.DragShadowBuilder(v) {

        private val shadow = ColorDrawable(Color.LTGRAY)

        // Defines a callback that sends the drag shadow dimensions and touch point back to the
        // system.
        override fun onProvideShadowMetrics(size: Point, touch: Point) {
            // Sets the width of the shadow to half the width of the original View
            val width: Int = view.width / 2

            // Sets the height of the shadow to half the height of the original View
            val height: Int = view.height / 2

            // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
            // Canvas that the system will provide. As a result, the drag shadow will fill the
            // Canvas.
            shadow.setBounds(0, 0, width, height)

            // Sets the size parameter's width and height values. These get back to the system
            // through the size parameter.
            size.set(width, height)

            // Sets the touch point's position to be in the middle of the drag shadow
            touch.set(width / 2, height / 2)
        }

        // Defines a callback that draws the drag shadow in a Canvas that the system constructs
        // from the dimensions passed in onProvideShadowMetrics().
        override fun onDrawShadow(canvas: Canvas) {
            // Draws the ColorDrawable in the Canvas passed in from the system.
            shadow.draw(canvas)
        }
    }
    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: NutritionViewHolder, position: Int) {
        //// to pass onClicklistener into adapter in CartFragment
        val product = getItem(position)
        product.let {
            holder.itemView.setOnTouchListener(onTouchListener)
//            holder.itemView.setOnLongClickListener{ v: View ->
//                // Create a new ClipData.
//                // This is done in two steps to provide clarity. The convenience method
//                // ClipData.newPlainText() can create a plain text ClipData in one step.
//
//                // Create a new ClipData.Item from the ImageView object's tag
//                val item = ClipData.Item(v.tag as? CharSequence)
//
//                // Create a new ClipData using the tag as a label, the plain text MIME type, and
//                // the already-created item. This will create a new ClipDescription object within the
//                // ClipData, and set its MIME type entry to "text/plain"
//                val dragData = ClipData(
//                    v.tag as? CharSequence,
//                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
//                    item)
//
//                // Instantiates the drag shadow builder.
//                val myShadow = MyDragShadowBuilder(v)
//
//                // Starts the drag
//                v.startDrag(
//                    dragData,   // the data to be dragged
//                    myShadow,   // the drag shadow builder
//                    null,       // no need to use local data
//                    0           // flags (not currently used, set to 0)
//                )
//            }
            holder.bind(product, viewModel)
        }
    }



    override fun onViewAttachedToWindow(holder: NutritionAdapter.NutritionViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: NutritionViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }
}