package com.terricom.mytype.foodie

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.marginStart
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.Logger
import com.terricom.mytype.R
import com.terricom.mytype.databinding.ItemFoodieEditNewFoodBinding
import com.terricom.mytype.databinding.ItemFoodieFoodBinding
import kotlinx.android.synthetic.main.item_foodie_edit_new_food.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val IMAGEVIEW_TAG = "icon bitmap"
private val FOODLIST = 0
private val EDIT_FOOD = 1

class FoodAdapter (val viewModel: FoodieViewModel
//                   , private val onTouchListener: MyTouchListener
                    ,private val onLongClickListener: LongClickListener)
: ListAdapter<DataItem, RecyclerView.ViewHolder>(DiffCallback) {

    class LongClickListener: View.OnLongClickListener{
        override fun onLongClick(p0: View): Boolean {
            val data = ClipData.newPlainText("", "")
            val myShadow = MyDragShadowBuilder(p0)
            p0?.startDrag(data, MyDragShadowBuilder(p0), p0, 0)
            return true
        }
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

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndSubmitList(list : List<String>?) {
        adapterScope.launch {
            val newList = mutableListOf<DataItem>()
            if (list != null) {
                for (foodie in list) {
                    newList.add(DataItem.FoodieList(foodie, viewModel))
                }
                newList.add(DataItem.EditFood(viewModel))
            }
            withContext(Dispatchers.Main) {
                submitList(newList)
            }
        }
    }

    fun submitFoods(list : List<String>?) {
        adapterScope.launch {
            val newList = mutableListOf<DataItem>()
            if (list != null) {
                for (foodie in list) {
                    newList.add(DataItem.FoodieList(foodie, viewModel))
                }
            }
            withContext(Dispatchers.Main) {
                submitList(newList)
            }
        }
    }






    class MyTouchListener: View.OnTouchListener {

        var x1: Float = 0.0f
        var x2: Float = 0.0f

        override fun onTouch(p0: View, p1: MotionEvent): Boolean {
            if (p1.action == MotionEvent.ACTION_DOWN){
                x1 = p1.x
            }
            if (p1.action == MotionEvent.ACTION_UP){
                x2 = p1.x
            }
            Logger.i("x1 =$x1 x2 =$x2")
            return if (
                p1.action == MotionEvent.ACTION_DOWN
//                && ((x2 - x1) == 0.0)
            ) {
                val data = ClipData.newPlainText("", "")

//                val data = ClipData.Item(p0.tag as? CharSequence)
//                p0.tag = p0.findViewById<EditText>(R.id.food).text
                val dragData = ClipData(
                    p0.tag as? CharSequence,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    data.getItemAt(0))
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


    class FoodViewHolder(private var binding: ItemFoodieFoodBinding): RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {

        fun bind(food: String, viewModel: FoodieViewModel) {

            binding.lifecycleOwner =this
            binding.food.setText(food)
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

    class EditFoodViewHolder(private var binding: ItemFoodieEditNewFoodBinding): RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {

        fun bind(viewModel: FoodieViewModel) {

            binding.lifecycleOwner =this
            binding.viewModel = viewModel
            Logger.i("binding.food.text =${binding.food.text}")
            binding.addFood.setOnClickListener {
                viewModel.checkedAddNewFood()
                binding.food.setOnLongClickListener(LongClickListener())
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
    companion object DiffCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return (oldItem == newItem)
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when(viewType) {
        FOODLIST -> FoodViewHolder(ItemFoodieFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        EDIT_FOOD -> EditFoodViewHolder(ItemFoodieEditNewFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else -> throw IllegalArgumentException()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //// to pass onClicklistener into adapter in CartFragment
        when (holder){
            is FoodViewHolder -> {
                val food = getItem(position) as DataItem.FoodieList
                holder.itemView.setOnLongClickListener(onLongClickListener)
                holder.bind(food.string ,viewModel)
            }
            is EditFoodViewHolder -> {
                holder.itemView.setOnLongClickListener(onLongClickListener)
                holder.bind(viewModel)
            }
        }

    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is DataItem.FoodieList -> FOODLIST
            is DataItem.EditFood -> EDIT_FOOD

            else -> throw IllegalArgumentException()
        }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        when (holder){
            is FoodViewHolder -> holder.markAttach()
            is EditFoodViewHolder -> holder.markAttach()
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        when (holder){
            is FoodViewHolder -> holder.markDetach()
            is EditFoodViewHolder -> holder.markDetach()
        }
    }
}

sealed class DataItem {
    data class FoodieList(val string: String, val viewModel: FoodieViewModel): DataItem(){
        override val id = string
    }
    data class EditFood(val viewModel: FoodieViewModel) : DataItem(){
        override val id = (Long.MIN_VALUE).toString()
    }

    abstract val id: String


}