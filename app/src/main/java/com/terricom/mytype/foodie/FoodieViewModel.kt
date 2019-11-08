package com.terricom.mytype.foodie

import android.net.Uri
import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.data.FirebaseKey.Companion.COLLECTION_FOODIE
import com.terricom.mytype.data.FirebaseKey.Companion.COLLECTION_PUZZLE
import com.terricom.mytype.data.FirebaseKey.Companion.COLUMN_FOODIE_CARBON
import com.terricom.mytype.data.FirebaseKey.Companion.COLUMN_FOODIE_FOODS
import com.terricom.mytype.data.FirebaseKey.Companion.COLUMN_FOODIE_FRUIT
import com.terricom.mytype.data.FirebaseKey.Companion.COLUMN_FOODIE_MEMO
import com.terricom.mytype.data.FirebaseKey.Companion.COLUMN_FOODIE_NUTRITIONS
import com.terricom.mytype.data.FirebaseKey.Companion.COLUMN_FOODIE_OIL
import com.terricom.mytype.data.FirebaseKey.Companion.COLUMN_FOODIE_PHOTO
import com.terricom.mytype.data.FirebaseKey.Companion.COLUMN_FOODIE_PROTEIN
import com.terricom.mytype.data.FirebaseKey.Companion.COLUMN_FOODIE_VEGETABLE
import com.terricom.mytype.data.FirebaseKey.Companion.COLUMN_FOODIE_WATER
import com.terricom.mytype.data.FirebaseKey.Companion.COLUMN_PUZZLE_IMGURL
import com.terricom.mytype.data.FirebaseKey.Companion.COLUMN_PUZZLE_POSITION
import com.terricom.mytype.data.FirebaseKey.Companion.COLUMN_PUZZLE_RECORDEDDATES
import com.terricom.mytype.data.FirebaseKey.Companion.COLUMN_USER_FOOD_LIST
import com.terricom.mytype.data.FirebaseKey.Companion.COLUMN_USER_NUTRITION_LIST
import com.terricom.mytype.data.FirebaseKey.Companion.TIMESTAMP
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.Puzzle
import com.terricom.mytype.data.PuzzleImg
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.data.source.MyTypeRepository
import com.terricom.mytype.tools.FORMAT_HH_MM_SS_FFFFFFFFF
import com.terricom.mytype.tools.FORMAT_YYYY_MM_DD
import com.terricom.mytype.tools.toDateFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Time
import java.sql.Timestamp
import java.util.*


class FoodieViewModel(private val myTypeRepository: MyTypeRepository): ViewModel() {

    private var selectedFood = mutableListOf<String>()
    private var selectedNutrition = mutableListOf<String>()

    private val _userFoodList = MutableLiveData<List<String>>()
    val userFoodList : LiveData<List<String>>
        get() = _userFoodList

    private val _userNutritionList = MutableLiveData<List<String>>()
    val userNutritionList : LiveData<List<String>>
        get() = _userNutritionList

    private val _selectedFoodList = MutableLiveData<List<String>>()
    val selectedFoodList: LiveData<List<String>>
        get() = _selectedFoodList

    private fun addSelectedFoodList(list: List<String>){
        _selectedFoodList.value = list
    }

    private val _selectedNutritionList = MutableLiveData<List<String>>()
    val selectedNutritionList: LiveData<List<String>>
        get() = _selectedNutritionList

    private fun addSelectedNutritionList(list: List<String>){
        _selectedNutritionList.value = list
    }

    private val toUpdateUserFoodList2Firebase = mutableListOf<String>()
    private val toUpdateUserNutritionList2Firebase = mutableListOf<String>()

    //Get history record of food list from Firebase
    private fun getHistoryUserFoodList(foodList: List<String>){

        val newFooList = foodList.toMutableList()
        newFooList.add(App.applicationContext().getString(R.string.foodie_add_food))
        _userFoodList.value = newFooList
        for (food in foodList){
            toUpdateUserFoodList2Firebase.add(food)
        }
    }

    //Get history record of nutrition list from Firebase
    fun getHistoryUserNutritionList(nutritionList: List<String>){

        val newNutritionList = nutritionList.toMutableList()
        newNutritionList.add(App.applicationContext().getString(R.string.diary_add_nutrition))
        _userNutritionList.value = newNutritionList
        for (nutrition in nutritionList){
            toUpdateUserNutritionList2Firebase.add(nutrition)
        }
    }

