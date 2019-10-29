package com.terricom.mytype.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.data.FirebaseKey
import com.terricom.mytype.data.FirebaseKey.Companion.COLLECTION_FOODIE
import com.terricom.mytype.data.FirebaseKey.Companion.COLLECTION_USERS
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.tools.FORMAT_YYYY_MM_DD
import com.terricom.mytype.tools.toDateFormat
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CalendarComponentLayout : ConstraintLayout, CalendarAdapter.ListenerCellSelect {


    companion object {
        const val MAX_DAY_COUNT = 35
        const val NUM_DAY_OF_WEEK = 7
    }

    private var dateFormat = App.applicationContext().getString(R.string.simpledateformat_yyyy_MM)

    private lateinit var buttonBack: ImageView
    private lateinit var buttonNext: ImageView
    private lateinit var buttonBackLarge: ImageView
    private lateinit var buttonNextLarge: ImageView
    private lateinit var dayOfMonth: TextView
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
            buttonBackLarge = findViewById(R.id.buttonBack)
            buttonNextLarge = findViewById(R.id.buttonNext)
            dayOfMonth = findViewById(R.id.itemDate)
            gridRecycler = findViewById(R.id.gridCalendar)

            buttonNext.setOnClickListener {
                currentDateCalendar.add(Calendar.MONTH, 1)
                eventHandler?.onCalendarNextPressed()
                checkStateNextButton()
            }
            buttonNextLarge.setOnClickListener {
                currentDateCalendar.add(Calendar.MONTH, 1)
                eventHandler?.onCalendarNextPressed()
                checkStateNextButton()
            }

            buttonBack.setOnClickListener{
                currentDateCalendar.add(Calendar.MONTH, -1)
                eventHandler?.onCalendarPreviousPressed()
                checkStateNextButton()
            }
            buttonBackLarge.setOnClickListener {
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
            getThisMonth()
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
            this.listener = this@CalendarComponentLayout
            this.recordedDates = recordedDate.value ?: listOf(Date().toDateFormat(FORMAT_YYYY_MM_DD))
        }
        gridRecycler.adapter = calendarAdapter
        setHeader(currentDateCalendar)

    }

//    val viewModel = DiaryViewModel(firebaseRepository = FirebaseRepository)
    @SuppressLint("SimpleDateFormat")
    var selectedDayOut = Date()
    val thisMonth: List<String> ?= null

    val userUid = UserManager.uid

    private val _recordedDate = MutableLiveData<List<String>>()
    val recordedDate : LiveData<List<String>>
        get() = _recordedDate

    private fun setRecordedDate(recordedDate: List<String>){
        _recordedDate.value = recordedDate
    }

    private val _date = MutableLiveData<Date>()
    val date : LiveData<Date>
        get() = _date

    fun setCurrentDate(date: Date){
        _date.value = date
    }


    @SuppressLint("StringFormatMatches")
    fun getThisMonth() {
        if (UserManager.isLogin()){

            UserManager.uid?.let {it ->

                FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                    .document(it).collection(COLLECTION_FOODIE)
                    .orderBy(FirebaseKey.TIMESTAMP, Query.Direction.DESCENDING)
                    .whereLessThanOrEqualTo(
                        FirebaseKey.TIMESTAMP, Timestamp.valueOf(
                            App.applicationContext().getString(R.string.timestamp_dayend,
                                App.applicationContext().getString(R.string.year_month_date,
                                "${currentDateCalendar.get(Calendar.YEAR)}",
                                        "${currentDateCalendar.get(Calendar.MONTH)+1}",
                                        "${getThisMonthLastDate()}"
                                )
                            )
                        )
                    )
                    .whereGreaterThanOrEqualTo(
                        FirebaseKey.TIMESTAMP, Timestamp.valueOf(
                            App.applicationContext().getString(R.string.timestamp_daybegin,
                                App.applicationContext().getString(R.string.year_month_date,
                                    "${currentDateCalendar.get(Calendar.YEAR)}",
                                    "${currentDateCalendar.get(Calendar.MONTH)+1}",
                                    "01"
                                    )
                                )
                        )
                    )
                    .get()
                    .addOnSuccessListener { it ->
                        val items = mutableListOf<Foodie>()
                        val dates = mutableListOf<String>()
                        items.clear()
                        dates.clear()

                        for (document in it) {

                            items.add(document.toObject(Foodie::class.java))
                            items[items.lastIndex].docId = document.id
                            document.toObject(Foodie::class.java).timestamp?.let {
                                dates.add(it.toDateFormat(FORMAT_YYYY_MM_DD))
                            }
                        }

                        when (items.size){
                            0 -> setRecordedDate(listOf(Date().toDateFormat(FORMAT_YYYY_MM_DD)))
                            else -> setRecordedDate(dates)
                        }
                    }
            }

        }
    }

    private fun getThisMonthLastDate(): Int {

        currentDateCalendar.add(Calendar.MONTH, 0)
        currentDateCalendar.set(Calendar.DAY_OF_MONTH,
            currentDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))

        return currentDateCalendar.get(Calendar.DAY_OF_MONTH)
    }


    override fun onDateSelect(selectDate: Date) {
        val tempAdapter = gridRecycler.adapter as CalendarAdapter
        tempAdapter.selectedDate = selectDate
        tempAdapter.notifyDataSetChanged()

        val tempCalendar = Calendar.getInstance()
        tempCalendar.time = selectDate
        setCurrentDate(selectDate)
        tempAdapter.recordedDates = recordedDate.value!!
        selectedDayOut = selectDate
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
        dayOfMonth.text = sdf.format(calendarDate.time)
    }


    interface EventBetweenCalendarAndFragment {
        fun onCalendarPreviousPressed()
        fun onCalendarNextPressed()

    }


}