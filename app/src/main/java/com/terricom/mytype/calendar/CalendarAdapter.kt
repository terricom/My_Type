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
    var selectedDate: Date ?= null
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
            listener,
            viewModel = DiaryViewModel()
        )
//        if(selectedDate != null){
//            val selectedDateTime = viewHolder.getCalendarFromTimestamp(selectedDate!!.time)
//            val sdf = SimpleDateFormat("yyyy-MM-dd")
//            Logger.i("itemView.setOnClickListener = ${sdf.format(Date(selectedDateTime.time.time))}")
//            DiaryViewModel().filterdate("${sdf.format(Date(selectedDateTime.time.time))}")
//        }
    }


    interface ListenerCellSelect {
        fun onDateSelect(selectDate: Date)

    }

    override fun onViewAttachedToWindow(holder: CalendarViewHolder) {
        super.onViewAttachedToWindow(holder)
         holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: CalendarViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }

}