    fun addToFoodList(food: String) {

        selectedFood.add(food)
        addSelectedFoodList(selectedFood.distinct())
        toUpdateUserFoodList2Firebase.add(food)
        if (toUpdateUserFoodList2Firebase.contains(App.applicationContext().getString(R.string.foodie_add_food))){
            toUpdateUserFoodList2Firebase.remove(App.applicationContext().getString(R.string.foodie_add_food))
        }
    }

    //Remove selected food item
    fun dropOutFoodList(food: String) {

        selectedFood.remove(food)
        addSelectedFoodList(selectedFood)
    }

    //Add nutrition item to Foodie
    fun addToNutritionList(nutrition: String) {

        selectedNutrition.add(nutrition)
        addSelectedNutritionList(selectedNutrition.distinct())
        toUpdateUserNutritionList2Firebase.add(nutrition)
        if (toUpdateUserNutritionList2Firebase.contains(App.applicationContext().getString(R.string.diary_add_nutrition))){
            toUpdateUserNutritionList2Firebase.remove(App.applicationContext().getString(R.string.diary_add_nutrition))
        }
    }

    //Remove selected nutrition item
    fun dropOutNutritionList (nutrition: String) {
        selectedNutrition.remove(nutrition)
        addSelectedNutritionList(selectedNutrition)
    }

    val water =  MutableLiveData<String>()
    val oil = MutableLiveData<String>()
    val vegetable = MutableLiveData<String>()
    val protein = MutableLiveData<String>()
    val fruit = MutableLiveData<String>()
    val carbon = MutableLiveData<String>()
    val memo = MutableLiveData<String>()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun convertStringToFloat(string: String): Float {
        return try {
            string.toFloat()
        } catch (nfe: NumberFormatException) {
            0f
        }
    }

    @InverseMethod("convertStringToFloat")
    fun floatToString(value:Float) = value.toString()


    private val _date = MutableLiveData<Date>()
    val date: LiveData<Date>
        get() = _date

    fun setCurrentDate(date: Date){
        _date.value = date
        _time.value = Time(date.time)
    }

    //Show or Hide the Date Picker
    private val _isEditDateClicked = MutableLiveData<Boolean>()
    val isEditDateClicked : LiveData<Boolean>
        get() = _isEditDateClicked

    fun editDateClicked(){
        _isEditDateClicked.value = true
    }

    fun editDateClickedAgain(){
        _isEditDateClicked.value = false
    }

    //Show or Hide the Time Picker
    private val _isEditTimeClicked = MutableLiveData<Boolean>()
    val isEditTimeClicked : LiveData<Boolean>
        get() = _isEditTimeClicked

    fun editTimeClicked(){
        _isEditTimeClicked.value = true
    }

    fun editTimeClickedAgain(){
        _isEditTimeClicked.value = false
    }

    private val _time = MutableLiveData<Time>()
    val time : LiveData<Time>
        get() = _time

    //Get the download URL form Firebase Storage
    private val _photoUri = MutableLiveData<Uri>()
    private val photoUri: LiveData<Uri>
        get() = _photoUri

    fun setPhoto(photo: Uri){
        _photoUri.value = photo
    }

    //Modify the history Foodie record from other fragment
    private val _getHistoryFoodie = MutableLiveData<Foodie>()
    private val getHistoryFoodie : LiveData<Foodie>
        get() = _getHistoryFoodie

    fun getHistoryFoodie(foodie: Foodie){
        _getHistoryFoodie.value = foodie
    }

    //Modify the history Foodie record from other fragment and upload new photo
    private val _isUploadPhoto = MutableLiveData<Boolean>()
    private val isUploadPhoto: LiveData<Boolean>
        get() = _isUploadPhoto

    fun uploadPhoto(){
        _isUploadPhoto.value = true
    }

