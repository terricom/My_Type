package com.terricom.mytype.shaperecord

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.R
import com.terricom.mytype.databinding.ItemCalendarSquareBinding
import java.util.*

class ShapeCalendarAdapter(
    val viewModel: ShapeRecordViewModel
): RecyclerView.Adapter<ShapeCalendarViewHolder>() {

    lateinit var listDates: ArrayList<Date>
    lateinit var context: Context
    lateinit var showingDateCalendar: Calendar
    var selectedDate: Date? = null
    var listener: ListenerCellSelect? = null


    override fun onCreateViewHolder(parent: ViewGroup, size: Int): ShapeCalendarViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_calendar_square, parent, false)

        return ShapeCalendarViewHolder(view, viewModel,ItemCalendarSquareBinding.inflate(LayoutInflater.from(parent.context), parent, false))
//        return ShapeCalendarViewHolder(view
//            , viewModel
//
//        )
    }

    override fun getItemCount(): Int {
        return listDates.size
    }

    override fun onBindViewHolder(viewHolder: ShapeCalendarViewHolder, position: Int) {
        viewHolder.myBindView(
            listDates[position],
            showingDateCalendar,
            selectedDate,
            listener
//            , viewModel
        )
    }


    interface ListenerCellSelect {
        fun onDateSelect(selectDate: Date)

    }

}