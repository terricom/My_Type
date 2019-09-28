package com.terricom.mytype.query

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.Goal
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.tools.Logger

class QueryViewModel: ViewModel(){

    val userUid = UserManager.uid

    val _queryFoodie = MutableLiveData<List<Foodie>>()
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
        getGoal()
    }


    fun calculateAverage(listFoodie: List<Foodie>) {

        _totalWater.value = 0f
        _totalOil.value = 0f
        _totalVegetable.value = 0f
        _totalProtein.value = 0f
        _totalFruit.value = 0f
        _totalCarbon.value = 0f

        for (foodie in listFoodie){
            _totalWater.value = _totalWater.value!!.plus(foodie.water ?: 0f)
            _totalOil.value = _totalOil.value!!.plus(foodie.oil ?: 0f)
            _totalVegetable.value = _totalVegetable.value!!.plus(foodie.vegetable ?: 0f)
            _totalProtein.value = _totalProtein.value!!.plus(foodie.protein ?: 0f)
            _totalCarbon.value = _totalCarbon.value!!.plus(foodie.carbon ?: 0f)
            _totalFruit.value = _totalFruit.value!!.plus(foodie.fruit ?:0f)
        }
        averageWater.value = "%.1f".format(_totalWater.value!!/listFoodie.size)
        averageOil.value = "%.1f".format(_totalOil.value!!/listFoodie.size)
        averageVegetable.value = "%.1f".format(_totalVegetable.value!!/listFoodie.size)
        averageCarbon.value = "%.1f".format(_totalCarbon.value!!/listFoodie.size)
        averageFruit.value = "%.1f".format(_totalFruit.value!!/listFoodie.size)
        averageProtein.value = "%.1f".format(_totalProtein.value!!/listFoodie.size)

//        diffWater.value = "%.1f".format((averageWater.value ?: 0f).minus((goalWater.value?:0f).toString().toFloat()))
//        diffFruit.value = "%.1f".format((averageFruit.value ?: 0f).minus((goalFruit.value?:0f).toString().toFloat()))
//        diffCarbon.value = "%.1f".format((averageCarbon.value ?: 0f).minus((goalCarbon.value?:0f).toString().toFloat()))
//        diffOil.value = "%.1f".format((averageOil.value ?: 0f).minus((goalOil.value?:0f).toString().toFloat()))
//        diffProtein.value = "%.1f".format((averageProtein.value ?: 0f).minus((goalProtein.value?:0f).toString().toFloat()))
//        diffVegetable.value = "%.1f".format((averageVegetable.value ?: 0f).minus((goalVegetable.value?:0f).toString().toFloat()))
//
//        diffWaterNum.value = (averageWater.value ?: 0f).minus((goalWater.value?:0f).toString().toFloat())
//        diffFruitNum.value = (averageFruit.value ?: 0f).minus((goalFruit.value?:0f).toString().toFloat())
//        diffCarbonNum.value = (averageCarbon.value ?: 0f).minus((goalCarbon.value?:0f).toString().toFloat())
//        diffOilNum.value = (averageOil.value ?: 0f).minus((goalOil.value?:0f).toString().toFloat())
//        diffProteinNum.value = (averageProtein.value ?: 0f).minus((goalProtein.value?:0f).toString().toFloat())
//        diffVegetableNum.value = (averageVegetable.value ?: 0f).minus((goalVegetable.value?:0f).toString().toFloat())

    }

    val _totalWater = MutableLiveData<Float>()
    val _totalOil = MutableLiveData<Float>()
    val _totalVegetable = MutableLiveData<Float>()
    val _totalProtein = MutableLiveData<Float>()
    val _totalFruit = MutableLiveData<Float>()
    val _totalCarbon = MutableLiveData<Float>()

    val averageWater = MutableLiveData<String>()
    val averageOil = MutableLiveData<String>()
    val averageVegetable = MutableLiveData<String>()
    val averageProtein = MutableLiveData<String>()
    val averageFruit = MutableLiveData<String>()
    val averageCarbon = MutableLiveData<String>()


    val totalWater: LiveData<Float>
        get() = _totalWater

    val totalOil: LiveData<Float>
        get() = _totalOil

    val totalVegetable: LiveData<Float>
        get() = _totalVegetable

    val totalProtein: LiveData<Float>
        get() = _totalProtein

    val totalFruit: LiveData<Float>
        get() = _totalFruit

    val totalCarbon: LiveData<Float>
        get() = _totalCarbon

    fun getGoal() {
        Logger.i("userUID = $userUid")
        val db = FirebaseFirestore.getInstance()
        val users = db.collection("Users")

        if (userUid!!.isNotEmpty()){
            val goal = users
                .document(userUid)
                .collection("Goal")
                .orderBy("timestamp", Query.Direction.DESCENDING)
//                .whereLessThanOrEqualTo("timestamp", Timestamp(recordDate.value!!.time) )
//                .whereGreaterThanOrEqualTo("timestamp", Timestamp(recordDate.value!!.time.minus(604800000L)))

            goal
                .get()
                .addOnSuccessListener {
                    val items = mutableListOf<Goal>()
                    if (it.isEmpty){
                        Logger.i("Goal Document is empty")
                    }else {
                        for (document in it) {
                            items.add(document.toObject(Goal::class.java))
                            items[items.size-1].docId = document.id
                        }
                        Logger.i("items in LinechartViewModel = $items")
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