    fun addNewFoodie(docId: String){

        if (selectedFood.contains(App.applicationContext().getString(R.string.foodie_add_food))) {
            selectedFood.remove(App.applicationContext().getString(R.string.foodie_add_food))}
        if (selectedNutrition.contains(App.applicationContext().getString(R.string.diary_add_nutrition))) {
            selectedNutrition.remove(App.applicationContext().getString(R.string.diary_add_nutrition))}

        val foodieContent = hashMapOf(

            TIMESTAMP to Timestamp.valueOf(
                "${date.value.toDateFormat(FORMAT_YYYY_MM_DD)} ${time.value.toDateFormat(
                    FORMAT_HH_MM_SS_FFFFFFFFF
                )}"
            ),
            COLUMN_FOODIE_WATER to (water.value ?: "0.0").toFloat(),
            COLUMN_FOODIE_OIL to (oil.value ?: "0.0").toFloat(),
            COLUMN_FOODIE_VEGETABLE to (vegetable.value ?: "0.0").toFloat(),
            COLUMN_FOODIE_PROTEIN to (protein.value ?: "0.0").toFloat(),
            COLUMN_FOODIE_FRUIT to (fruit.value ?: "0.0").toFloat(),
            COLUMN_FOODIE_CARBON to (carbon.value ?: "0.0").toFloat(),
            if (isUploadPhoto.value == true) {
                COLUMN_FOODIE_PHOTO to photoUri.value.toString()
            } else {
                COLUMN_FOODIE_PHOTO to getHistoryFoodie.value!!.photo
            },
            COLUMN_FOODIE_FOODS to selectedFood.distinct(),
            COLUMN_FOODIE_NUTRITIONS to selectedNutrition.distinct(),
            COLUMN_FOODIE_MEMO to memo.value
        )

        coroutineScope.launch {

            myTypeRepository.setOrUpdateObjects(COLLECTION_FOODIE, foodieContent, docId)
            updatePuzzle()
        }
    }

    private fun updatePuzzle() {

        coroutineScope.launch {

            val foodieAll = myTypeRepository.getObjects(COLLECTION_FOODIE, Timestamp(946656000), Timestamp(4701859200))
            val dates = mutableListOf<String>()
            for (foodie in foodieAll as List<Foodie>){
                dates.add(foodie.timestamp.toDateFormat(FORMAT_YYYY_MM_DD))
            }
            if (dates.distinct().size %7 == 0){

                val puzzleAll = myTypeRepository.getObjects(COLLECTION_PUZZLE, Timestamp(946656000), Timestamp(4701859200))

                if(!(puzzleAll[0] as Puzzle).recordedDates!!.contains(date.value.toDateFormat(FORMAT_YYYY_MM_DD))){
                    UserManager.getPuzzleOldUser = UserManager.getPuzzleOldUser.toString().toInt().plus(1).toString()
                    when(puzzleAll.size){
                        0 -> { }
                        else -> {
                            when((puzzleAll[0] as Puzzle).position!!.sum()){
                                105 -> {
                                    val newPuzzle = hashMapOf(
                                        COLUMN_PUZZLE_POSITION to listOf((0..14).random()),
                                        COLUMN_PUZZLE_IMGURL to PuzzleImg.values()[ puzzleAll.size ].value,
                                        COLUMN_PUZZLE_RECORDEDDATES to listOf(date.value.toDateFormat(
                                            FORMAT_YYYY_MM_DD
                                        )),
                                        TIMESTAMP to FieldValue.serverTimestamp()
                                    )
                                    myTypeRepository.setOrUpdateObjects(COLLECTION_PUZZLE, newPuzzle, "")
                                }
                                else -> {
                                    val positionList = (puzzleAll[0] as Puzzle).position!!.toMutableList()
                                    val recordedDatesList = (puzzleAll[0] as Puzzle).recordedDates!!.toMutableList()

                                    positionList.add((1..15).minus(positionList).random())
                                    recordedDatesList.add(date.value.toDateFormat(FORMAT_YYYY_MM_DD))

                                    myTypeRepository.setOrUpdateObjects(COLLECTION_PUZZLE, mapOf(
                                        COLUMN_PUZZLE_POSITION to positionList,
                                        COLUMN_PUZZLE_RECORDEDDATES to recordedDatesList,
                                        TIMESTAMP to FieldValue.serverTimestamp()
                                    ), (puzzleAll[0] as Puzzle).docId)
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    fun clearData(){
        water.value = ""
        oil.value = ""
        vegetable.value = ""
        protein.value = ""
        fruit.value = ""
        carbon.value = ""
    }


    init {
        if (UserManager.isLogin()){
            getFoodAndNutritionList()
        }
        setCurrentDate(Date())
        editDateClicked()
        editTimeClicked()
    }

    private fun getFoodAndNutritionList(){

        coroutineScope.launch {

            getHistoryUserFoodList(myTypeRepository.getObjects(
                COLUMN_USER_FOOD_LIST,
                Timestamp(946656000), Timestamp(4701859200)) as List<String>)
            getHistoryUserNutritionList(myTypeRepository.getObjects(
                COLUMN_USER_NUTRITION_LIST,
                Timestamp(946656000), Timestamp(4701859200)) as List<String>)
        }
    }

    fun updateFoodAndNuList(){

        coroutineScope.launch {

            myTypeRepository.setOrUpdateObjects(COLUMN_USER_FOOD_LIST, toUpdateUserFoodList2Firebase.distinct(),"")
            myTypeRepository.setOrUpdateObjects(COLUMN_USER_NUTRITION_LIST, toUpdateUserNutritionList2Firebase.distinct(),"")
        }
    }
}