package com.terricom.mytype.linechart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.terricom.mytype.*
import com.terricom.mytype.data.*
import com.terricom.mytype.tools.*
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList


class LineChartViewModel: ViewModel() {

    val userUid = UserManager.uid

    private val _date = MutableLiveData<String>()
    val date: LiveData<String>
        get() = _date

    private val _dateThisWeek = MutableLiveData<String>()
    val dateThisWeek: LiveData<String>
        get() = _dateThisWeek

    private val _recordDate = MutableLiveData<Date>()
    val recordDate: LiveData<Date>
        get() = _recordDate


    fun setDate(date: Date){

        _date.value = date.toDateFormat(FORMAT_YYYY_MM_DD)
        _dateThisWeek.value = "${Date(date.time.minus(518400000L)).toDateFormat(FORMAT_MM_DD)} " +
                "~ ${date.toDateFormat(FORMAT_MM_DD)}"
        _recordDate.value = date

    }

    private val _chartListDate = MutableLiveData<ArrayList<String>>()
    val chartListDate : LiveData<ArrayList<String>>
        get() = _chartListDate

    private fun fireDateBack (foodList: ArrayList<String>){
        _chartListDate.value = foodList
    }

    val _listDates = MutableLiveData<ArrayList<ChartEntity>>()
    val listDates : LiveData<ArrayList<ChartEntity>>
        get() = _listDates

    private fun setListDates(listDates: ArrayList<ChartEntity>){
        _listDates.value = listDates
    }

    private val _foodieSum = MutableLiveData<List<FoodieSum>>()
    val foodieSum : LiveData<List<FoodieSum>>
        get() = _foodieSum

    private fun setFoodieSum(fooSum: List<FoodieSum>){
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
        setDate(Date())
    }

