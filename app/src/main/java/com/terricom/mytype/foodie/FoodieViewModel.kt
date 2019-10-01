package com.terricom.mytype.foodie

import android.net.Uri
import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.terricom.mytype.data.*
import com.terricom.mytype.tools.Logger
import java.sql.Time
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*




class FoodieViewModel: ViewModel() {

    val userUid = UserManager.uid

    var selectedFood = mutableListOf<String>()
    var selectedNutrition = mutableListOf<String>()

    val _userFoodList = MutableLiveData<List<String>>()
    val userFoodList : LiveData<List<String>>
        get() = _userFoodList

    val editFood = MutableLiveData<String>()
    val editNutrition = MutableLiveData<String>()

    val _selectedFoodList = MutableLiveData<List<String>>()
    val selectedFoodList: LiveData<List<String>>
        get() = _selectedFoodList

    fun addSelectedFoodList(list: List<String>){
        _selectedFoodList.value = list
    }

    val _selectedNutritionList = MutableLiveData<List<String>>()
    val selectedNutritionList: LiveData<List<String>>
        get() = _selectedNutritionList

    fun addSelectedNutritionList(list: List<String>){
        _selectedNutritionList.value = list
    }

    val newFuList = mutableListOf<String>()
    val newNuList = mutableListOf<String>()


    val _userNuList = MutableLiveData<List<String>>()
    val userNuList : LiveData<List<String>>
        get() = _userNuList

    fun getFoodlist(foodlist: List<String>){
        val newFooList = foodlist.toMutableList()
        newFooList.add("新增食物")
        Logger.i("getFoodlist newFooList = $newFooList")
        _userFoodList.value = newFooList
        for (food in foodlist){
            newFuList.add(food)
        }
    }

    fun getNulist(nulist: List<String>){
        val newNutritionList = nulist.toMutableList()
        newNutritionList.add("新增營養")
        _userNuList.value = newNutritionList
        for (nutrition in nulist){
            newNuList.add(nutrition)
        }
    }

    fun dragToList(food: String) {
        Logger.i("dragToList food =$food")
        selectedFood.add(food)
        addSelectedFoodList(selectedFood.distinct())
        newFuList.add(food)
        if (newFuList.contains("新增食物")){
            newFuList.remove("新增食物")
        }
    }

    fun dragOutList(food: String) {
        Logger.i("dragOutList food =$food")
        Logger.i("selectedFood before = $selectedFood")
        selectedFood.remove(food)
        Logger.i("selectedFood after = $selectedFood")
        addSelectedFoodList(selectedFood)
//        newFuList.remove(food)
    }


    fun dragToListNu(nutrition: String) {
        selectedNutrition.add(nutrition)
        addSelectedNutritionList(selectedNutrition.distinct())
        newNuList.add(nutrition)
        if (newNuList.contains("新增營養")){
            newNuList.remove("新增營養")
        }
    }

    fun dragOutListNu (nutrition: String) {
        selectedNutrition.remove(nutrition)
        addSelectedNutritionList(selectedNutrition)
//        newNuList.remove(nutrition)
    }

    val water =  MutableLiveData<Float>()
    val oil = MutableLiveData<Float>()
    val vegetable = MutableLiveData<Float>()
    val protein = MutableLiveData<Float>()
    val fruit = MutableLiveData<Float>()
    val carbon = MutableLiveData<Float>()
    val memo = MutableLiveData<String>()

    fun convertStringToFloat(string: String): Float {
        return try {
            string.toFloat()
        } catch (nfe: NumberFormatException) {
            0f
        }
    }

    @InverseMethod("convertStringToFloat")
    fun floatToString(value:Float) = value.toString()


    val _date = MutableLiveData<Date>()
    val date: LiveData<Date>
        get() = _date

    fun setDate(date: Date){
        _date.value = date
        _time.value = Time(date.time)
    }

    val _editDateClicked = MutableLiveData<Boolean>()
    val editDateClicked : LiveData<Boolean>
        get() = _editDateClicked

    fun editDateClicked(){
        _editDateClicked.value = true
    }

    fun editDateClickedAgain(){
        _editDateClicked.value = false
    }

    val _editTimeClicked = MutableLiveData<Boolean>()
    val editTimeClicked : LiveData<Boolean>
        get() = _editTimeClicked

    fun editTimeClicked(){
        _editTimeClicked.value = true
    }

    fun editTimeClickedAgain(){
        _editTimeClicked.value = false
    }

    val _time = MutableLiveData<Time>()
    val time : LiveData<Time>
        get() = _time

