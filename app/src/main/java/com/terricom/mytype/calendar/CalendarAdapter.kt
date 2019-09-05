package com.terricom.mytype.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.R
import com.terricom.mytype.diary.DiaryViewModel
import java.util.*

class CalendarAdapter : RecyclerView.Adapter<CalendarViewHolder>() {

    lateinit var listDates: ArrayList<Date>
    lateinit var context: Context
    lateinit var showingDateCalendar: Calendar
    var selectedDate: Date? = null
    var listener: ListenerCellSelect? = null


    override fun onCreateViewHolder(parent: ViewGroup, size: Int): CalendarViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listDates.size
    }

    override fun onBindViewHolder(viewHolder: CalendarViewHolder, position: Int) {
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