    fun getThisMonth() {

        if (UserManager.isLogin()){

            getGoal()

            UserManager.USER_REFERENCE?.let { userDocumnet ->

                userDocumnet.collection(FirebaseKey.COLLECTION_FOODIE)
                    .orderBy(FirebaseKey.TIMESTAMP, Query.Direction.ASCENDING)
                    .whereLessThanOrEqualTo(FirebaseKey.TIMESTAMP, Timestamp(Timestamp.valueOf(
                        App.applicationContext().getString(R.string.timestamp_dayend,
                            recordDate.value.toDateFormat(FORMAT_YYYY_MM_DD))
                    ).time))
                    .whereGreaterThanOrEqualTo(FirebaseKey.TIMESTAMP, Timestamp(Timestamp.valueOf(
                        App.applicationContext().getString(R.string.timestamp_dayend,
                            recordDate.value.toDateFormat(FORMAT_YYYY_MM_DD))
                    ).time.minus(604800000L)))
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val items = mutableListOf<Foodie>()
                        val dateList = mutableListOf<String>()
                        val waterList = mutableListOf<Float>()
                        val oilList = mutableListOf<Float>()
                        val vegetableList = mutableListOf<Float>()
                        val proteinList = mutableListOf<Float>()
                        val fruitList = mutableListOf<Float>()
                        val carbonList = mutableListOf<Float>()
                        val foodieSum = mutableListOf<FoodieSum>()

                        val waterListTemp= mutableListOf<Float>()
                        val oilListTemp= mutableListOf<Float>()
                        val vegetableListTemp= mutableListOf<Float>()
                        val proteinListTemp= mutableListOf<Float>()
                        val fruitListTemp= mutableListOf<Float>()
                        val carbonListTemp= mutableListOf<Float>()

                        for (document in querySnapshot) {

                            items.add(document.toObject(Foodie::class.java))
                            items[items.lastIndex].docId = document.id
                            dateList.add(document.toObject(Foodie::class.java).timestamp.toDateFormat(
                                FORMAT_MM_DD
                            ))
                        }

                        val dateListClean = mutableListOf<String>()

                        for (eachDay in dateList.distinct()){

                            waterListTemp.clear()
                            oilListTemp.clear()
                            vegetableListTemp.clear()
                            proteinListTemp.clear()
                            fruitListTemp.clear()
                            carbonListTemp.clear()

                            for (i in 0 until items.size){

                                if (items[i].timestamp.toDateFormat(FORMAT_MM_DD) == eachDay){

                                    dateListClean.add(items[i].timestamp.toDateFormat(
                                        FORMAT_YYYY_MM_DD
                                    ))
                                    items[i].water?.let {
                                        waterListTemp.add(it)
                                    }
                                    items[i].oil?.let {
                                        oilListTemp.add(it)
                                    }
                                    items[i].vegetable?.let {
                                        vegetableListTemp.add(it)
                                    }
                                    items[i].protein?.let {
                                        proteinListTemp.add(it)
                                    }
                                    items[i].fruit?.let {
                                        fruitListTemp.add(it)
                                    }
                                    items[i].carbon?.let {
                                        carbonListTemp.add(it)
                                    }
                                }
                            }
                            dateListClean.distinct()
                            waterList.add(waterListTemp.sum())
                            oilList.add(oilListTemp.sum())
                            vegetableList.add(vegetableListTemp.sum())
                            proteinList.add(proteinListTemp.sum())
                            fruitList.add(fruitListTemp.sum())
                            carbonList.add(carbonListTemp.sum())
                            foodieSum.add(FoodieSum(
                                dateListClean[dateListClean.size-1],
                                waterListTemp.sum(),
                                oilListTemp.sum(),
                                vegetableListTemp.sum(),
                                proteinListTemp.sum(),
                                fruitListTemp.sum(),
                                carbonListTemp.sum()
                            ))

                        }
                        setFoodieSum(foodieSum)
                        if (waterList.size > 0){

                            diffWater.value =
                                waterList[waterList.lastIndex]
                                    .minus(goalWater.value.toFloatFormat()).toDemicalPoint(1)


                            diffWaterNum.value =
                                waterList[waterList.lastIndex]
                                    .minus(goalWater.value.toFloatFormat())

                        }
                        if (fruitList.size > 0){

                            diffFruit.value =
                                fruitList[fruitList.lastIndex]
                                    .minus(goalFruit.value.toFloatFormat()).toDemicalPoint(1)


                            diffFruitNum.value =
                                fruitList[fruitList.lastIndex]
                                    .minus(goalFruit.value.toFloatFormat())

                        }
                        if (oilList.size > 0){

                            diffOil.value =
                                oilList[oilList.lastIndex]
                                    .minus(goalOil.value.toFloatFormat()).toDemicalPoint(1)

                            diffOilNum.value =
                                oilList[oilList.lastIndex]
                                    .minus(goalOil.value.toFloatFormat())
                        }
                        if (proteinList.size > 0){

                            diffProtein.value =
                                proteinList[proteinList.lastIndex]
                                    .minus(goalProtein.value.toFloatFormat()).toDemicalPoint(1)

                            diffProteinNum.value =
                                proteinList[proteinList.lastIndex]
                                    .minus(goalProtein.value.toFloatFormat())
                        }
                        if (vegetableList.size > 0){

                            diffVegetable.value =
                                vegetableList[vegetableList.lastIndex]
                                    .minus(goalVegetable.value.toFloatFormat()).toDemicalPoint(1)

                            diffVegetableNum.value =
                                vegetableList[vegetableList.lastIndex]
                                    .minus(goalVegetable.value.toFloatFormat())
                        }
                        if (carbonList.size > 0){

                            diffCarbon.value =
                                carbonList[carbonList.lastIndex]
                                    .minus(goalCarbon.value.toFloatFormat()).toDemicalPoint(1)

                            diffCarbonNum.value =
                                carbonList[carbonList.lastIndex]
                                    .minus(goalCarbon.value.toFloatFormat())
                        }

                        fireDateBack(ArrayList(dateList))

                        val chartList = mutableListOf<ChartEntity>()

                        chartList.add(ChartEntity(App.applicationContext().getColor(R.color.colorWater), waterList.toFloatArray()))
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
    }

    private fun getGoal() {

        if (UserManager.isLogin()){

            UserManager.USER_REFERENCE?.let { userDocument ->

                userDocument.collection(FirebaseKey.COLLECTION_GOAL)
                    .orderBy(FirebaseKey.TIMESTAMP, Query.Direction.DESCENDING)
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
                                goalWater.value = items[0].water.toDemicalPoint(1)
                                goalCarbon.value = items[0].carbon.toDemicalPoint(1)
                                goalOil.value = items[0].oil.toDemicalPoint(1)
                                goalFruit.value = items[0].fruit.toDemicalPoint(1)
                                goalProtein.value = items[0].protein.toDemicalPoint(1)
                                goalVegetable.value = items[0].vegetable.toDemicalPoint(1)
                            } else if (items.size == 0){
                                goalWater.value = 0.0f.toDemicalPoint(1)
                                goalCarbon.value = 0.0f.toDemicalPoint(1)
                                goalOil.value = 0.0f.toDemicalPoint(1)
                                goalFruit.value = 0.0f.toDemicalPoint(1)
                                goalProtein.value = 0.0f.toDemicalPoint(1)
                                goalVegetable.value = 0.0f.toDemicalPoint(1)
                            }
                        }
                    }
            }
        }
    }

}