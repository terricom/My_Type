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
import com.terricom.mytype.Logger
import com.terricom.mytype.databinding.ItemFoodieEditNewNutritionBinding
import com.terricom.mytype.databinding.ItemFoodieNutritionBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


const val IMAGEVIEW_TAG_N = "icon bitmap"
private val NUTRITIONLIST = 0
private val EDIT_NUTRITION = 1

class NutritionAdapter(val viewModel: FoodieViewModel
//                       , private val onTouchListener: MyTouchListener
                       ,private val onLongClickListenerNu: LongClickListenerNu
)

    : ListAdapter<DataItemNu, RecyclerView.ViewHolder>(DiffCallback) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndSubmitListNu(list : List<String>?) {
        adapterScope.launch {
            val newList = mutableListOf<DataItemNu>()
            if (list != null) {
                for (foodie in list) {
                    newList.add(DataItemNu.NutritionList(foodie, viewModel))
                }
                newList.add(DataItemNu.EditNutrition(viewModel))
            }
            withContext(Dispatchers.Main) {
                submitList(newList)
            }
        }
    }

    fun submitNutritions(list : List<String>?) {
        adapterScope.launch {
            val newList = mutableListOf<DataItemNu>()
            if (list != null) {
                for (foodie in list) {
                    newList.add(DataItemNu.NutritionList(foodie, viewModel))
                }
            }
            withContext(Dispatchers.Main) {
                submitList(newList)
            }
        }
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
    companion object DiffCallback : DiffUtil.ItemCallback<DataItemNu>() {
        override fun areItemsTheSame(oldItem: DataItemNu, newItem: DataItemNu): Boolean {
            return (oldItem == newItem)
        }

        override fun areContentsTheSame(oldItem: DataItemNu, newItem: DataItemNu): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when(viewType) {
        NUTRITIONLIST -> NutritionViewHolder(ItemFoodieNutritionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        EDIT_NUTRITION -> EditNutritionViewHolder(ItemFoodieEditNewNutritionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else -> throw IllegalArgumentException()
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
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //// to pass onClicklistener into adapter in CartFragment
        when (holder){
            is NutritionViewHolder -> {
                val nutrition = getItem(position) as DataItemNu.NutritionList
                holder.itemView.setOnLongClickListener(onLongClickListenerNu)
                holder.bind(nutrition.string ,viewModel)
            }
            is EditNutritionViewHolder -> {
                holder.itemView.setOnLongClickListener(onLongClickListenerNu)
                holder.bind(viewModel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is DataItemNu.NutritionList -> NUTRITIONLIST
            is DataItemNu.EditNutrition -> EDIT_NUTRITION

            else -> throw IllegalArgumentException()
        }



    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        when (holder){
            is NutritionViewHolder -> holder.markAttach()
            is EditNutritionViewHolder -> holder.markAttach()
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        when (holder){
            is NutritionViewHolder -> holder.markDetach()
            is EditNutritionViewHolder -> holder.markDetach()
        }
    }
}

sealed class DataItemNu {
    data class NutritionList(val string: String, val viewModel: FoodieViewModel): DataItemNu(){
        override val id = string
    }
    data class EditNutrition(val viewModel: FoodieViewModel) : DataItemNu(){
        override val id = (Long.MIN_VALUE).toString()
    }

    abstract val id: String


}