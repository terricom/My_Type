package com.terricom.mytype.foodie

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.databinding.ItemFoodieEditNewFoodBinding
import com.terricom.mytype.databinding.ItemFoodieFoodBinding
import com.terricom.mytype.tools.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val FOOD_LIST = 0
private const val EDIT_FOOD = 1

class FoodAdapter (val viewModel: FoodieViewModel
        ,private val onClickListener: OnClickListener)
: ListAdapter<DataItem, RecyclerView.ViewHolder>(DiffCallback) {

    var addOrRemove : Boolean = true

    class OnClickListener(val clickListener: (food: String) -> Unit) {
        fun onClick(food: String) = clickListener(food)
    }

    private val adapterScope = CoroutineScope(Dispatchers.Default)

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

    fun submitFoodsWithEdit(list : List<String>?) {
        adapterScope.launch {
            val newList = mutableListOf<DataItem>()
            if (list != null && list.contains(App.applicationContext().getString(R.string.foodie_add_food))) {
                for (foodie in list) {
                    if (foodie != App.applicationContext().getString(R.string.foodie_add_food)){
                        newList.add(DataItem.FoodieList(foodie, viewModel))
                    }
                }
                newList.add(DataItem.EditFood(viewModel))
            } else if (list != null && !list.contains(App.applicationContext().getString(R.string.foodie_add_food))){
                for (foodie in list) {
                    if (foodie != App.applicationContext().getString(R.string.foodie_add_food)){
                        newList.add(DataItem.FoodieList(foodie, viewModel))
                    }
                }
            }
            withContext(Dispatchers.Main) {
                submitList(newList)
            }
        }
    }

    class FoodViewHolder(private var binding: ItemFoodieFoodBinding): RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {

        fun bind(food: DataItem, viewModel: FoodieViewModel) {

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
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                                viewModel.addToFoodList(binding.food.text.toString())
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

    companion object DiffCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return (oldItem == newItem)
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
        FOOD_LIST -> FoodViewHolder(ItemFoodieFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        EDIT_FOOD -> EditFoodViewHolder(ItemFoodieEditNewFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else -> throw IllegalArgumentException()
    }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // to pass onClicklistener into adapter in CartFragment
        val string = getItem(position)
        when (holder){
            is FoodViewHolder -> {
                if (addOrRemove){
                    holder.itemView.setOnClickListener {
                        viewModel.addToFoodList(string.id)
                    }
                } else if (!addOrRemove){
                    holder.itemView.setOnClickListener {
                        viewModel.dropOutFoodList(string.id)
                    }
                }
                holder.bind(string ,viewModel)
            }
            is EditFoodViewHolder -> {
                holder.bind(viewModel)
            }
        }

    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is DataItem.FoodieList -> FOOD_LIST
            is DataItem.EditFood -> EDIT_FOOD

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
    data class FoodieList(val string: String, val viewModel: FoodieViewModel): DataItem(){
        override val id = string
    }
    data class EditFood(val viewModel: FoodieViewModel) : DataItem(){
        override val id = (Long.MIN_VALUE).toString()
    }

    abstract val id: String


}