package com.terricom.mytype.linechart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.terricom.mytype.App
import com.terricom.mytype.Logger
import com.terricom.mytype.R
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.Sleep
import com.terricom.mytype.data.UserManager
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LinechartViewModel: ViewModel() {

    val userUid = UserManager.uid

    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val sdfM = SimpleDateFormat("M/d")
    val currentDate = sdf.format(Date())

    private val _date = MutableLiveData<String>()
    val date: LiveData<String>
        get() = _date

    private val _dateM = MutableLiveData<String>()
    val dateM: LiveData<String>
        get() = _dateM

    private val _recordDate = MutableLiveData<Date>()
    val recordDate: LiveData<Date>
        get() = _recordDate

    private val _currentPotition = MutableLiveData<Int>()
    val currentPotition: LiveData<Int>
        get() = _currentPotition

    fun setCurrentPosition(po: Int){
        _currentPotition.value = po
    }


    fun setDate(date: Date){//date format should be java.util.Date
        _date.value = sdf.format(date)
        _dateM.value = "${sdfM.format(date.time.minus(518400000L))}-${sdfM.format(date)}"
        _recordDate.value = date
        Logger.i("viewModel.date.observe = ${dateM.value}")

    }

    val _fireFoodie = MutableLiveData<List<Foodie>>()
    val fireFoodie : LiveData<List<Foodie>>
        get() = _fireFoodie

    fun fireFoodieBack (foo: List<Foodie>){
        _fireFoodie.value = foo
    }

    val _fireDate = MutableLiveData<ArrayList<String>>()
    val fireDate : LiveData<ArrayList<String>>
        get() = _fireDate

    fun fireDateBack (foo: ArrayList<String>){
        _fireDate.value = foo
    }

    val _waterList = MutableLiveData<FloatArray>()
    val waterList : LiveData<FloatArray>
        get() = _waterList

    fun waterListBack (foo: FloatArray){
        _waterList.value = foo
    }

    val _oilList = MutableLiveData<FloatArray>()
    val oilList : LiveData<FloatArray>
        get() = _oilList

    fun oilListBack (foo: FloatArray){
        _oilList.value = foo
    }

    val _vegetableList = MutableLiveData<FloatArray>()
    val vegetableList : LiveData<FloatArray>
        get() = _vegetableList

    fun vegetableListBack (foo: FloatArray){
        _vegetableList.value = foo
    }

    val _proteinList = MutableLiveData<FloatArray>()
    val proteinList : LiveData<FloatArray>
        get() = _proteinList

    fun proteinListBack (foo: FloatArray){
        _proteinList.value = foo
    }

    val _fruitList = MutableLiveData<FloatArray>()
    val fruitList : LiveData<FloatArray>
        get() = _fruitList

    fun fruitListBack (foo: FloatArray){
        _fruitList.value = foo
    }

    val _carbonList = MutableLiveData<FloatArray>()
    val carbonList : LiveData<FloatArray>
        get() = _carbonList

    fun carbonListBack (foo: FloatArray){
        _carbonList.value = foo
    }

    val _fireBackEnd = MutableLiveData<Boolean>()
    val fireBackEnd : LiveData<Boolean>
        get() = _fireBackEnd

    fun finishFireBack (){
        _fireBackEnd.value = true
    }
    fun newFireBack (){
        _fireBackEnd.value = null
    }

    val _waterClicked = MutableLiveData<Boolean>()
    val waterClicked : LiveData<Boolean>
        get() = _waterClicked

    fun watchWaterClicked (){
        _waterClicked.value = true
    }

    fun clearData(){
        _waterList.value = null
        _oilList.value = null
        _vegetableList.value = null
        _proteinList.value = null
        _fruitList.value = null
        _carbonList.value = null
    }

    val _listDates = MutableLiveData<ArrayList<ChartEntity>>()
    val listDates : LiveData<ArrayList<ChartEntity>>
        get() = _listDates

    fun setListDates(listDates: ArrayList<ChartEntity>){
        _listDates.value = listDates
    }


    val db = FirebaseFirestore.getInstance()
    val users: CollectionReference = db.collection("Users")

    init {
        newFireBack()
        setDate(Date())
    }

    fun getThisMonth() {
        if (userUid!!.isNotEmpty()){
            val foodieDiary = users
                .document(userUid).collection("Foodie")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .whereLessThanOrEqualTo("timestamp", Timestamp(recordDate.value!!.time) )
                .whereGreaterThanOrEqualTo("timestamp", Timestamp(recordDate.value!!.time.minus(518400000L)))
            val sleepDiary = users
                .document(userUid).collection("Sleep")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .whereLessThanOrEqualTo("timestamp", Timestamp(recordDate.value!!.time) )
                .whereGreaterThanOrEqualTo("timestamp", Timestamp(recordDate.value!!.time.minus(518400000L)))

            val chartList = mutableListOf<ChartEntity>()



            foodieDiary
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val items = mutableListOf<Foodie>()
                    val datelist = mutableListOf<String>()
                    val waterList = mutableListOf<Float>()
                    val oilList = mutableListOf<Float>()
                    val vegetableList = mutableListOf<Float>()
                    val proteinList = mutableListOf<Float>()
                    val fruitList = mutableListOf<Float>()
                    val carbonList = mutableListOf<Float>()

                    var waterD= mutableListOf<Float>()
                    val oilD= mutableListOf<Float>()
                    val vegetableD= mutableListOf<Float>()
                    val proteinD= mutableListOf<Float>()
                    val fruitD= mutableListOf<Float>()
                    val carbonD= mutableListOf<Float>()
                    for (document in querySnapshot) {
                        val convertDate = java.sql.Date(document.toObject(Foodie::class.java).timestamp!!.time)
                        if (date.value != null && "${sdf.format(convertDate).split("-")[0]}-" +
                            "${sdf.format(convertDate).split("-")[1]}" ==
                            "${date.value!!.split("-")[0]}-${date.value!!.split("-")[1]}"){
                            items.add(document.toObject(Foodie::class.java))
                            datelist.add(sdfM.format(document.toObject(Foodie::class.java).timestamp))
                        }
                    }
                    val cleanList = datelist.distinct()
                    val sleepList = mutableListOf<Float>()
                    val itemSleep = mutableListOf<Sleep>()
                    val dateListSleep = mutableListOf<String>()

//                    sleepDiary
//                        .get()
//                        .addOnSuccessListener {
//
//                            val sleepListD = mutableListOf<Float>()
//
//                            for (document in it){
//                                val convertDate = java.sql.Date(document.toObject(Sleep::class.java).timestamp!!.time)
//                                if (date.value != null && "${sdf.format(convertDate).split("-")[0]}-" +
//                                    "${sdf.format(convertDate).split("-")[1]}" ==
//                                    "${date.value!!.split("-")[0]}-${date.value!!.split("-")[1]}"){
//                                    itemSleep.add(document.toObject(Sleep::class.java))
//                                    dateListSleep.add(sdfM.format(document.toObject(Sleep::class.java).timestamp))
//                                }
//                            }
//
//                        }
//                    Logger.i("itemSleep =$itemSleep")
//                    chartList.clear()
                    for (eachDay in cleanList){
                        waterD.clear()
                        oilD.clear()
                        vegetableD.clear()
                        proteinD.clear()
                        fruitD.clear()
                        carbonD.clear()
                        for (i in 0 until items.size){
                            if (sdfM.format(items[i].timestamp?.time) == eachDay){
                                items[i].water?.let {
                                    waterD.add(it)
                                }
                                items[i].oil?.let {
                                    oilD.add(it)
                                }
                                items[i].vegetable?.let {
                                    vegetableD.add(it)
                                }
                                items[i].protein?.let {
                                    proteinD.add(it)
                                }
                                items[i].fruit?.let {
                                    fruitD.add(it)
                                }
                                items[i].carbon?.let {
                                    carbonD.add(it)
                                }
                            }
                        }
                        waterList.add(waterD.sum())
                        oilList.add(oilD.sum())
                        vegetableList.add(vegetableD.sum())
                        proteinList.add(proteinD.sum())
                        fruitList.add(fruitD.sum())
                        carbonList.add(carbonD.sum())

//                        for (i in 0 until itemSleep.size){
//                        if (dateListSleep.contains(eachDay)){
//                            sleepList.add(itemSleep[i].sleepHr ?: 0f)
//                        } else if (sdfM.format(itemSleep[i].timestamp) != eachDay){
//                            sleepList.add(0f)
//                        }
//                        }

                    }
                    Logger.i("waterList =$waterList oilList = $oilList sleepList =$sleepList")
                    fireFoodieBack(items)
                    fireDateBack(ArrayList(cleanList))
                    waterListBack(waterList.toFloatArray())
                    oilListBack(oilList.toFloatArray())
                    vegetableListBack(vegetableList.toFloatArray())
                    proteinListBack(proteinList.toFloatArray())
                    fruitListBack(fruitList.toFloatArray())
                    carbonListBack(carbonList.toFloatArray())

                    chartList.add(ChartEntity(App.applicationContext().getColor(R.color.colorWater), waterList.toFloatArray()))
                    chartList.add(ChartEntity(App.applicationContext().getColor(R.color.colorOil), oilList.toFloatArray()))
                    chartList.add(ChartEntity(App.applicationContext().getColor(R.color.colorVegetable), vegetableList.toFloatArray()))
                    chartList.add(ChartEntity(App.applicationContext().getColor(R.color.colorProtein), proteinList.toFloatArray()))
                    chartList.add(ChartEntity(App.applicationContext().getColor(R.color.colorFruit), fruitList.toFloatArray()))
                    chartList.add(ChartEntity(App.applicationContext().getColor(R.color.colorCarbon), carbonList.toFloatArray()))
//                    chartList.add(ChartEntity(App.applicationContext().getColor(R.color.colorNight), sleepList.toFloatArray()))

                    setListDates(chartList.toCollection(ArrayList()))
                    Logger.i("chartList.size =${chartList.size}")
                    _listDates.value = null
                    Logger.i("LinechartViewModel fireDate = ${fireDate.value} cleanList = $cleanList")
                }
        }
    }
}