package com.terricom.mytype.shaperecord

import android.graphics.Typeface
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.App
import com.terricom.mytype.R
import java.util.*

class ShapeCalendarViewHolder( private var binding: com.terricom.mytype.databinding.ItemCalendarSquareBinding
) : RecyclerView.ViewHolder(binding.root)
, LifecycleOwner {

    private val monthOfDate = binding.itemDate
    private val shape = binding.shape
    private val cellDateLayout = binding.cellDateLayout
    var recorded = false
    var selected = false

    fun myBindView(currentDateInput : Date,
                   showingDate : Calendar,
                   dateSelected : Date?,
                   listener: ShapeCalendarAdapter.ListenerCellSelect? = null,
                   viewModel: ShapeRecordViewModel
    ){
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()

        resetViewDefault()

        val viewMonth = showingDate.get(Calendar.MONTH) + 1
        val viewYear = showingDate.get(Calendar.YEAR)

        val currentDateTime = Calendar.getInstance()
        currentDateTime.time = currentDateInput

        val currentDay = currentDateTime.get(Calendar.DATE)
        val currentMonth = currentDateTime.get(Calendar.MONTH) + 1
        val currentYear = currentDateTime.get(Calendar.YEAR)

        var selectedDay = -1

        if(dateSelected != null){
            val selectedDateTime = getCalendarFromTimestamp(dateSelected.time)
            selectedDay = selectedDateTime.get(Calendar.DATE)
        }

        if(currentMonth != viewMonth || currentYear != viewYear){
            monthOfDate.setTextColor(ResourcesCompat.getColor(App.applicationContext().resources, R.color.colorAllTransparent, null))
            shape.visibility = View.INVISIBLE
            cellDateLayout.setBackgroundColor(
                ResourcesCompat.getColor(
                    App.applicationContext().resources,
                    R.color.colorAllTransparent,
                    null
                )
            )
        } else if(selectedDay == currentDay ){
            if (recorded){
                shape.visibility = View.VISIBLE
            }
            monthOfDate.setTypeface(null, Typeface.BOLD)
            cellDateLayout.background =
                ResourcesCompat.getDrawable(
                    App.applicationContext().resources,
                    R.drawable.input_column,
                    null
                )

        } else {
            if (recorded){
                shape.visibility = View.VISIBLE
            }
            monthOfDate.setTextColor(ResourcesCompat.getColor(App.applicationContext().resources, R.color.colorMyType, null))
            cellDateLayout.background = ResourcesCompat.getDrawable(App.applicationContext().resources, R.drawable.calendar_date,null)


            itemView.setOnClickListener{
                listener?.let { eventHandler ->
                    eventHandler.onDateSelect(currentDateInput)
                }
            }
        }

        monthOfDate.text = currentDay.toString()
    }



    private fun resetViewDefault() {
        itemView.setOnClickListener(null)
        monthOfDate.setTypeface(null, Typeface.NORMAL)
        monthOfDate.setTextColor(ResourcesCompat.getColor(App.applicationContext().resources, R.color.colorWhite, null))
    }


    fun getCalendarFromTimestamp(timestamp: Long): Calendar {
        val date = Date(timestamp)
        return Calendar.getInstance().apply { time = date }
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