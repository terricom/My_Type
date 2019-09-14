package com.terricom.mytype.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.databinding.ItemCalendarDayBinding
import com.terricom.mytype.diary.DiaryViewModel
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter : RecyclerView.Adapter<CalendarViewHolder>() {

    lateinit var listDates: ArrayList<Date>
    lateinit var context: Context
    lateinit var showingDateCalendar: Calendar
    var selectedDate: Date ?= null
    var listener: ListenerCellSelect? = null
    lateinit var recordedDates: List<String>
    val sdf = SimpleDateFormat("yyyy-MM-dd")

    override fun onCreateViewHolder(parent: ViewGroup, size: Int): CalendarViewHolder {
        context = parent.context
        return CalendarViewHolder(ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return listDates.size
    }

    override fun onBindViewHolder(viewHolder: CalendarViewHolder, position: Int) {

        if (recordedDates.contains(sdf.format(listDates[position]))){
            viewHolder.recorded = true
        }
        viewHolder.myBindView(
            listDates[position],
            showingDateCalendar,
            selectedDate,
            listener,
            viewModel = DiaryViewModel()
        )
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