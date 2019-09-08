package com.terricom.mytype.linechart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.App
import com.terricom.mytype.Logger
import com.terricom.mytype.R
import com.terricom.mytype.databinding.ItemLinechartViewPageBinding
import java.util.*

class LinechartAdapter(val viewModel: LinechartViewModel) : RecyclerView.Adapter<LinechartAdapter.PagerVH>() {

    //array of colors to change the background color of screen
    private val colors = intArrayOf(
        android.R.color.black,
        android.R.color.holo_red_light,
        android.R.color.holo_blue_dark,
        android.R.color.holo_purple
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
        PagerVH(
            ItemLinechartViewPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    //get the size of color array
    override fun getItemCount(): Int = Int.MAX_VALUE

    //binding the screen with view
    override fun onBindViewHolder(holder: PagerVH, position: Int) = holder.itemView.run {
        Logger.i("Linechart Adapter position =$position")
        if(position == 20){
            viewModel.newFireBack()
            viewModel.setDate(Date())
            holder.bind(viewModel)
        }
        if(position != 20) {
            viewModel.newFireBack()
            viewModel.setDate(Date(viewModel.recordDate.value!!.time.plus(604800000L*(position-20))))
            holder.bind(viewModel)
        }

    }



class PagerVH(private var binding: ItemLinechartViewPageBinding) : RecyclerView.ViewHolder(binding.root), LifecycleOwner{

    val list = ArrayList<ChartEntity>()

    fun bind(viewModel: LinechartViewModel){
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.executePendingBindings()

        viewModel.fireBackEnd.observe(this, androidx.lifecycle.Observer {
            if (it == true){
                viewModel.waterList.observe(this, androidx.lifecycle.Observer {
                    val waterChartEntity = ChartEntity(App.applicationContext().getColor(R.color.colorWater), it)
                    if (it != null){
                        viewModel.waterClicked.observe(this, androidx.lifecycle.Observer {
                            if (it == true){
                                list.add(waterChartEntity)
                            }
                        })

                    }
                })

                viewModel.oilList.observe(this, androidx.lifecycle.Observer {
                    if (it != null){
                        val oilChartEntity = ChartEntity(App.applicationContext().getColor(R.color.colorOil), it)
                        list.add(oilChartEntity)
                    }
                })

                viewModel.vegetableList.observe(this, androidx.lifecycle.Observer {
                    if (it != null){
                        val vegetableChartEntity = ChartEntity(App.applicationContext().getColor(R.color.colorVegetable), it)
                        list.add(vegetableChartEntity)

                    }
                })

                viewModel.proteinList.observe(this, androidx.lifecycle.Observer {
                    if (it != null){
                        val proteinChartEntity = ChartEntity(App.applicationContext().getColor(R.color.colorProtein), it)
                        list.add(proteinChartEntity)
                    }
                })

                viewModel.fruitList.observe(this, androidx.lifecycle.Observer {
                    if (it != null){
                        val fruitChartEntity = ChartEntity(App.applicationContext().getColor(R.color.colorFruit), it)
                        list.add(fruitChartEntity)

                    }
                })

                viewModel.carbonList.observe(this, androidx.lifecycle.Observer {
                    if (it != null){
                        val carbonChartEntity = ChartEntity(App.applicationContext().getColor(R.color.colorCarbon), it)
                        list.add(carbonChartEntity)
                    }
                })

                binding.lineChart.legendArray = viewModel.fireDate.value
                if (list.size != 0){
                    binding.lineChart.setList(list)
                }
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
    override fun onViewAttachedToWindow(holder: PagerVH) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: PagerVH) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()

    }
}