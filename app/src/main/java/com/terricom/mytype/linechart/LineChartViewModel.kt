package com.terricom.mytype.linechart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.data.FirebaseKey
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.FoodieSum
import com.terricom.mytype.data.Goal
import com.terricom.mytype.data.source.MyTypeRepository
import com.terricom.mytype.tools.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

class LineChartViewModel(private val myTypeRepository: MyTypeRepository): ViewModel() {

    private val _date = MutableLiveData<String>()
    val date: LiveData<String>
        get() = _date

    private val _dateThisWeek = MutableLiveData<String>()
    val dateThisWeek: LiveData<String>
        get() = _dateThisWeek

    private val _recordDate = MutableLiveData<Date>()
    val recordDate: LiveData<Date>
        get() = _recordDate

    private val _status = MutableLiveData<Boolean>()
    val status: LiveData<Boolean>
        get() = _status


    fun setDate(date: Date){

        _date.value = date.toDateFormat(FORMAT_YYYY_MM_DD)
        _dateThisWeek.value = "${Date(date.time.minus(518400000L)).toDateFormat(FORMAT_MM_DD)} " +
                "~ ${date.toDateFormat(FORMAT_MM_DD)}"
        _recordDate.value = date

    }

    val goal: LiveData<List<Goal>> = myTypeRepository.getGoal()

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

