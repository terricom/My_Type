package com.terricom.mytype.diary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.App
import com.terricom.mytype.NavigationDirections
import com.terricom.mytype.R
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.databinding.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val TITLE = 0
private val DIARY_LIST = 1
private val SHAPE_RECORD = 2
private val SLEEP_RECORD = 3
private val PLACEHOLDER = 4

class FoodieAdapter(
    val viewModel: DiaryViewModel,
    private val onClickListener: OnClickListener
) : ListAdapter<DataItem, RecyclerView.ViewHolder>(DiffCallback) {

    var deleteOrNot = false

    class OnClickListener(val clickListener: (foodie: Foodie) -> Unit) {
        fun onClick(foodie: Foodie) = clickListener(foodie)
    }

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun diarySubmitList(list : List<Foodie>?) {
        adapterScope.launch {
            val newList = mutableListOf<DataItem>()
            if (list != null) {
                newList.add(DataItem.Header( viewModel))
                if (viewModel.fireShape.value != null){
                    newList.add(DataItem.ShapeItem(viewModel))
                }
                if (viewModel.fireSleep.value != null){
                    newList.add(DataItem.SleepItem(viewModel))
                }
                if (list.isEmpty()){
                    newList.add(DataItem.PlaceHolder(viewModel))
                }else {
                    for (foodie in list) {
                        newList.add(DataItem.FoodieList(foodie, viewModel))
                    }
                }
            }
            withContext(Dispatchers.Main) {
                submitList(newList)
            }
        }
    }

    class RecordViewHolder(
        private var binding: ItemDiaryRecordBinding
    ): RecyclerView.ViewHolder(binding.root), LifecycleOwner {

        fun bind(foodie: Foodie, viewModel: DiaryViewModel) {

            binding.lifecycleOwner =this

            binding.foodie = foodie

            binding.recyclerDiaryFoodsItem.adapter =
                FoodlistAdapter(viewModel, FoodlistAdapter.OnClickListener{
            })

            (binding.recyclerDiaryFoodsItem.adapter as FoodlistAdapter).submitList(foodie.foods)
            binding.recyclerDiaryNutritionItem.adapter =
                NutritionlistsAdapter(viewModel, NutritionlistsAdapter.OnClickListener{
                })

            (binding.recyclerDiaryNutritionItem.adapter as NutritionlistsAdapter).submitList(foodie.nutritions)

            if (!foodie.memo.isNullOrEmpty()){

                binding.foodieMemo.visibility = View.VISIBLE
            } else {
                binding.foodieMemo.visibility = View.GONE
            }
            binding.viewModel = viewModel

            binding.imageView2.rotation =
                if (binding.imageView2.maxHeight > binding.imageView2.maxWidth)90.0f else 0.0f
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

    class SumViewHolder(
        private var binding: ItemDiarySumBinding
    ): RecyclerView.ViewHolder(binding.root), LifecycleOwner {

        fun bind(viewModel: DiaryViewModel){
            binding.lifecycleOwner = this
            binding.viewModel = viewModel
            binding.diaryItemSumShowInfo.setOnClickListener {

                findNavController(it).navigate(NavigationDirections.navigateToReferenceDialog())
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

    class PlaceholderViewHolder(
        private var binding: ItemDiaryNoFoodieBinding
    ): RecyclerView.ViewHolder(binding.root), LifecycleOwner {

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

    class ShapeViewHolder(
        private var binding: ItemDiaryShapeBinding
    ): RecyclerView.ViewHolder(binding.root), LifecycleOwner {

        fun bind( viewModel: DiaryViewModel){

            binding.lifecycleOwner = this
            viewModel.fireShape.value?.let {

                if ((it.weight ?: 0.0f).equals(0.0f) || it.weight == null){
                    binding.numberWeight.text = "-"
                }else {
                    binding.numberWeight.text = App.applicationContext()
                        .getString(R.string.float_round_one).format(it.weight)
                }
                if ((it.bodyWater ?: 0.0f).equals(0.0f) || it.bodyWater == null){
                    binding.numberBodyWater.text = "-"
                }else {
                    binding.numberBodyWater.text = App.applicationContext()
                        .getString(R.string.float_round_one).format(it.bodyWater)
                }
                if ((it.bodyFat ?: 0.0f).equals(0.0f) || it.bodyFat == null){
                    binding.numberBodyFat.text = "-"
                }else {
                    binding.numberBodyFat.text = App.applicationContext()
                        .getString(R.string.float_round_one).format(it.bodyFat)
                }
                if ((it.tdee ?: 0.0f).equals(0f) || it.tdee == null){
                    binding.numberTdee.text = "-"
                }else {
                    binding.numberTdee.text = App.applicationContext()
                        .getString(R.string.float_round_zero).format(it.tdee)
                }
                if ((it.muscle ?: 0.0f).equals(0.0f) || it.muscle == null){
                    binding.numberMuscle.text = "-"
                }else {
                    binding.numberMuscle.text = App.applicationContext()
                        .getString(R.string.float_round_one).format(it.muscle)
                }
                if ((it.bodyAge ?: 0f).equals(0f) || it.bodyAge == null){
                    binding.numberBodyAge.text = "-"
                }else {
                    binding.numberBodyAge.text = App.applicationContext()
                        .getString(R.string.float_round_one).format(it.bodyAge)
                }

            }

            binding.imageView4.setOnClickListener {
                findNavController(it).navigate(NavigationDirections.navigateToShapeRecordDialog())
            }
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

    class SleepViewHolder(
        private var binding: ItemDiarySleepBinding
    ): RecyclerView.ViewHolder(binding.root), LifecycleOwner {
        fun bind( viewModel: DiaryViewModel){

            binding.lifecycleOwner = this

            viewModel.fireSleep.value?.let {
                it.goToBed?.let {
                    binding.tvBedTime.text = viewModel.getTime(it)
                }
                it.wakeUp?.let {
                    binding.tvWakeTime.text = viewModel.getTime(it)
                }
                it.sleepHr?.let {
                    binding.numberSleep.text = App.applicationContext()
                        .getString(R.string.float_round_one).format(it)
                }
            }
            binding.diaryItemSleepShowInfo.setOnClickListener {

                findNavController(it).navigate(NavigationDirections.navigateToSleepDialog())
            }

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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {

            TITLE -> SumViewHolder(ItemDiarySumBinding
                .inflate(LayoutInflater.from(parent.context), parent, false))
            DIARY_LIST -> RecordViewHolder(ItemDiaryRecordBinding
                .inflate(LayoutInflater.from(parent.context), parent, false))
            SHAPE_RECORD -> ShapeViewHolder(ItemDiaryShapeBinding
                .inflate(LayoutInflater.from(parent.context), parent, false))
            SLEEP_RECORD -> SleepViewHolder(ItemDiarySleepBinding
                .inflate(LayoutInflater.from(parent.context), parent, false))
            PLACEHOLDER -> PlaceholderViewHolder(ItemDiaryNoFoodieBinding
                .inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException()
        }




    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder){
            is SumViewHolder -> {
                val header = getItem(position) as DataItem.Header
                holder.bind( header.viewModel)
            }
            is RecordViewHolder -> {
                val header = getItem(position) as DataItem.FoodieList
                holder.bind(header.foodie, header.viewModel)

                    holder.itemView.setOnLongClickListener {
                        deleteOrNot = true
                        it.findViewById<ImageView>(R.id.add2Garbage).visibility = View.VISIBLE
                        it.findViewById<ImageView>(R.id.background_add2Garbage).visibility = View.VISIBLE
                        it.findViewById<ImageView>(R.id.add2Garbage).setOnClickListener { garbage ->
                            viewModel.delete(header.foodie)
                            garbage.findViewById<ImageView>(R.id.add2Garbage).visibility = View.INVISIBLE
                            it.findViewById<ImageView>(R.id.background_add2Garbage).visibility = View.INVISIBLE
                        }
                        holder.itemView.isClickable
                    }
                    holder.itemView.setOnClickListener {
                        onClickListener.onClick(header.foodie)
                    }

            }
            is ShapeViewHolder -> {
                val shape = getItem(position) as DataItem.ShapeItem
                holder.itemView.setOnClickListener {
                    findNavController(holder.itemView).navigate(NavigationDirections
                        .navigateToShapeRecordFragment(viewModel.fireShape.value!!))
                }
                holder.bind( shape.viewModel)
            }
            is SleepViewHolder -> {
                val sleep = getItem(position) as DataItem.SleepItem
                holder.itemView.setOnClickListener {
                    findNavController(holder.itemView).navigate(NavigationDirections
                        .navigateToSleepFragment(viewModel.fireSleep.value!!))
                }
                holder.bind( sleep.viewModel)
            }
            is PlaceholderViewHolder ->{
                val header = getItem(position) as DataItem.PlaceHolder
                holder.bind(header.viewModel)
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
            is DataItem.PlaceHolder -> PLACEHOLDER
            else -> throw IllegalArgumentException()
        }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        when (holder) {
            is RecordViewHolder -> holder.markAttach()
            is SumViewHolder -> holder.markAttach()
            is ShapeViewHolder -> holder.markAttach()
            is SleepViewHolder -> holder.markAttach()
            is PlaceholderViewHolder -> holder.markAttach()
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        when (holder) {
            is RecordViewHolder -> holder.markDetach()
            is SumViewHolder -> holder.markDetach()
            is ShapeViewHolder -> holder.markDetach()
            is SleepViewHolder -> holder.markDetach()
            is PlaceholderViewHolder -> holder.markDetach()
        }
    }
}

sealed class DataItem {
    data class FoodieList(
        val foodie: Foodie, val viewModel: DiaryViewModel
    ): DataItem(){
        override val id = foodie.timestamp.toString()
    }
    data class ShapeItem(
        val viewModel: DiaryViewModel
    ) : DataItem(){
        override val id = (Long.MIN_VALUE).toString()
    }
    data class SleepItem(
        val viewModel: DiaryViewModel
    ) : DataItem(){
        override val id = (Long.MIN_VALUE+1).toString()

    }
    data class Header (
        val viewModel: DiaryViewModel
    ): DataItem() {
        override val id = (Long.MIN_VALUE+2).toString()
    }
    data class PlaceHolder(
        val viewModel: DiaryViewModel
    ) : DataItem(){
        override val id = (Long.MIN_VALUE+3).toString()
    }
    abstract val id: String
}
