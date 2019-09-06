package com.terricom.mytype.calendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.Logger
import com.terricom.mytype.R
import com.terricom.mytype.diary.DiaryViewModel
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : ConstraintLayout, CalendarAdapter.ListenerCellSelect {

    companion object {
        const val MAX_DAY_COUNT = 35

        const val NUM_DAY_OF_WEEK = 7
    }

    public val DEFAULT_DATE_FORMAT = "yyyy.MM"

    private var dateFormat = DEFAULT_DATE_FORMAT

    private lateinit var buttonBack: ImageView
    private lateinit var buttonNext: ImageView
    private lateinit var txtDate: TextView
    private lateinit var gridRecycler: RecyclerView
    private lateinit var currentDateCalendar: Calendar
    private var eventHandler: EventBetweenCalendarAndFragment? = null
    private var todayMonth: Int = -1
    private var todayYear: Int = -1


    constructor(context: Context?) : super(context) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }


    private fun initView(context: Context?, attrs: AttributeSet? = null){
        currentDateCalendar = Calendar.getInstance().apply {
            todayMonth = this.get(Calendar.MONTH)
            todayYear = this.get(Calendar.YEAR)

        }

        context?.let {
            val inflater = it.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.fragment_calendar, this, true)
            buttonBack = findViewById(R.id.toBack)
            buttonNext = findViewById(R.id.toNext)
            txtDate = findViewById(R.id.itemDate)
            gridRecycler = findViewById(R.id.gridCalendar)

            buttonNext.setOnClickListener {
                currentDateCalendar.add(Calendar.MONTH, 1)

                eventHandler?.onCalendarNextPressed()

                checkStateNextButton()
            }

            buttonBack.setOnClickListener{
                currentDateCalendar.add(Calendar.MONTH, -1)
                eventHandler?.onCalendarPreviousPressed()
                checkStateNextButton()
            }

            ViewCompat.setNestedScrollingEnabled(gridRecycler, false)

            gridRecycler.layoutManager = GridLayoutManager(context, NUM_DAY_OF_WEEK)
            gridRecycler.addItemDecoration(SpaceItemDecoration(
                resources.getDimension(R.dimen._1sdp).toInt(),
                true
            ))

        }
    }


    fun updateCalendar(){
        val mCellList = ArrayList<Date>()
        val mCalendar = currentDateCalendar.clone() as Calendar

        mCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val monthBeginningCell = mCalendar.get(Calendar.DAY_OF_WEEK) - 1


        mCalendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        while (mCellList.size < MAX_DAY_COUNT) {
            mCellList.add(mCalendar.time)
            mCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val calendarAdapter = CalendarAdapter().apply {
            this.listDates = mCellList
            this.context = getContext()
            this.showingDateCalendar = currentDateCalendar
            this.listener = this@CalendarFragment
        }

        gridRecycler.adapter = calendarAdapter
        setHeader(currentDateCalendar)

    }

    val viewModel = DiaryViewModel()
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    var selectedDayOut: String ?= ""


    override fun onDateSelect(selectDate: Date) {
        val tempAdapter = gridRecycler.adapter as CalendarAdapter
        tempAdapter.selectedDate = selectDate
        tempAdapter.notifyDataSetChanged()

        val tempCalendar = Calendar.getInstance()
        tempCalendar.time = selectDate
        Logger.i("CalendarFragment sdf.format(selectDate) =${sdf.format(selectDate)} ")
        selectedDayOut = sdf.format(selectDate)
        viewModel.filterdate(sdf.format(selectDate))
        setHeader(tempCalendar)
    }

    private fun checkStateNextButton() {
        val currentMonth = currentDateCalendar.get(Calendar.MONTH)
        val currentYear = currentDateCalendar.get(Calendar.YEAR)
        if (currentMonth == todayMonth && currentYear == todayYear) {
            buttonNext.visibility = View.INVISIBLE
        } else {
            buttonNext.visibility = View.VISIBLE
        }
    }




    fun setEventHandler(eventHandler: EventBetweenCalendarAndFragment) {
        this.eventHandler = eventHandler
    }

    private fun setHeader(calendarDate: Calendar) {
        val sdf = SimpleDateFormat(dateFormat, Locale.US)
        txtDate.text = sdf.format(calendarDate.time)
    }


    interface EventBetweenCalendarAndFragment {
        fun onCalendarPreviousPressed()
        fun onCalendarNextPressed()

    }

}