    init {
        setDate(Date())
    }

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun getThisMonth() {

        coroutineScope.launch {

            val foodieList = myTypeRepository.getObjects(
                FirebaseKey.COLLECTION_FOODIE,
                Timestamp(
                    Timestamp.valueOf(
                        App.applicationContext().getString(
                            R.string.timestamp_dayend,
                            recordDate.value.toDateFormat(FORMAT_YYYY_MM_DD)
                        )
                    ).time.minus(604800000L)
                ),
                Timestamp(
                    Timestamp.valueOf(
                        App.applicationContext().getString(
                            R.string.timestamp_dayend,
                            recordDate.value.toDateFormat(FORMAT_YYYY_MM_DD)
                        )
                    ).time
                )
            )
            val dateList = mutableListOf<String>()
            for (foodie in foodieList as List<Foodie>) {
                dateList.add(foodie.timestamp.toDateFormat(FORMAT_MM_DD))
            }
            val waterListTemp = mutableListOf<Float>()
            val oilListTemp = mutableListOf<Float>()
            val vegetableListTemp = mutableListOf<Float>()
            val proteinListTemp = mutableListOf<Float>()
            val fruitListTemp = mutableListOf<Float>()
            val carbonListTemp = mutableListOf<Float>()
            val dateListClean = mutableListOf<String>()

            val waterList = mutableListOf<Float>()
            val oilList = mutableListOf<Float>()
            val vegetableList = mutableListOf<Float>()
            val proteinList = mutableListOf<Float>()
            val fruitList = mutableListOf<Float>()
            val carbonList = mutableListOf<Float>()
            val foodieSum = mutableListOf<FoodieSum>()

            for (eachDay in dateList.distinct()) {

                waterListTemp.clear()
                oilListTemp.clear()
                vegetableListTemp.clear()
                proteinListTemp.clear()
                fruitListTemp.clear()
                carbonListTemp.clear()

                for (i in 0 until foodieList.size) {
                    if (foodieList[i].timestamp.toDateFormat(FORMAT_MM_DD) == eachDay) {

                        dateListClean.add(
                            foodieList[i].timestamp.toDateFormat(
                                FORMAT_YYYY_MM_DD
                            )
                        )
                        foodieList[i].water?.let {
                            waterListTemp.add(it)
                        }
                        foodieList[i].oil?.let {
                            oilListTemp.add(it)
                        }
                        foodieList[i].vegetable?.let {
                            vegetableListTemp.add(it)
                        }
                        foodieList[i].protein?.let {
                            proteinListTemp.add(it)
                        }
                        foodieList[i].fruit?.let {
                            fruitListTemp.add(it)
                        }
                        foodieList[i].carbon?.let {
                            carbonListTemp.add(it)
                        }
                    }
                }
                waterList.add(waterListTemp.sum())
                oilList.add(oilListTemp.sum())
                vegetableList.add(vegetableListTemp.sum())
                proteinList.add(proteinListTemp.sum())
                fruitList.add(fruitListTemp.sum())
                carbonList.add(carbonListTemp.sum())
                foodieSum.add(
                    FoodieSum(
                        dateListClean[dateListClean.size - 1],
                        waterListTemp.sum(),
                        oilListTemp.sum(),
                        vegetableListTemp.sum(),
                        proteinListTemp.sum(),
                        fruitListTemp.sum(),
                        carbonListTemp.sum()
                    )
                )
            }
            setFoodieSum(foodieSum)

            if (waterList.size > 0) {

                diffWater.value =
                    waterList[waterList.lastIndex]
                        .minus(goalWater.value.toFloatFormat()).toDemicalPoint(1)


                diffWaterNum.value =
                    waterList[waterList.lastIndex]
                        .minus(goalWater.value.toFloatFormat())

            }

            if (fruitList.size > 0) {

                diffFruit.value =
                    fruitList[fruitList.lastIndex]
                        .minus(goalFruit.value.toFloatFormat()).toDemicalPoint(1)


                diffFruitNum.value =
                    fruitList[fruitList.lastIndex]
                        .minus(goalFruit.value.toFloatFormat())

            }
            if (oilList.size > 0) {

                diffOil.value =
                    oilList[oilList.lastIndex]
                        .minus(goalOil.value.toFloatFormat()).toDemicalPoint(1)

                diffOilNum.value =
                    oilList[oilList.lastIndex]
                        .minus(goalOil.value.toFloatFormat())
            }
            if (proteinList.size > 0) {

                diffProtein.value =
                    proteinList[proteinList.lastIndex]
                        .minus(goalProtein.value.toFloatFormat()).toDemicalPoint(1)

                diffProteinNum.value =
                    proteinList[proteinList.lastIndex]
                        .minus(goalProtein.value.toFloatFormat())
            }
            if (vegetableList.size > 0) {

                diffVegetable.value =
                    vegetableList[vegetableList.lastIndex]
                        .minus(goalVegetable.value.toFloatFormat()).toDemicalPoint(1)

                diffVegetableNum.value =
                    vegetableList[vegetableList.lastIndex]
                        .minus(goalVegetable.value.toFloatFormat())
            }
            if (carbonList.size > 0) {

                diffCarbon.value =
                    carbonList[carbonList.lastIndex]
                        .minus(goalCarbon.value.toFloatFormat()).toDemicalPoint(1)

                diffCarbonNum.value =
                    carbonList[carbonList.lastIndex]
                        .minus(goalCarbon.value.toFloatFormat())
            }

            fireDateBack(ArrayList(dateList.distinct()))

            val chartList = mutableListOf<ChartEntity>()

            chartList.add(
                ChartEntity(
                    App.applicationContext().getColor(R.color.colorWater),
                    waterList.toFloatArray()
                )
            )
            chartList.add(
                ChartEntity(
                    App.applicationContext().getColor(R.color.colorOil),
                    oilList.toFloatArray()
                )
            )
            chartList.add(
                ChartEntity(
                    App.applicationContext().getColor(R.color.colorVegetable),
                    vegetableList.toFloatArray()
                )
            )
            chartList.add(
                ChartEntity(
                    App.applicationContext().getColor(R.color.colorProtein),
                    proteinList.toFloatArray()
                )
            )
            chartList.add(
                ChartEntity(
                    App.applicationContext().getColor(R.color.colorFruit),
                    fruitList.toFloatArray()
                )
            )
            chartList.add(
                ChartEntity(
                    App.applicationContext().getColor(R.color.colorCarbon),
                    carbonList.toFloatArray()
                )
            )

            setListDates(chartList.toCollection(ArrayList()))
            _status.value = true
            _listDates.value = null

        }
    }


}