package com.terricom.mytype.query

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.terricom.mytype.data.FirebaseKey
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.Goal
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.tools.toDemicalPoint

class QueryViewModel: ViewModel(){

    val userUid = UserManager.uid

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

    init {
        getGoalFromFirebase()
    }


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



    private fun getGoalFromFirebase() {

        if (UserManager.isLogin()){

            UserManager.USER_REFERENCE?.let {userDocument ->

                userDocument.collection(FirebaseKey.COLLECTION_GOAL)
                    .orderBy(FirebaseKey.TIMESTAMP, Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener {

                        val items = mutableListOf<Goal>()

                        for (document in it) {

                            items.add(document.toObject(Goal::class.java))
                            items[items.size-1].docId = document.id
                        }

                        when (items.size){

                            0 -> {

                                goalWater.value = 0.0f.toDemicalPoint(1)
                                goalCarbon.value = 0.0f.toDemicalPoint(1)
                                goalOil.value = 0.0f.toDemicalPoint(1)
                                goalFruit.value = 0.0f.toDemicalPoint(1)
                                goalProtein.value = 0.0f.toDemicalPoint(1)
                                goalVegetable.value = 0.0f.toDemicalPoint(1)
                            }

                            else -> {

                                goalWater.value = items[0].water.toDemicalPoint(1)
                                goalCarbon.value = items[0].carbon.toDemicalPoint(1)
                                goalOil.value = items[0].oil.toDemicalPoint(1)
                                goalFruit.value = items[0].fruit.toDemicalPoint(1)
                                goalProtein.value = items[0].protein.toDemicalPoint(1)
                                goalVegetable.value = items[0].vegetable.toDemicalPoint(1)
                            }
                        }
                    }
            }
        }
    }

}