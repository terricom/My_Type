package com.terricom.mytype.shaperecord

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.Logger
import com.terricom.mytype.databinding.ItemCalendarSquareBinding
import java.text.SimpleDateFormat
import java.util.*

class ShapeCalendarAdapter(
): RecyclerView.Adapter<ShapeCalendarViewHolder>() {

    lateinit var listDates: ArrayList<Date>
    lateinit var context: Context
    lateinit var showingDateCalendar: Calendar
    var selectedDate: Date? = null
    var listener: ListenerCellSelect? = null
    lateinit var recordedDates: List<String>
    val sdf = SimpleDateFormat("yyyy-MM-dd")

    override fun onCreateViewHolder(parent: ViewGroup, size: Int): ShapeCalendarViewHolder {
        context = parent.context
        return ShapeCalendarViewHolder(ItemCalendarSquareBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return listDates.size
    }

    override fun onBindViewHolder(viewHolder: ShapeCalendarViewHolder, position: Int) {
        Logger.i("recordedDates =$recordedDates")
        if (recordedDates.contains(sdf.format(listDates[position]))){
            viewHolder.recorded = true
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