    private val _photoUri = MutableLiveData<Uri>()
    val photoUri: LiveData<Uri>
        get() = _photoUri

    fun setPhoto(photo: Uri){
        _photoUri.value = photo
        Logger.i("photouri get = $photo")
    }

    val _addNewFoodChecked = MutableLiveData<Boolean>()
    val addNewFoodChecked : LiveData<Boolean>
        get() = _addNewFoodChecked

    fun checkedAddNewFood(){
        _addNewFoodChecked.value = true
    }

    fun unCheckedAddNewFood(){
        _addNewFoodChecked.value = false
    }

    val _addNewNutritionChecked = MutableLiveData<Boolean>()
    val addNewNutritionChecked : LiveData<Boolean>
        get() = _addNewNutritionChecked

    fun checkedAddNewNutrition(){
        _addNewNutritionChecked.value = true
    }

    fun unCheckedAddNewNutrition(){
        _addNewNutritionChecked.value = false
    }

    val _updateFoodie = MutableLiveData<Foodie>()
    val updateFoodie : LiveData<Foodie>
        get() = _updateFoodie

    fun updateFoodie(foodie: Foodie){
        _updateFoodie.value = foodie
    }

    val _uploadFile = MutableLiveData<Boolean>()
    val uploadFile: LiveData<Boolean>
        get() = _uploadFile

    fun uploadFile(){
        _uploadFile.value = true
    }

    val db = FirebaseFirestore.getInstance()
    val user = db.collection("Users")

    val sdf = SimpleDateFormat("yyyy-MM-dd")

    fun addFoodie(){

        if (selectedFood.contains("新增食物")) {selectedFood.remove("新增食物")}
        if (selectedNutrition.contains("新增營養")) {selectedNutrition.remove("新增營養")}

        Logger.i("Timestamp Format = \"${sdf.format(date.value)} ${time.value}:00.000000000\")")
        //發文功能
        val foodieContent = hashMapOf(
            "timestamp" to Timestamp.valueOf("${sdf.format(date.value)} ${time.value}.000000000"),
            "water" to water.value,
            "oil" to oil.value,
            "vegetable" to vegetable.value,
            "protein" to protein.value,
            "fruit" to fruit.value,
            "carbon" to carbon.value,
            "photo" to photoUri.value.toString(),
            "foods" to selectedFood.distinct(),
            "nutritions" to selectedNutrition.distinct(),
            "memo" to memo.value
        )

        user.get()
            .addOnSuccessListener { result->
                Logger.i("FoodieViewModel userUid =$userUid")
                for (doc in result){
                    if (doc.id == userUid){
                        user.document(doc.id).collection("Foodie").document().set(foodieContent)
                    }
                }
                updatePuzzle()

            }



    }

    fun adjustFoodie(){

        if (selectedFood.contains("新增食物")) {selectedFood.remove("新增食物")}
        if (selectedNutrition.contains("新增營養")) {selectedNutrition.remove("新增營養")}
        Logger.i("SelectedFood = ${selectedFood.distinct()}")

        //發文功能
        val foodieContent = hashMapOf(
            "timestamp" to Timestamp.valueOf("${sdf.format(date.value)} ${time.value}.000000000"),
            "water" to water.value,
            "oil" to oil.value,
            "vegetable" to vegetable.value,
            "protein" to protein.value,
            "fruit" to fruit.value,
            "carbon" to carbon.value,
            "foods" to selectedFood.distinct(),
            "nutritions" to selectedNutrition.distinct(),
            "memo" to memo.value,
            if (uploadFile.value == true) "photo" to photoUri.value.toString() else "photo" to updateFoodie.value!!.photo
        )

        user.get()
            .addOnSuccessListener { result->
                if (userUid != null){
                    Logger.i("FoodieViewModel userUid =$userUid updateFoodie.value.docId =${updateFoodie.value!!.docId}")

                    user.document(userUid).collection("Foodie").document(updateFoodie.value!!.docId!!).update(foodieContent)
                    updatePuzzle()
                }

            }

    }

