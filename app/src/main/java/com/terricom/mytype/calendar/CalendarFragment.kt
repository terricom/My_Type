package com.terricom.mytype.calendar

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
import com.terricom.mytype.Logger
import com.terricom.mytype.R
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.diary.DiaryViewModel
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CalendarFragment : ConstraintLayout, CalendarAdapter.ListenerCellSelect {


    companion object {
        const val MAX_DAY_COUNT = 35

        const val NUM_DAY_OF_WEEK = 7
    }

    public val DEFAULT_DATE_FORMAT = "yyyy-MM"

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
            Logger.i("calendarAdapter in CalendarFragment list Dates = $mCellList")
            this.listDates = mCellList
            this.context = getContext()
            this.showingDateCalendar = currentDateCalendar
            this.listener = this@CalendarFragment
            this.recordedDates = recordedDate.value!!
        }

        gridRecycler.adapter = calendarAdapter
        setHeader(currentDateCalendar)

    }

    val viewModel = DiaryViewModel()
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    var selectedDayOut: Date = Date()
    val thisMonth: List<String> ?= null


    val userUid = UserManager.uid

    val db = FirebaseFirestore.getInstance()
    val users = db.collection("Users")

    val _recordedDate = MutableLiveData<List<String>>()
    val recordedDate : LiveData<List<String>>
        get() = _recordedDate

    fun setRecordedDate(recordedDate: List<String>){
        _recordedDate.value = recordedDate
    }

    val _fireFoodieM = MutableLiveData<List<Foodie>>()
    val fireFoodieM : LiveData<List<Foodie>>
        get() = _fireFoodieM

    fun fireFoodieBackM (foo: List<Foodie>){
        _fireFoodieM.value = foo
    }
    val _date = MutableLiveData<Date>()
    val date : LiveData<Date>
        get() = _date

    fun filterdate(dato: Date){
        Logger.i("CalendarViewHolder filterdate = ${dato}")
        _date.value = dato
    }


    fun getThisMonth() {
        if (userUid!!.isNotEmpty()){
            Logger.i("whereGreaterThanOrEqualTo ${currentDateCalendar.get(Calendar.YEAR)}" +
                    "-${currentDateCalendar.get(Calendar.MONTH)+1}-" +
                    "01 00:00:00.000000000" +
                    "whereLessThanOrEqualTo ${currentDateCalendar.get(Calendar.YEAR)}" +
                    "-${currentDateCalendar.get(Calendar.MONTH)+1}-"+
                    "${getLastMonthLastDate()} 23:59:59.000000000")
            val foodieDiary = users
                .document(userUid).collection("Foodie")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereLessThanOrEqualTo("timestamp", Timestamp.valueOf(
                    "${currentDateCalendar.get(Calendar.YEAR)}-${currentDateCalendar.get(Calendar.MONTH)+1}-" +
                            "${getLastMonthLastDate()} 23:59:59.000000000"
                ))
                .whereGreaterThanOrEqualTo("timestamp", Timestamp.valueOf(
                    "${currentDateCalendar.get(Calendar.YEAR)}-${currentDateCalendar.get(Calendar.MONTH)+1}-" +
                            "01 00:00:00.000000000"
                ))


            foodieDiary
                .get()
                .addOnSuccessListener {
                    val items = mutableListOf<Foodie>()
                    val dates = mutableListOf<String>()
                    items.clear()
                    dates.clear()
                    for (document in it) {
                        val convertDate = java.sql.Date(document.toObject(Foodie::class.java).timestamp!!.time)
                        if (date.value != null && "${sdf.format(convertDate).split("-")[0]}-" +
                            "${sdf.format(convertDate).split("-")[1]}" ==
                            "${sdf.format(date.value)!!.split("-")[0]}-" +
                            "${sdf.format(date.value)!!.split("-")[1]}"){
                            items.add(document.toObject(Foodie::class.java))
                            items[items.size-1].docId = document.id
                            document.toObject(Foodie::class.java).timestamp?.let {
                                dates.add(sdf.format(java.sql.Date(it.time)))
                            }
                        }
                    }
                    if (items.size != 0) {
                    }
                    fireFoodieBackM(items)
                    setRecordedDate(dates)
                }
        }
    }

    fun getLastMonthLastDate(): Int {
//        val calendar = Calendar.getInstance()
        currentDateCalendar.add(Calendar.MONTH, 0)

        val max = currentDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        currentDateCalendar.set(Calendar.DAY_OF_MONTH, max)

        return currentDateCalendar.get(Calendar.DAY_OF_MONTH)
    }


    override fun onDateSelect(selectDate: Date) {
        val tempAdapter = gridRecycler.adapter as CalendarAdapter
        tempAdapter.selectedDate = selectDate
        tempAdapter.notifyDataSetChanged()

        val tempCalendar = Calendar.getInstance()
        tempCalendar.time = selectDate
        Logger.i("CalendarFragment sdf.format(selectDate) =${sdf.format(selectDate)} selectDate = $selectDate ")
        filterdate(selectDate)
//        getThisMonth()
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
        txtDate.text = sdf.format(calendarDate.time)
    }


    interface EventBetweenCalendarAndFragment {
        fun onCalendarPreviousPressed()
        fun onCalendarNextPressed()

    }

}