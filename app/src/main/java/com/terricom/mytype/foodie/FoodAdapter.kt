package com.terricom.mytype.foodie

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.view.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.R
import com.terricom.mytype.databinding.ItemFoodieEditNewFoodBinding
import com.terricom.mytype.databinding.ItemFoodieFoodBinding
import com.terricom.mytype.tools.Logger
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
//                    ,private val onLongClickListener: LongClickListener
        ,private val onClickListener: FoodAdapter.OnClickListener)
: ListAdapter<com.terricom.mytype.foodie.DataItem, RecyclerView.ViewHolder>(DiffCallback) {

    var addOrRemove : Boolean = true

    class LongClickListener: View.OnLongClickListener{
        override fun onLongClick(p0: View): Boolean {
            val data = ClipData.newPlainText("", "")
            val myShadow = MyDragShadowBuilder(p0)
            p0?.startDrag(data, MyDragShadowBuilder(p0), p0, 0)
            return true
        }
    }

    class OnClickListener(val clickListener: (food: String) -> Unit) {
        fun onClick(food: String) = clickListener(food)
    }


    private class MyDragShadowBuilder(v: View) : View.DragShadowBuilder(v) {

        private val shadow = ColorDrawable(Color.LTGRAY)

        override fun onProvideShadowMetrics(size: Point, touch: Point) {
            val width: Int = view.width / 2

            val height: Int = view.height / 2

            shadow.setBounds(0, 0, width, height)

            size.set(width, height)

            touch.set(width / 2, height / 2)
        }

        override fun onDrawShadow(canvas: Canvas) {
            shadow.draw(canvas)
        }
    }

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndSubmitList(list : List<String>?) {
        adapterScope.launch {
            val newList = mutableListOf<com.terricom.mytype.foodie.DataItem>()
            if (list != null) {
                for (foodie in list) {
                    newList.add(com.terricom.mytype.foodie.DataItem.FoodieList(foodie, viewModel))
                }
                newList.add(com.terricom.mytype.foodie.DataItem.EditFood(viewModel))
            }
            withContext(Dispatchers.Main) {
                submitList(newList)
            }
        }
    }

    fun submitFoods(list : List<String>?) {
        adapterScope.launch {
            val newList = mutableListOf<com.terricom.mytype.foodie.DataItem>()
            if (list != null) {
                for (foodie in list) {
                    newList.add(com.terricom.mytype.foodie.DataItem.FoodieList(foodie, viewModel))
                }
            }
            withContext(Dispatchers.Main) {
                submitList(newList)
            }
        }
    }

    fun submitFoodsWithEdit(list : List<String>?) {
        adapterScope.launch {
            val newList = mutableListOf<com.terricom.mytype.foodie.DataItem>()
            if (list != null && list.contains("新增食物")) {
                for (foodie in list) {
                    if (foodie != "新增食物"){
                        newList.add(com.terricom.mytype.foodie.DataItem.FoodieList(foodie, viewModel))
                    }
                }
                newList.add(com.terricom.mytype.foodie.DataItem.EditFood(viewModel))
            } else if (list != null && !list.contains("新增食物")){
                for (foodie in list) {
                    if (foodie != "新增食物"){
                        newList.add(com.terricom.mytype.foodie.DataItem.FoodieList(foodie, viewModel))
                    }
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

        fun bind(food: com.terricom.mytype.foodie.DataItem, viewModel: FoodieViewModel) {

            binding.lifecycleOwner =this
            binding.food.setText(food.id)
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
            binding.food.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                    if (event.action === KeyEvent.ACTION_DOWN) {
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                                viewModel.dragToList(binding.food.text.toString())
                                binding.food.setText("")
                                v.nextFocusDownId = R.id.food
                                return true
                            }
                            else -> {
                            }
                        }
                    }
                    return false
                }
            })
//            binding.food.setOnClickListener {
//                binding.food.isFocusableInTouchMode = true
//                binding.food.post {
//                    binding.food.requestFocus()
//                    (binding.food.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
//                        .toggleSoftInputFromWindow(it.applicationWindowToken, InputMethodManager.SHOW_FORCED, 0)
//                }
//            }

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

    companion object DiffCallback : DiffUtil.ItemCallback<com.terricom.mytype.foodie.DataItem>() {
        override fun areItemsTheSame(oldItem: com.terricom.mytype.foodie.DataItem, newItem: com.terricom.mytype.foodie.DataItem): Boolean {
            return (oldItem == newItem)
        }

        override fun areContentsTheSame(oldItem: com.terricom.mytype.foodie.DataItem, newItem: com.terricom.mytype.foodie.DataItem): Boolean {
            return oldItem == newItem
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
        FOODLIST -> FoodViewHolder(ItemFoodieFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        EDIT_FOOD -> EditFoodViewHolder(ItemFoodieEditNewFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else -> throw IllegalArgumentException()
    }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //// to pass onClicklistener into adapter in CartFragment
        val string = getItem(position)
        when (holder){
            is FoodViewHolder -> {
                val food = getItem(position) as com.terricom.mytype.foodie.DataItem.FoodieList
//                holder.itemView.setOnLongClickListener(onLongClickListener)
                val addFoodList = mutableListOf<String>()
        if (addOrRemove){
            holder.itemView.setOnClickListener {
                viewModel.dragToList(string.id)
            }
        } else if (!addOrRemove){
            holder.itemView.setOnClickListener {
                viewModel.dragOutList(string.id)
            }
        }

        holder.bind(string ,viewModel)
            }
            is EditFoodViewHolder -> {
//                if (addOrRemove){
//                    holder.itemView.setOnClickListener {
//                        viewModel.dragToList(string.id)
//                    }
//                }
//                holder.itemView.setOnClickListener {
//
//                }
                holder.bind(viewModel)
            }
        }

    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is com.terricom.mytype.foodie.DataItem.FoodieList -> FOODLIST
            is com.terricom.mytype.foodie.DataItem.EditFood -> EDIT_FOOD

            else -> throw IllegalArgumentException()
        }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        when (holder){
            is FoodViewHolder ->
                holder.markAttach()
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
    data class FoodieList(val string: String, val viewModel: FoodieViewModel): com.terricom.mytype.foodie.DataItem(){
        override val id = string
    }
    data class EditFood(val viewModel: FoodieViewModel) : com.terricom.mytype.foodie.DataItem(){
        override val id = (Long.MIN_VALUE).toString()
    }

    abstract val id: String


}