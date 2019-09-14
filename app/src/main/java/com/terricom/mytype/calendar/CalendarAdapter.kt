package com.terricom.mytype.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.databinding.ItemCalendarDayBinding
import com.terricom.mytype.diary.DiaryViewModel
import java.util.*

class CalendarAdapter : RecyclerView.Adapter<CalendarViewHolder>() {

    lateinit var listDates: ArrayList<Date>
    lateinit var context: Context
    lateinit var showingDateCalendar: Calendar
    var selectedDate: Date ?= null
    var listener: ListenerCellSelect? = null

//    val userUid = UserManager.uid
//
//    val db = FirebaseFirestore.getInstance()
//    val users = db.collection("Users")
//
//    val _fireFoodieM = MutableLiveData<List<Foodie>>()
//    val fireFoodieM : LiveData<List<Foodie>>
//        get() = _fireFoodieM
//
//    fun fireFoodieBackM (foo: List<Foodie>){
//        _fireFoodieM.value = foo
//    }

//    val sdf = SimpleDateFormat("yyyy-MM-dd")
//
//    val _date = MutableLiveData<Date>()
//    val date : LiveData<Date>
//        get() = _date
//
//    fun filterdate(dato: Date){
//        Logger.i("CalendarViewHolder filterdate = ${dato}")
//        _date.value = dato
//    }
//
//    fun getThisMonth() {
//        if (userUid!!.isNotEmpty()){
//            Logger.i("whereGreaterThanOrEqualTo ${showingDateCalendar.get(Calendar.YEAR)}" +
//                    "-${showingDateCalendar.get(Calendar.MONTH)}-" +
//                    "01 00:00:00.000000000" +
//                    "whereLessThanOrEqualTo ${showingDateCalendar.get(Calendar.YEAR)}" +
//                    "-${showingDateCalendar.get(Calendar.MONTH)}-"+
//                    "${getLastMonthLastDate()} 23:59:59.000000000")
//            val foodieDiary = users
//                .document(userUid).collection("Foodie")
//                .orderBy("timestamp", Query.Direction.DESCENDING)
//                .whereLessThanOrEqualTo("timestamp", Timestamp.valueOf(
//                "${showingDateCalendar.get(Calendar.YEAR)}-${showingDateCalendar.get(Calendar.MONTH)}-" +
//                        "${getLastMonthLastDate()} 23:59:59.000000000"
//                ))
//                .whereGreaterThanOrEqualTo("timestamp", Timestamp.valueOf(
//                    "${showingDateCalendar.get(Calendar.YEAR)}-${showingDateCalendar.get(Calendar.MONTH)}-" +
//                            "01 00:00:00.000000000"
//                ))
//
//
//            foodieDiary
//                .get()
//                .addOnSuccessListener {
//                    val items = mutableListOf<Foodie>()
//                    for (document in it) {
//                        val convertDate = java.sql.Date(document.toObject(Foodie::class.java).timestamp!!.time)
//                        if (date.value != null && "${sdf.format(convertDate).split("-")[0]}-" +
//                            "${sdf.format(convertDate).split("-")[1]}" ==
//                            "${sdf.format(date.value)!!.split("-")[0]}-" +
//                            "${sdf.format(date.value)!!.split("-")[1]}"){
//                            items.add(document.toObject(Foodie::class.java))
//                        }
//                    }
//                    if (items.size != 0) {
//                    }
//                    fireFoodieBackM(items)
//                    Logger.i("fireFoodieM in CalendarAdapter =${fireFoodieM.value}")
//                }
//        }
//    }
//
//    fun getLastMonthLastDate(): Int {
////        val calendar = Calendar.getInstance()
//        showingDateCalendar.add(Calendar.MONTH, -1)
//
//        val max = showingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
//        showingDateCalendar.set(Calendar.DAY_OF_MONTH, max)
//
//        return showingDateCalendar.get(Calendar.DAY_OF_MONTH)
//    }



    override fun onCreateViewHolder(parent: ViewGroup, size: Int): CalendarViewHolder {
        context = parent.context
//        filterdate(selectedDate ?: Date())
//        getThisMonth()
        return CalendarViewHolder(ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context),parent,false))
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