    fun updatePuzzle() {

        if (userUid!!.isNotEmpty()){
            val diary = user
                .document(userUid).collection("Foodie")
                .orderBy("timestamp", Query.Direction.DESCENDING)

            diary
                .get()
                .addOnSuccessListener {
                    val dates = mutableListOf<String>()
                    for (document in it) {
                        dates.add(sdf.format(java.sql.Date(document.toObject(Foodie::class.java).timestamp!!.time)))
                    }
                    Logger.i("dates.size = ${dates.distinct().size} dates = $dates")
                    if (dates.distinct().size%7 == 0){
                        val puzzle = user
                            .document(userUid).collection("Puzzle")
                            .orderBy("timestamp", Query.Direction.DESCENDING)

                        val pazzleAll = mutableListOf<Puzzle>()

                        puzzle
                            .get()
                            .addOnSuccessListener {
                                for (document in it){
                                        pazzleAll.add(document.toObject(Puzzle::class.java))
                                        pazzleAll[pazzleAll.size-1].docId = document.id
                                }

                                Logger.i("pazzleAll.size = ${pazzleAll.size} pazzle = $pazzleAll")

                                if ( pazzleAll.size != 0 ){
                                    UserManager.getPuzzleOld = UserManager.getPuzzleOld.toString().toInt().plus(1).toString()
                                    if (pazzleAll[0].position!!.sum()!= 105 && UserManager.getPuzzleOld == "1" && !pazzleAll[0].recordedDates!!.contains(sdf.format(date.value))){
                                        val addNewPazzle = pazzleAll[0].position!!.toMutableList()
                                        val addOldPazzleTS = pazzleAll[0].recordedDates!!.toMutableList()
                                        addNewPazzle.add((1..15).minus(addNewPazzle).random())
                                        addOldPazzleTS.add(sdf.format(date.value!!))
                                        user.document(userUid).collection("Puzzle").document(pazzleAll[0].docId!!).update(
                                            mapOf(
                                            "position" to addNewPazzle,
                                            "recordedDates" to addOldPazzleTS,
                                                "timestamp" to FieldValue.serverTimestamp()
                                            )
                                        )

                                    } else if (pazzleAll[0].position!!.sum()== 105 && UserManager.getPuzzleOld == "1" && !pazzleAll[0].recordedDates!!.contains(sdf.format(date.value))){
                                        val pazzleOld = hashMapOf(
                                            "position" to listOf((0..14).random()),
                                            "imgURL" to PuzzleImg.values()[ pazzleAll.size ].value,
                                            "recordedDates" to listOf(sdf.format(date.value)),
                                            "timestamp" to FieldValue.serverTimestamp()

                                        )
                                        user.document(userUid).collection("Puzzle").document().set(pazzleOld)
                                    }
                                }
                                //全新用戶的拼圖在 Diary 去 Update
                                else if ( pazzleAll.size == 0 ){
                                    val pazzleOld = hashMapOf(
                                        "position" to listOf((0..14).random()),
                                        "imgURL" to PuzzleImg.values()[ pazzleAll.size ].value,
                                        "recordedDates" to listOf(sdf.format(date.value)),
                                        "timestamp" to FieldValue.serverTimestamp()

                                    )
//                                    user.document(userUid).collection("Puzzle").document().set(pazzleOld)
                                }
                            }

                    }
                }
        }


    }


    fun clearData(){
        water.value = 0.0f
        oil.value = 0.0f
        vegetable.value = 0.0f
        protein.value = 0.0f
        fruit.value = 0.0f
        carbon.value = 0.0f
    }


    init {
        if (userUid != null){
            getFoodAndNuList()
            updatePuzzle()
        }
        setDate(Date())
        unCheckedAddNewFood()
        unCheckedAddNewNutrition()
        editDateClickedAgain()
        editTimeClickedAgain()
    }

    fun getFoodAndNuList(){

        user.get()
            .addOnSuccessListener { result ->
                for (doc in result){
                    if (doc.id == userUid){
                        val user = doc.toObject(User::class.java)
                        if (user.foodlist != null){
                            var firebaseFoodlist: List<String> = doc["foodlist"] as List<String>
                            getFoodlist(firebaseFoodlist)
                        }
                        if (user.nutritionlist != null){
                            var firebaseNulist: List<String> = doc["nutritionlist"] as List<String>
                            getNulist(firebaseNulist)
                        }
                    }
                }

            }
    }

    fun updateFoodAndNuList(){

        user.get()
            .addOnSuccessListener { result ->
                for (doc in result){
                    if (doc.id == userUid){
                        val user = doc.toObject(User::class.java)
//                        if (user.foodlist == null){
                            db.collection("Users").document(doc.id).update("foodlist", newFuList.distinct()).addOnCompleteListener{}
//                        }
//                        if (user.nutritionlist == null){
                            db.collection("Users").document(doc.id).update("nutritionlist", newNuList.distinct()).addOnCompleteListener {  }
//                        }
                    }
                }

            }
    }


}