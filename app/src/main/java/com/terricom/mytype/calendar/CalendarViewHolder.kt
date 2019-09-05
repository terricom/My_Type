package com.terricom.mytype.calendar

import android.graphics.Typeface
import android.graphics.Typeface.BOLD
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.Logger
import com.terricom.mytype.R
import com.terricom.mytype.diary.DiaryViewModel
import java.text.SimpleDateFormat
import java.util.*

class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view), LifecycleOwner {

    private val monthOfDate = view.findViewById<TextView>(R.id.itemDate)
    private val recordDate = view.findViewById<ImageView>(R.id.date_record)
    private val puzzleDate = view.findViewById<ImageView>(R.id.date_puzzle)
    private val cellDateLayout = view.findViewById<ConstraintLayout>(R.id.cellDateLayout)
    private val context = view.context

    fun myBindView(currentDateInput : Date,
                   showingDate : Calendar,
                   dateSelected : Date?,
                   listener: CalendarAdapter.ListenerCellSelect? = null,
                   viewModel: DiaryViewModel
    ){
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
        val sdf = SimpleDateFormat("d")
        viewModel.fireFoodieM.observe(this, androidx.lifecycle.Observer {
            if (it != null){
                Logger.i("CalendarViewHolder date not null")
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
            monthOfDate.setTextColor(ResourcesCompat.getColor(context.resources, R.color.colorAllTransparent, null))
            cellDateLayout.setBackgroundColor(
                ResourcesCompat.getColor(
                    context.resources,
                    R.color.colorAllTransparent,
                    null
                )
            )
        } else if(selectedDay == currentDay ){
            if (viewModel.fireFoodieM.value != null){
                for (day in viewModel.fireFoodieM.value!!){
                    if (sdf.format(java.sql.Date(day.timestamp!!.time)) == monthOfDate.text && currentMonth == viewMonth){
                        Logger.i("sdf.format(java.sql.Date(day.timestamp!!.time)) = ${sdf.format(java.sql.Date(day.timestamp!!.time))}")
                        recordDate.visibility = View.VISIBLE
                    }
                }}

            puzzleDate.visibility = View.VISIBLE
            monthOfDate.setTypeface(null, BOLD)
            cellDateLayout.background =
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.input_column,
                    null
                )

        } else {
            if (viewModel.fireFoodieM.value != null){
                for (day in viewModel.fireFoodieM.value!!){
                    if (sdf.format(java.sql.Date(day.timestamp!!.time)) == monthOfDate.text && currentMonth == viewMonth){
                        Logger.i("sdf.format(java.sql.Date(day.timestamp!!.time)) = ${sdf.format(java.sql.Date(day.timestamp!!.time))}")
                        recordDate.visibility = View.VISIBLE
                    }
                }}
            monthOfDate.setTextColor(ResourcesCompat.getColor(context.resources, R.color.colorMyType, null))
            cellDateLayout.background = ResourcesCompat.getDrawable(context.resources, R.drawable.calendar_date,null)


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
        monthOfDate.setTextColor(ResourcesCompat.getColor(context.resources, R.color.colorWhite, null))
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