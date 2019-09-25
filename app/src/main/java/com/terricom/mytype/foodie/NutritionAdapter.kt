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
import com.terricom.mytype.tools.Logger
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
//                       ,private val onLongClickListenerNu: LongClickListenerNu
                    ,private val onClickListener: OnClickListener
)

    : ListAdapter<DataItemNu, RecyclerView.ViewHolder>(DiffCallback) {

    var addOrRemove : Boolean = true

    class OnClickListener(val clickListener: (nutrition: String) -> Unit) {
        fun onClick(nutrition: String) = clickListener(nutrition)
    }

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun submitNutritions(list : List<String>?) {
        adapterScope.launch {
            val newList = mutableListOf<com.terricom.mytype.foodie.DataItemNu>()
            if (list != null) {
                for (nutrition in list) {
                    newList.add(com.terricom.mytype.foodie.DataItemNu.NutritionList(nutrition, viewModel))
                }
            }
            withContext(Dispatchers.Main) {
                submitList(newList)
            }
        }
    }

    fun submitNutritionsWithEdit(list : List<String>?) {
        adapterScope.launch {
            val newList = mutableListOf<com.terricom.mytype.foodie.DataItemNu>()
            if (list != null && list.contains("新增營養")) {
                for (nutrition in list) {
                    if (nutrition != "新增營養"){
                        newList.add(com.terricom.mytype.foodie.DataItemNu.NutritionList(nutrition, viewModel))
                    }
                }
                newList.add(com.terricom.mytype.foodie.DataItemNu.EditNutrition(viewModel))
            } else if (list != null && !list.contains("新增營養")){
                for (nutrition in list) {
                    if (nutrition != "新增營養"){
                        newList.add(com.terricom.mytype.foodie.DataItemNu.NutritionList(nutrition, viewModel))
                    }
                }
            }
            withContext(Dispatchers.Main) {
                submitList(newList)
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
            binding.nutrition.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                    if (event.action === KeyEvent.ACTION_DOWN) {
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                                viewModel.dragToListNu(binding.nutrition.text.toString())
                                binding.nutrition.setText("")
                                v.nextFocusDownId = R.id.nutrition
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

        override fun getItemViewType(position: Int): Int =
               when (getItem(position)) {
                       is DataItemNu.NutritionList -> NUTRITIONLIST
                      is DataItemNu.EditNutrition -> EDIT_NUTRITION
                       else -> throw IllegalArgumentException()
                  }



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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //// to pass onClicklistener into adapter in CartFragment
        val nutrition = getItem(position)
        when (holder) {
            is NutritionViewHolder -> {
                holder.bind(nutrition.id, viewModel)

                if (addOrRemove) {
                    holder.itemView.setOnClickListener {
                        viewModel.dragToListNu(nutrition.id)
                    }
                } else if (!addOrRemove) {
                    holder.itemView.setOnClickListener {
                        viewModel.dragOutListNu(nutrition.id)
                    }
                }
                holder.bind(nutrition.id, viewModel)
            }

            is EditNutritionViewHolder -> {
                holder.bind(viewModel)
            }
        }
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
