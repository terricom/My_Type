package com.terricom.mytype.linechart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.Logger
import com.terricom.mytype.databinding.ItemLinechartViewPageBinding
import java.util.*

class LinechartAdapter(val viewModel: LinechartViewModel) : RecyclerView.Adapter<LinechartAdapter.PagerVH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH {
        Logger.i("LinechartAdapter")
        viewModel.clearData()
        viewModel.getThisMonth()
        return PagerVH(
            ItemLinechartViewPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    private var dates: List<Date>? = null

    fun submitDates(listDates: List<Date>) {
        this.dates = listDates
        notifyDataSetChanged()
    }

    //get the size of color array
    override fun getItemCount(): Int {
        return dates?.let { Int.MAX_VALUE } ?: 0
    }

    //binding the screen with view
    override fun onBindViewHolder(holder: PagerVH, position: Int){
        dates?.let {
            holder.bind(viewModel, it[getRealPosition(position)])
        }
    }

    class PagerVH(private var binding: ItemLinechartViewPageBinding) :
    RecyclerView.ViewHolder(binding.root), LifecycleOwner{

    fun bind(viewModel: LinechartViewModel, dates: Date){
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.executePendingBindings()

        Logger.i("NEW ViewHolder")

        viewModel.listDates.observe(this, androidx.lifecycle.Observer {
            if (it != null){
                Logger.i("After check list =${viewModel.listDates}")
                binding.lineChart.legendArray = viewModel.fireDate.value
                binding.lineChart.setList(it)
            }
        })

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
    private fun getRealPosition(position: Int): Int = dates?.let {
        position % it.size
    } ?: 0

    override fun onViewAttachedToWindow(holder: PagerVH) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: PagerVH) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()

    }
}