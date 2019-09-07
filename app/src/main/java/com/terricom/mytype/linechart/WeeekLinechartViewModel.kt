package com.terricom.mytype.linechart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.terricom.mytype.Logger
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.UserManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WeeekLinechartViewModel: ViewModel() {

    val userUid = UserManager.uid

    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val sdfM = SimpleDateFormat("M/d")
    val currentDate = sdf.format(Date())

    private val _date = MutableLiveData<String>()

    val date: LiveData<String>
        get() = _date

    fun setDate(date: String){
        _date.value = date
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

    val _waterClicked = MutableLiveData<Boolean>()
    val waterClicked : LiveData<Boolean>
        get() = _waterClicked

    fun watchWaterClicked (){
        _waterClicked.value = true
    }


    val db = FirebaseFirestore.getInstance()
    val users: CollectionReference = db.collection("Users")

    init {
        setDate(currentDate)
        getThisMonth()
    }

    fun getThisMonth() {

        if (userUid!!.isNotEmpty()){
            val foodieDiary = users
                .document(userUid as String).collection("Foodie")
                .orderBy("timestamp", Query.Direction.ASCENDING)
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
                    }
                    Logger.i("waterList =$waterList oilList = $oilList")
                    fireFoodieBack(items)
                    fireDateBack(ArrayList(cleanList))
                    waterListBack(waterList.toFloatArray())
                    oilListBack(oilList.toFloatArray())
                    vegetableListBack(vegetableList.toFloatArray())
                    proteinListBack(proteinList.toFloatArray())
                    fruitListBack(fruitList.toFloatArray())
                    carbonListBack(carbonList.toFloatArray())
                    finishFireBack()
                    Logger.i("WeeekLinechartViewModel fireDate = ${fireDate.value} cleanList = $cleanList")
                }
        }
    }
}