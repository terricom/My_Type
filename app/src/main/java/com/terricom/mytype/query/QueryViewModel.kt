package com.terricom.mytype.query

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.Goal
import com.terricom.mytype.data.source.MyTypeRepository
import com.terricom.mytype.tools.toDemicalPoint

class QueryViewModel(myTypeRepository: MyTypeRepository): ViewModel(){

    val goal: LiveData<List<Goal>> = myTypeRepository.getGoal()

    private val _queryFoodie = MutableLiveData<List<Foodie>>()
    val queryFoodie: LiveData<List<Foodie>>
        get() = _queryFoodie

    fun setQueryFoodie(queryFoodie: List<Foodie>){
        _queryFoodie.value = queryFoodie
    }

    val goalWater = MutableLiveData<String>()
    val goalFruit = MutableLiveData<String>()
    val goalVegetable = MutableLiveData<String>()
    val goalOil = MutableLiveData<String>()
    val goalProtein = MutableLiveData<String>()
    val goalCarbon = MutableLiveData<String>()

    fun calculateAverage(listFoodie: List<Foodie>) {

        totalWater.value = 0f
        totalOil.value = 0f
        totalVegetable.value = 0f
        totalProtein.value = 0f
        totalFruit.value = 0f
        totalCarbon.value = 0f

        for (foodie in listFoodie){
            totalWater.value = (totalWater.value ?:0f).plus(foodie.water ?: 0f)
            totalOil.value = (totalOil.value ?: 0f).plus(foodie.oil ?: 0f)
            totalVegetable.value = (totalVegetable.value ?: 0f).plus(foodie.vegetable ?: 0f)
            totalProtein.value = (totalProtein.value ?: 0f).plus(foodie.protein ?: 0f)
            totalCarbon.value = (totalCarbon.value ?: 0f).plus(foodie.carbon ?: 0f)
            totalFruit.value = (totalFruit.value ?: 0f).plus(foodie.fruit ?:0f)
        }
        averageWater.value = (totalWater.value!!/listFoodie.size).toDemicalPoint(1)
        averageOil.value = (totalOil.value!!/listFoodie.size).toDemicalPoint(1)
        averageVegetable.value = (totalVegetable.value!!/listFoodie.size).toDemicalPoint(1)
        averageCarbon.value = (totalCarbon.value!!/listFoodie.size).toDemicalPoint(1)
        averageFruit.value = (totalFruit.value!!/listFoodie.size).toDemicalPoint(1)
        averageProtein.value = (totalProtein.value!!/listFoodie.size).toDemicalPoint(1)

    }

    private val totalWater = MutableLiveData<Float>()
    private val totalOil = MutableLiveData<Float>()
    private val totalVegetable = MutableLiveData<Float>()
    private val totalProtein = MutableLiveData<Float>()
    private val totalFruit = MutableLiveData<Float>()
    private val totalCarbon = MutableLiveData<Float>()

    val averageWater = MutableLiveData<String>()
    val averageOil = MutableLiveData<String>()
    val averageVegetable = MutableLiveData<String>()
    val averageProtein = MutableLiveData<String>()
    val averageFruit = MutableLiveData<String>()
    val averageCarbon = MutableLiveData<String>()

}