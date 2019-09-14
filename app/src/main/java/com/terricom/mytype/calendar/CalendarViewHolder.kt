package com.terricom.mytype.calendar

import android.graphics.Typeface
import android.graphics.Typeface.BOLD
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.App
import com.terricom.mytype.Logger
import com.terricom.mytype.R
import com.terricom.mytype.databinding.ItemCalendarDayBinding
import com.terricom.mytype.diary.DiaryViewModel
import java.text.SimpleDateFormat
import java.util.*

class CalendarViewHolder(private var binding: ItemCalendarDayBinding) :
    RecyclerView.ViewHolder(binding.root), LifecycleOwner {

    private val monthOfDate = binding.itemDate
    private val recordDate = binding.dateRecord
    private val puzzleDate = binding.datePuzzle
    private val cellDateLayout = binding.cellDateLayout
//    private val context = view.context


    fun myBindView(currentDateInput : Date,
                   showingDate : Calendar,
                   dateSelected : Date?,
                   listener: CalendarAdapter.ListenerCellSelect? = null,
                   viewModel: DiaryViewModel
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
        var selectedMonth = -1
        var selectedYear = -1

        val sdf = SimpleDateFormat("d")


        viewModel.fireFoodieM.observe(this, androidx.lifecycle.Observer {
            if (it.size >0 ){
                for (day in it){
                    if (sdf.format(java.sql.Date(day.timestamp!!.time)) == monthOfDate.text && currentMonth == viewMonth){
                        Logger.i("sdf.format(java.sql.Date(day.timestamp!!.time)) = ${sdf.format(java.sql.Date(day.timestamp!!.time))}")
                        recordDate.visibility = View.VISIBLE
                    }
                }}

        })


        if(currentMonth != viewMonth || currentYear != viewYear){
            recordDate.visibility = View.INVISIBLE
            puzzleDate.visibility = View.INVISIBLE
            monthOfDate.setTextColor(ResourcesCompat.getColor(App.applicationContext().resources, R.color.colorAllTransparent, null))
            cellDateLayout.setBackgroundColor(
                ResourcesCompat.getColor(
                    App.applicationContext().resources,
                    R.color.colorAllTransparent,
                    null
                )
            )
        } else if(selectedDay == currentDay ){

            puzzleDate.visibility = View.VISIBLE
            monthOfDate.setTypeface(null, BOLD)
            cellDateLayout.background =
                ResourcesCompat.getDrawable(
                    App.applicationContext().resources,
                    R.drawable.input_column,
                    null
                )
//            if (viewModel.fireFoodieM.value != null){
//                for (day in viewModel.fireFoodieM.value!!){
//                    if (sdf.format(java.sql.Date(day.timestamp!!.time)) == monthOfDate.text && currentMonth == viewMonth){
//                        recordDate.visibility = View.VISIBLE
//                    }
//                }}

        } else {
//            if (viewModel.fireFoodieM.value != null){
//                for (day in viewModel.fireFoodieM.value!!){
//                    if (sdf.format(java.sql.Date(day.timestamp!!.time)) == monthOfDate.text && currentMonth == viewMonth){
//                        Logger.i("sdf.format(java.sql.Date(day.timestamp!!.time)) = ${sdf.format(java.sql.Date(day.timestamp!!.time))}")
//                        recordDate.visibility = View.VISIBLE
//                    }
//                }}
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