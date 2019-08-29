package com.terricom.mytype.linechart

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.R
import java.util.*

class MyCalendarAdapter : RecyclerView.Adapter<MyCalendarViewHolder>() {

    lateinit var listDates: ArrayList<Date>
    lateinit var context: Context
    lateinit var showingDateCalendar: Calendar
    var selectedDate: Date? = null
    var listener: ListenerCellSelect? = null


    override fun onCreateViewHolder(parent: ViewGroup, size: Int): MyCalendarViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.view_my_calendar_item, parent, false)
        return MyCalendarViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listDates.size
    }

    override fun onBindViewHolder(viewHolder: MyCalendarViewHolder, position: Int) {
        viewHolder.myBindView(
            listDates[position],
            showingDateCalendar,
            selectedDate,
            listener
        )
    }


    interface ListenerCellSelect {
        fun onDateSelect(selectDate: Date)

    }

}