package com.terricom.mytype.foodie

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
import com.terricom.mytype.tools.Logger
import com.terricom.mytype.databinding.ItemFoodieEditNewNutritionBinding
import com.terricom.mytype.databinding.ItemFoodieNutritionBinding


const val IMAGEVIEW_TAG_N = "icon bitmap"
private val NUTRITIONLIST = 0
private val EDIT_NUTRITION = 1

class NutritionAdapter(val viewModel: FoodieViewModel
//                       , private val onTouchListener: MyTouchListener
//                       ,private val onLongClickListenerNu: LongClickListenerNu
                    ,private val onClickListener: OnClickListener
)

    : ListAdapter<String, NutritionAdapter.NutritionViewHolder>(DiffCallback) {

    var addOrRemove : Boolean = true

    class OnClickListener(val clickListener: (nutrition: String) -> Unit) {
        fun onClick(nutrition: String) = clickListener(nutrition)
    }



    class LongClickListenerNu: View.OnLongClickListener{
        override fun onLongClick(p0: View): Boolean {
            val data = ClipData.newPlainText("", "")
            val myShadow = MyDragShadowBuilder(p0)
            p0?.startDrag(data, MyDragShadowBuilder(p0), p0, 0)
            return true
        }
    }


    class MyTouchListener: View.OnTouchListener {
        override fun onTouch(p0: View, p1: MotionEvent): Boolean {
            return if (p1.action == MotionEvent.ACTION_DOWN) {
//                val data = ClipData.newPlainText("", "")
                val data = ClipData.Item(p0.tag as? CharSequence)
                p0.tag = IMAGEVIEW_TAG_N
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




    class NutritionViewHolder(private var binding: ItemFoodieNutritionBinding): RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {

        fun bind(nutrition: String, viewModel: FoodieViewModel) {

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

    class EditNutritionViewHolder(private var binding: ItemFoodieEditNewNutritionBinding): RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {

        fun bind(viewModel: FoodieViewModel) {

            binding.lifecycleOwner =this
            binding.viewModel = viewModel
            Logger.i("binding.food.text =${binding.nutrition.text}")
            binding.addNutrition.setOnClickListener {
                viewModel.checkedAddNewNutrition()
                binding.nutrition.setOnLongClickListener(LongClickListenerNu())
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
        return NutritionViewHolder(ItemFoodieNutritionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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
                val nutrition = getItem(position)
        if (addOrRemove){
            holder.itemView.setOnClickListener {
                viewModel.dragToListNu(nutrition)
            }
        } else if (!addOrRemove){
            holder.itemView.setOnClickListener {
                viewModel.dragOutListNu(nutrition)
            }
        }
                holder.bind(nutrition ,viewModel)

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
