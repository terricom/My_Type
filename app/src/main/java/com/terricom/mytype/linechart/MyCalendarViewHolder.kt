package com.terricom.mytype.linechart

import android.graphics.Typeface
import android.graphics.Typeface.BOLD
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.R
import java.util.*

class MyCalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val monthOfDate = view.findViewById<TextView>(R.id.itemDate)
    private val recordDate = view.findViewById<ImageView>(R.id.date_record)
    private val puzzleDate = view.findViewById<ImageView>(R.id.date_puzzle)
    private val cellDateLayout = view.findViewById<ConstraintLayout>(R.id.cellDateLayout)
    private val context = view.context

    fun myBindView(currentDateInput : Date,
                   showingDate : Calendar,
                   dateSelected : Date?,
                   listener: MyCalendarAdapter.ListenerCellSelect? = null
    ){
        resetViewDefault()

        val showingMonth = showingDate.get(Calendar.MONTH) + 1
        val showingYear = showingDate.get(Calendar.YEAR)

        val currentDateTime = Calendar.getInstance()
        currentDateTime.time = currentDateInput

        val currentDay = currentDateTime.get(Calendar.DATE)
        val currentMonth = currentDateTime.get(Calendar.MONTH) + 1
        val currentYear = currentDateTime.get(Calendar.YEAR)

        var selectedDay = -1
        var selectedMonth = -1
        if(dateSelected != null){
            val selectedDateTime = getCalendarFromTimestamp(dateSelected.time)
            selectedDay = selectedDateTime.get(Calendar.DATE)
            selectedMonth = selectedDateTime.get(Calendar.MONTH)
        }

        if(currentMonth != showingMonth || currentYear != showingYear){
            monthOfDate.setTextColor(ResourcesCompat.getColor(context.resources, R.color.colorAllTransparent, null))
            cellDateLayout.setBackgroundColor(
                ResourcesCompat.getColor(
                    context.resources,
                    R.color.colorAllTransparent,
                    null
                )
            )
        } else if(selectedDay == currentDay ){
            monthOfDate.setTypeface(null, BOLD)
            cellDateLayout.background =
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.input_column,
                    null
                )

        } else {
            monthOfDate.setTextColor(ResourcesCompat.getColor(context.resources, R.color.colorMyType, null))
            cellDateLayout.background = ResourcesCompat.getDrawable(context.resources, R.drawable.calendar_date,null)

//            cellDateLayout.setBackgroundColor(
//                ResourcesCompat.getColor(
//                    context.resources,
//                    R.color.calendar_background_item_enable,
//                    null
//                )
//            )
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


}