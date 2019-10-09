package com.terricom.mytype.shaperecord

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.databinding.ItemCalendarSquareBinding
import com.terricom.mytype.tools.FORMAT_YYYY_MM_DD
import com.terricom.mytype.tools.toDateFormat
import java.util.*

class ShapeCalendarAdapter(
): RecyclerView.Adapter<ShapeCalendarViewHolder>() {

    lateinit var listDates: ArrayList<Date>
    lateinit var context: Context
    lateinit var showingDateCalendar: Calendar
    var selectedDate: Date? = null
    var listener: ListenerCellSelect? = null
    lateinit var recordedDates: List<String>
    var selectedDateFromArguments: Date? = null


    override fun onCreateViewHolder(parent: ViewGroup, size: Int): ShapeCalendarViewHolder {
        context = parent.context
        return ShapeCalendarViewHolder(ItemCalendarSquareBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return listDates.size
    }

    override fun onBindViewHolder(viewHolder: ShapeCalendarViewHolder, position: Int) {
        if (recordedDates.contains(listDates[position].toDateFormat(FORMAT_YYYY_MM_DD))){
            viewHolder.recorded = true
        }
        if (listDates[position].toDateFormat(FORMAT_YYYY_MM_DD) == selectedDateFromArguments.toDateFormat(FORMAT_YYYY_MM_DD)){
            viewHolder.selected = true
        }
        viewHolder.myBindView(
            listDates[position],
            showingDateCalendar,
            selectedDate,
            listener,
            viewModel = ShapeRecordViewModel()
        )
    }


    interface ListenerCellSelect {
        fun onDateSelect(selectDate: Date)

    }

    override fun onViewAttachedToWindow(holder: ShapeCalendarViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: ShapeCalendarViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }


}