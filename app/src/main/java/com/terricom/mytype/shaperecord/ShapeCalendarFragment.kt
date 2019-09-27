package com.terricom.mytype.shaperecord

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
import com.terricom.mytype.R
import com.terricom.mytype.data.Shape
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.tools.Logger
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class ShapeCalendarFragment: ConstraintLayout, ShapeCalendarAdapter.ListenerCellSelect {


    companion object {
        const val MAX_DAY_COUNT = 35

        const val NUM_DAY_OF_WEEK = 7
    }

    public val DEFAULT_DATE_FORMAT = "yyyy-MM"

    private var dateFormat = DEFAULT_DATE_FORMAT

    private lateinit var buttonBack: ImageView
    private lateinit var buttonNext: ImageView
    private lateinit var buttonBackLarge: ImageView
    private lateinit var buttonNextLarge: ImageView
    private lateinit var txtDate: TextView
    private lateinit var gridRecycler: RecyclerView
    private lateinit var currentDateCalendar: Calendar
    private var eventHandler: EventBetweenCalendarAndFragment? = null
    private var todayMonth: Int = -1
    private var todayYear: Int = -1

    val viewModel = ShapeRecordViewModel()


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
            inflater.inflate(R.layout.fragment_shape_record_calendar, this, true)
            buttonBack = findViewById(R.id.toBack)
            buttonNext = findViewById(R.id.toNext)
            buttonBackLarge = findViewById(R.id.buttonBack)
            buttonNextLarge = findViewById(R.id.buttonNext)
            txtDate = findViewById(R.id.itemDate)
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

            buttonBackLarge.setOnClickListener{
                currentDateCalendar.add(Calendar.MONTH, -1)
                eventHandler?.onCalendarPreviousPressed()
                checkStateNextButton()
            }

            ViewCompat.setNestedScrollingEnabled(gridRecycler, false)

            gridRecycler.layoutManager = GridLayoutManager(context, NUM_DAY_OF_WEEK)
            gridRecycler.addItemDecoration(
                SpaceItemDecoration(
                    resources.getDimension(R.dimen._1sdp).toInt(),
                    true
                )
            )

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



        val calendarAdapter = ShapeCalendarAdapter().apply {
            this.listDates = mCellList
            this.context = getContext()
            this.showingDateCalendar = currentDateCalendar
            this.listener = this@ShapeCalendarFragment
            this.recordedDates = recordedDate.value ?: listOf()
            this.selectedDateBefore = selectedDayOut
        }

        gridRecycler.adapter = calendarAdapter
        setHeader(currentDateCalendar)

    }

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

    val _fireShapeM = MutableLiveData<List<Shape>>()
    val fireShapeM : LiveData<List<Shape>>
        get() = _fireShapeM

    fun fireShapeBackM (shape: List<Shape>){
        _fireShapeM.value = shape
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
            val shapeRecord = users
                .document(userUid).collection("Shape")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereLessThanOrEqualTo("timestamp", Timestamp.valueOf(
                    "${currentDateCalendar.get(Calendar.YEAR)}-${currentDateCalendar.get(Calendar.MONTH)+1}-" +
                            "${getLastMonthLastDate()} 23:59:59.000000000"
                ))
                .whereGreaterThanOrEqualTo("timestamp", Timestamp.valueOf(
                    "${currentDateCalendar.get(Calendar.YEAR)}-${currentDateCalendar.get(Calendar.MONTH)+1}-" +
                            "01 00:00:00.000000000"
                ))


            shapeRecord
                .get()
                .addOnSuccessListener {
                    val items = mutableListOf<Shape>()
                    val dates = mutableListOf<String>()
                    items.clear()
                    dates.clear()
                    for (document in it) {
                        val convertDate = java.sql.Date(document.toObject(Shape::class.java).timestamp!!.time)
                        Logger.i("convertDate = $convertDate")
                        if (date.value != null && "${sdf.format(convertDate).split("-")[0]}-" +
                            "${sdf.format(convertDate).split("-")[1]}" ==
                            "${sdf.format(date.value)!!.split("-")[0]}-" +
                            "${sdf.format(date.value)!!.split("-")[1]}"){
                            items.add(document.toObject(Shape::class.java))
                            items[items.size-1].docId = document.id
                            document.toObject(Shape::class.java).timestamp?.let {
                                dates.add(sdf.format(java.sql.Date(it.time)))
                            }
                        }
                    }

                    fireShapeBackM(items)
                    setRecordedDate(dates)
                    Logger.i("fireFoodieM =${fireShapeM.value} setRecordedDate dates = ${recordedDate.value}")
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


    var selectDateOut: Date ?= null

    override fun onDateSelect(selectDate: Date) {
        val tempAdapter = gridRecycler.adapter as ShapeCalendarAdapter
        tempAdapter.selectedDate = selectDate
        tempAdapter.notifyDataSetChanged()

        val tempCalendar = Calendar.getInstance()
        tempCalendar.time = selectDate
        filterdate(selectDate)
        tempAdapter.recordedDates = recordedDate.value!!
        selectDateOut = selectDate
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