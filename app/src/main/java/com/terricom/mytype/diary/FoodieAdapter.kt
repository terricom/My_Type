package com.terricom.mytype.diary

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.Logger
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.Shape
import com.terricom.mytype.data.Sleep
import com.terricom.mytype.databinding.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val TITLE = 0
private val DIARY_LIST = 1
private val SHAPE_RECORD = 2
private val SLEEP_RECORD = 3

class FoodieAdapter(val viewModel: DiaryViewModel
//                    , private val onClickListener: OnClickListener
) : ListAdapter<DataItem, RecyclerView.ViewHolder>(DiffCallback) {

//    class OnClickListener(val clickListener: (foodie: Foodie) -> Unit) {
//        fun onClick(foodie: Foodie) = clickListener(foodie)
//    }

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndSubmitList(list : List<Foodie>?) {
        adapterScope.launch {
            val newList = mutableListOf<DataItem>()
            if (list != null) {
                newList.add(DataItem.Header( viewModel))
                for (foodie in list) {
                    Logger.i("addHeaderAndSubmitList ")
                    newList.add(DataItem.FoodieList(foodie, viewModel))
                }
                newList.add(DataItem.ShapeItem(viewModel))
                newList.add(DataItem.SleepItem(viewModel))
            }
            withContext(Dispatchers.Main) {
                submitList(newList)
            }
        }
    }



    class ProductViewHolder(private var binding: ItemDiaryRecordBinding): RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {

        fun bind(foodie: Foodie, viewModel: DiaryViewModel) {

            binding.lifecycleOwner =this
            binding.foodie = foodie
            Log.i("Terri","$this foodie.foods null? = ${foodie.foods}")
            binding.recyclerDiaryFoodsItem.adapter = FoodlistAdapter(viewModel)
            (binding.recyclerDiaryFoodsItem.adapter as FoodlistAdapter).submitList(foodie.foods)
            binding.recyclerDiaryNutritionItem.adapter = NutritionlistAdapter(viewModel)
            (binding.recyclerDiaryNutritionItem.adapter as NutritionlistAdapter).submitList(foodie.nutritions)
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

    class SumViewHolder(private var binding: ItemDiarySumBinding): RecyclerView.ViewHolder(binding.root),
    LifecycleOwner {
        fun bind(viewModel: DiaryViewModel){
            binding.lifecycleOwner = this
            binding.viewModel = viewModel
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

    class ShapeViewHolder(private var binding: ItemDiaryShapeBinding): RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {
        fun bind( viewModel: DiaryViewModel){
//            binding.shape = shape
            binding.lifecycleOwner = this

            binding.viewModel = viewModel
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

    class SleepViewHolder(private var binding: ItemDiarySleepBinding): RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {
        fun bind( viewModel: DiaryViewModel){
//            binding.sleep = sleep
            binding.lifecycleOwner = this

            binding.viewModel = viewModel
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
            return oldItem == newItem
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        TITLE -> SumViewHolder(ItemDiarySumBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        DIARY_LIST -> ProductViewHolder(ItemDiaryRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        SHAPE_RECORD -> ShapeViewHolder(ItemDiaryShapeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        SLEEP_RECORD -> SleepViewHolder(ItemDiarySleepBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else -> throw IllegalArgumentException()
    }




    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //// to pass onClicklistener into adapter in CartFragment
        val product = getItem(position)
        when(holder){
            is SumViewHolder -> {
                val header = getItem(position) as DataItem.Header
                holder.bind( header.viewModel)
            }
            is ProductViewHolder -> {
                val header = getItem(position) as DataItem.FoodieList
                holder.bind(header.foodie, header.viewModel)
            }
            is ShapeViewHolder -> {
                val shape = getItem(position) as DataItem.ShapeItem
                holder.bind( shape.viewModel)
            }
            is SleepViewHolder -> {
                val sleep = getItem(position) as DataItem.SleepItem
                holder.bind( sleep.viewModel)
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is DataItem.Header -> TITLE
            is DataItem.FoodieList -> DIARY_LIST
            is DataItem.ShapeItem -> SHAPE_RECORD
            is DataItem.SleepItem -> SLEEP_RECORD
            else -> throw IllegalArgumentException()
        }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        when (holder) {
            is ProductViewHolder -> holder.markAttach()
            is SumViewHolder -> holder.markAttach()
            is ShapeViewHolder -> holder.markAttach()
            is SleepViewHolder -> holder.markAttach()
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        when (holder) {
            is ProductViewHolder -> holder.markDetach()
            is SumViewHolder -> holder.markDetach()
            is ShapeViewHolder -> holder.markDetach()
            is SleepViewHolder -> holder.markDetach()
        }
    }
}

sealed class DataItem {
    data class FoodieList(val foodie: Foodie, val viewModel: DiaryViewModel): DataItem(){
        override val id = foodie.timestamp.toString()
    }
    data class ShapeItem(
//        val shape: Shape,
        val viewModel: DiaryViewModel) : DataItem(){
        override val id = (Long.MIN_VALUE).toString()
    }
    data class SleepItem(
//        val sleep: Sleep,
        val viewModel: DiaryViewModel) : DataItem(){
        override val id = (Long.MIN_VALUE+1).toString()

    }
    data class Header (val viewModel: DiaryViewModel): DataItem() {
        override val id = (Long.MIN_VALUE+2).toString()
    }
    abstract val id: String


}
