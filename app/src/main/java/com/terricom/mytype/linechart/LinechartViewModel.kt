package com.terricom.mytype.linechart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.FoodieSum
import com.terricom.mytype.data.Goal
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.tools.Logger
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class LinechartViewModel: ViewModel() {

    val userUid = UserManager.uid

    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val sdfM = SimpleDateFormat("MM-dd")
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
        _dateM.value = "${sdfM.format(date.time.minus(518400000L))} ~ ${sdfM.format(date)}"
        Logger.i("dateM = ${dateM.value}")
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

    val _foodieSum = MutableLiveData<List<FoodieSum>>()
    val foodieSum : LiveData<List<FoodieSum>>
        get() = _foodieSum

    fun setFoodieSum(fooSum: List<FoodieSum>){
        _foodieSum.value = fooSum
    }

    val goalWater = MutableLiveData<String>()
    val goalFruit = MutableLiveData<String>()
    val goalVegetable = MutableLiveData<String>()
    val goalOil = MutableLiveData<String>()
    val goalProtein = MutableLiveData<String>()
    val goalCarbon = MutableLiveData<String>()

    val diffWater = MutableLiveData<String>()
    val diffFruit = MutableLiveData<String>()
    val diffCarbon = MutableLiveData<String>()
    val diffOil = MutableLiveData<String>()
    val diffProtein = MutableLiveData<String>()
    val diffVegetable = MutableLiveData<String>()

    val diffWaterNum = MutableLiveData<Float>()
    val diffFruitNum = MutableLiveData<Float>()
    val diffCarbonNum = MutableLiveData<Float>()
    val diffOilNum = MutableLiveData<Float>()
    val diffProteinNum = MutableLiveData<Float>()
    val diffVegetableNum = MutableLiveData<Float>()

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
                .whereLessThanOrEqualTo("timestamp", Timestamp(Timestamp.valueOf("${sdf.format(recordDate.value)} 23:59:59.999999999").time))
                .whereGreaterThanOrEqualTo("timestamp", Timestamp(Timestamp.valueOf("${sdf.format(recordDate.value)} 23:59:59.999999999").time.minus(604800000L)))

            val chartList = mutableListOf<ChartEntity>()

            getGoal()

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
                    val foodieSum = mutableListOf<FoodieSum>()

                    var waterD= mutableListOf<Float>()
                    val oilD= mutableListOf<Float>()
                    val vegetableD= mutableListOf<Float>()
                    val proteinD= mutableListOf<Float>()
                    val fruitD= mutableListOf<Float>()
                    val carbonD= mutableListOf<Float>()
                    for (document in querySnapshot) {
                        items.add(document.toObject(Foodie::class.java))
                        items[items.lastIndex].docId = document.id
                        datelist.add(sdfM.format(document.toObject(Foodie::class.java).timestamp))
                    }
                    val cleanList = datelist.distinct().dropLastWhile { datelist.size == 8 }
                    val sleepList = mutableListOf<Float>()
                    val dateListClean = mutableListOf<String>()

                    for (eachDay in cleanList){
                        waterD.clear()
                        oilD.clear()
                        vegetableD.clear()
                        proteinD.clear()
                        fruitD.clear()
                        carbonD.clear()
                        for (i in 0 until items.size){
                            if (sdfM.format(items[i].timestamp?.time) == eachDay){
                                dateListClean.add(sdf.format(items[i].timestamp?.time))
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
                        dateListClean.distinct()
                        waterList.add(waterD.sum())
                        oilList.add(oilD.sum())
                        vegetableList.add(vegetableD.sum())
                        proteinList.add(proteinD.sum())
                        fruitList.add(fruitD.sum())
                        carbonList.add(carbonD.sum())
                        foodieSum.add(FoodieSum(
                            dateListClean[dateListClean.size-1],
                            waterD.sum(),
                            oilD.sum(),
                            vegetableD.sum(),
                            proteinD.sum(),
                            fruitD.sum(),
                            carbonD.sum()
                        ))

                    }
                    setFoodieSum(foodieSum)
                    if (waterList.size > 0){
                        diffWater.value = "%.1f".format(waterList[waterList.lastIndex].minus(
                            (if (goalWater.value == "null" || goalWater.value.isNullOrEmpty())"0" else goalWater.value)!!.toFloat()))
                        diffWaterNum.value = waterList[waterList.lastIndex].minus(
                            (if (goalWater.value == "null" || goalWater.value.isNullOrEmpty())"0" else goalWater.value)!!.toFloat())
                    }
                    if (fruitList.size > 0){
                        diffFruit.value = "%.1f".format(fruitList[fruitList.lastIndex].minus(
                            (if (goalFruit.value == "null" || goalFruit.value.isNullOrEmpty())"0" else goalFruit.value)!!.toFloat()))
                        diffFruitNum.value = fruitList[fruitList.lastIndex].minus(
                            (if (goalFruit.value == "null" || goalFruit.value.isNullOrEmpty())"0" else goalFruit.value)!!.toFloat())
                    }
                    if (oilList.size > 0){
                        diffOil.value = "%.1f".format(oilList[oilList.lastIndex].minus(
                            (if (goalOil.value == "null" || goalOil.value.isNullOrEmpty())"0" else goalOil.value)!!.toFloat()))
                        diffOilNum.value = oilList[oilList.lastIndex].minus(
                            (if (goalOil.value == "null" || goalOil.value.isNullOrEmpty())"0" else goalOil.value)!!.toFloat())
                    }
                    if (proteinList.size > 0){
                        diffProtein.value = "%.1f".format(proteinList[proteinList.lastIndex].minus(
                            (if (goalProtein.value == "null" || goalProtein.value.isNullOrEmpty())"0" else goalProtein.value)!!.toFloat()))
                        diffProteinNum.value = proteinList[proteinList.lastIndex].minus(
                            (if (goalProtein.value == "null" || goalProtein.value.isNullOrEmpty())"0" else goalProtein.value)!!.toFloat())
                    }
                    if (vegetableList.size > 0){
                        diffVegetable.value = "%.1f".format(vegetableList[vegetableList.lastIndex].minus(
                            (if (goalVegetable.value == "null" || goalVegetable.value.isNullOrEmpty())"0" else goalVegetable.value)!!.toFloat()))
                        diffVegetableNum.value = vegetableList[vegetableList.lastIndex].minus(
                            (if (goalVegetable.value == "null" || goalVegetable.value.isNullOrEmpty())"0" else goalVegetable.value)!!.toFloat())
                    }
                    if (carbonList.size > 0){
                        diffCarbon.value = "%.1f".format(carbonList[carbonList.lastIndex].minus(
                            (if (goalCarbon.value == "null" || goalCarbon.value.isNullOrEmpty())"0" else goalCarbon.value)!!.toFloat()))
                        diffCarbonNum.value = carbonList[carbonList.lastIndex].minus(
                            (if (goalCarbon.value == "null" || goalCarbon.value.isNullOrEmpty())"0" else goalCarbon.value)!!.toFloat())
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

                    chartList.add(ChartEntity(App.applicationContext().getColor(com.terricom.mytype.R.color.colorWater), waterList.toFloatArray()))
                    chartList.add(ChartEntity(App.applicationContext().getColor(R.color.colorOil), oilList.toFloatArray()))
                    chartList.add(ChartEntity(App.applicationContext().getColor(R.color.colorVegetable), vegetableList.toFloatArray()))
                    chartList.add(ChartEntity(App.applicationContext().getColor(R.color.colorProtein), proteinList.toFloatArray()))
                    chartList.add(ChartEntity(App.applicationContext().getColor(R.color.colorFruit), fruitList.toFloatArray()))
                    chartList.add(ChartEntity(App.applicationContext().getColor(R.color.colorCarbon), carbonList.toFloatArray()))

                    setListDates(chartList.toCollection(ArrayList()))
                    _listDates.value = null
                }
        }
    }

    fun getGoal() {
        val db = FirebaseFirestore.getInstance()
        val users = db.collection("Users")

        if (userUid!!.isNotEmpty()){
            val goal = users
                .document(userUid)
                .collection("Goal")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereLessThanOrEqualTo("timestamp", Timestamp(recordDate.value!!.time) )
                .whereGreaterThanOrEqualTo("timestamp", Timestamp(recordDate.value!!.time.minus(604800000L)))

            goal
                .get()
                .addOnSuccessListener {
                    val items = mutableListOf<Goal>()
                    if (it.isEmpty){
                    }else {
                        for (document in it) {
                            items.add(document.toObject(Goal::class.java))
                            items[items.size-1].docId = document.id
                        }
                        if (items.size > 0 ){
                        goalWater.value = "%.1f".format(items[0].water)
                        goalCarbon.value = "%.1f".format(items[0].carbon)
                        goalOil.value = "%.1f".format(items[0].oil)
                        goalFruit.value = "%.1f".format(items[0].fruit)
                        goalProtein.value = "%.1f".format(items[0].protein)
                        goalVegetable.value = "%.1f".format(items[0].vegetable)
                        } else if (items.size == 0){
                            goalWater.value = "%.1f".format(0.0f)
                            goalCarbon.value = "%.1f".format(0.0f)
                            goalOil.value = "%.1f".format(0.0f)
                            goalFruit.value = "%.1f".format(0.0f)
                            goalProtein.value = "%.1f".format(0.0f)
                            goalVegetable.value = "%.1f".format(0.0f)
                        }
                    }
                }
        }
    }

}