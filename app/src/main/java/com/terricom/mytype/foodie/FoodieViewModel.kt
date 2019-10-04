package com.terricom.mytype.foodie

import android.net.Uri
import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.terricom.mytype.*
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
import java.sql.Time
import java.sql.Timestamp
import java.util.*


class FoodieViewModel: ViewModel() {

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

    //從 Firebase 取得 User 的歷史食物清單
    private fun getHistoryUserFoodList(foodList: List<String>){

        val newFooList = foodList.toMutableList()
        newFooList.add(App.applicationContext().getString(R.string.foodie_add_food))
        _userFoodList.value = newFooList
        for (food in foodList){
            toUpdateUserFoodList2Firebase.add(food)
        }
    }

    //從 Firebase 取得 User 的歷營養素清單
    fun getHistoryUserNutritionList(nutritionList: List<String>){

        val newNutritionList = nutritionList.toMutableList()
        newNutritionList.add(App.applicationContext().getString(R.string.diary_add_nutrition))
        _userNutritionList.value = newNutritionList
        for (nutrition in nutritionList){
            toUpdateUserNutritionList2Firebase.add(nutrition)
        }
    }

    //新增食物項目到食記
    fun addToFoodList(food: String) {

        selectedFood.add(food)
        addSelectedFoodList(selectedFood.distinct())
        toUpdateUserFoodList2Firebase.add(food)
        if (toUpdateUserFoodList2Firebase.contains(App.applicationContext().getString(R.string.foodie_add_food))){
            toUpdateUserFoodList2Firebase.remove(App.applicationContext().getString(R.string.foodie_add_food))
        }
    }

    //移除當前食記已新增的食物項目
    fun dropOutFoodList(food: String) {

        selectedFood.remove(food)
        addSelectedFoodList(selectedFood)
    }

    //新增營養品項目到食記
    fun addToNutritionList(nutrition: String) {

        selectedNutrition.add(nutrition)
        addSelectedNutritionList(selectedNutrition.distinct())
        toUpdateUserNutritionList2Firebase.add(nutrition)
        if (toUpdateUserNutritionList2Firebase.contains(App.applicationContext().getString(R.string.diary_add_nutrition))){
            toUpdateUserNutritionList2Firebase.remove(App.applicationContext().getString(R.string.diary_add_nutrition))
        }
    }

    //移除當前食記已新增的營養品項目
    fun dropOutNutritionList (nutrition: String) {
        selectedNutrition.remove(nutrition)
        addSelectedNutritionList(selectedNutrition)
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


    private val _date = MutableLiveData<Date>()
    val date: LiveData<Date>
        get() = _date

    fun setCurrentDate(date: Date){
        _date.value = date
        _time.value = Time(date.time)
    }

    //顯示或隱藏 Date Picker
    private val _isEditDateClicked = MutableLiveData<Boolean>()
    val isEditDateClicked : LiveData<Boolean>
        get() = _isEditDateClicked

    fun editDateClicked(){
        _isEditDateClicked.value = true
    }

    fun editDateClickedAgain(){
        _isEditDateClicked.value = false
    }

    //顯示或隱藏 Time Picker
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

    //取得 Firebase Store 的 download URL
    private val _photoUri = MutableLiveData<Uri>()
    private val photoUri: LiveData<Uri>
        get() = _photoUri

    fun setPhoto(photo: Uri){
        _photoUri.value = photo
    }

    //從其他 Fragment 帶來的歷史紀錄
    private val _getHistoryFoodie = MutableLiveData<Foodie>()
    private val getHistoryFoodie : LiveData<Foodie>
        get() = _getHistoryFoodie

    fun getHistoryFoodie(foodie: Foodie){
        _getHistoryFoodie.value = foodie
    }

    //從其他 Fragment 帶來的歷史紀錄並且有新增照片
    private val _isUploadPhoto = MutableLiveData<Boolean>()
    private val isUploadPhoto: LiveData<Boolean>
        get() = _isUploadPhoto

    fun uploadPhoto(){
        _isUploadPhoto.value = true
    }

    fun addNewFoodie(){

        if (selectedFood.contains(App.applicationContext().getString(R.string.foodie_add_food))) {
            selectedFood.remove(App.applicationContext().getString(R.string.foodie_add_food))}
        if (selectedNutrition.contains(App.applicationContext().getString(R.string.diary_add_nutrition))) {
            selectedNutrition.remove(App.applicationContext().getString(R.string.diary_add_nutrition))}

        val foodieContent = hashMapOf(

            TIMESTAMP to Timestamp.valueOf(
                "${date.value.toDateFormat(FORMAT_YYYY_MM_DD)} ${time.value.toDateFormat(
                FORMAT_HH_MM_SS_FFFFFFFFF)}"
            ),
            COLUMN_FOODIE_WATER to water.value,
            COLUMN_FOODIE_OIL to oil.value,
            COLUMN_FOODIE_VEGETABLE to vegetable.value,
            COLUMN_FOODIE_PROTEIN to protein.value,
            COLUMN_FOODIE_FRUIT to fruit.value,
            COLUMN_FOODIE_CARBON to carbon.value,
            COLUMN_FOODIE_PHOTO to photoUri.value.toString(),
            COLUMN_FOODIE_FOODS to selectedFood.distinct(),
            COLUMN_FOODIE_NUTRITIONS to selectedNutrition.distinct(),
            COLUMN_FOODIE_MEMO to memo.value
        )

        if (UserManager.isLogin()){

            UserManager.USER_REFERENCE?.let {user ->

                user.collection(COLLECTION_FOODIE).document().set(foodieContent)
                updatePuzzle()
            }

        }
    }

    fun adjustOldFoodie(){

        if (selectedFood.contains(App.applicationContext().getString(R.string.foodie_add_food))) {

            selectedFood.remove(App.applicationContext().getString(R.string.foodie_add_food))}

        if (selectedNutrition.contains(App.applicationContext().getString(R.string.diary_add_nutrition))) {

            selectedNutrition.remove(App.applicationContext().getString(R.string.diary_add_nutrition))}

        val foodieContent = hashMapOf(

            TIMESTAMP to Timestamp.valueOf("${date.value.toDateFormat(FORMAT_YYYY_MM_DD)} ${time.value.toDateFormat(
                FORMAT_HH_MM_SS_FFFFFFFFF)}"),
            COLUMN_FOODIE_WATER to water.value,
            COLUMN_FOODIE_OIL to oil.value,
            COLUMN_FOODIE_VEGETABLE to vegetable.value,
            COLUMN_FOODIE_PROTEIN to protein.value,
            COLUMN_FOODIE_FRUIT to fruit.value,
            COLUMN_FOODIE_CARBON to carbon.value,
            COLUMN_FOODIE_FOODS to selectedFood.distinct(),
            COLUMN_FOODIE_NUTRITIONS to selectedNutrition.distinct(),
            COLUMN_FOODIE_MEMO to memo.value,
            if (isUploadPhoto.value == true) {
                COLUMN_FOODIE_PHOTO to photoUri.value.toString()
            } else {
                COLUMN_FOODIE_PHOTO to getHistoryFoodie.value!!.photo
            }
        )

        if (UserManager.isLogin()){

            UserManager.USER_REFERENCE?.let {user ->

                user.collection(COLLECTION_FOODIE).document(getHistoryFoodie.value!!.docId).update(foodieContent)
                updatePuzzle()
            }

        }

    }

    private fun updatePuzzle() {

        if (UserManager.isLogin()){

            UserManager.USER_REFERENCE?.let { user ->

                user.collection(COLLECTION_FOODIE)
                    .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener {

                        val dates = mutableListOf<String>()
                        for (document in it) {

                            dates.add(java.sql.Date(document.toObject(Foodie::class.java).timestamp!!.time)
                                .toDateFormat(
                                FORMAT_YYYY_MM_DD)
                            )
                        }

                        //紀錄 7 天的食記會獲得一塊拼圖
                        if (dates.distinct().size % 7 == 0){

                            user.collection(COLLECTION_PUZZLE)
                                .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                                .get()
                                .addOnSuccessListener {

                                    val puzzleList = mutableListOf<Puzzle>()
                                    for (document in it){

                                        puzzleList.add(document.toObject(Puzzle::class.java))
                                        puzzleList[puzzleList.lastIndex].docId = document.id
                                    }

                                    //處理老用戶的拼圖發放( 拼圖數量不為 0 )
                                    if (puzzleList.size != 0){

                                        //為了在日記頁面顯示一次性通知，當計算次數為一次時，則顯示通知
                                        UserManager.getPuzzleOldUser = UserManager.getPuzzleOldUser.toString().toInt().plus(1).toString()

                                        if (
                                            puzzleList[0].position!!.sum()!= 105 //拼到一半的拼圖
                                            && !puzzleList[0].recordedDates!!.contains(date.value.toDateFormat(
                                                FORMAT_YYYY_MM_DD)) //已經發放的日期不能重複發放
                                        ){
                                            val positionList = puzzleList[0].position!!.toMutableList()
                                            val recordedDatesList = puzzleList[0].recordedDates!!.toMutableList()

                                            positionList.add((1..15).minus(positionList).random()) //隨機新增一塊拼圖
                                            recordedDatesList.add(date.value.toDateFormat(
                                                FORMAT_YYYY_MM_DD)) //加上新增的日期，以免同一天重複發放

                                            user.collection(COLLECTION_PUZZLE).document(puzzleList[0].docId).update(

                                                mapOf(
                                                    COLUMN_PUZZLE_POSITION to positionList,
                                                    COLUMN_PUZZLE_RECORDEDDATES to recordedDatesList,
                                                    TIMESTAMP to FieldValue.serverTimestamp()
                                                )
                                            )

                                        } else if (
                                            puzzleList[0].position!!.sum()== 105 //只有拼完的拼圖
                                            && !puzzleList[0].recordedDates!!.contains(date.value.toDateFormat(
                                                FORMAT_YYYY_MM_DD)) //已經發放的日期不能重複發放
                                        ){
                                            val newPuzzle = hashMapOf(
                                                COLUMN_PUZZLE_POSITION to listOf((0..14).random()),
                                                COLUMN_PUZZLE_IMGURL to PuzzleImg.values()[ puzzleList.size ].value,
                                                COLUMN_PUZZLE_RECORDEDDATES to listOf(date.value.toDateFormat(
                                                    FORMAT_YYYY_MM_DD)),
                                                TIMESTAMP to FieldValue.serverTimestamp()

                                            )
                                            user.collection(COLLECTION_PUZZLE).document().set(newPuzzle)
                                        }
                                    }
                                    //全新用戶的拼圖在 Diary 去 Update
                                    else if ( puzzleList.size == 0 ){
                                    }
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
        if (UserManager.isLogin()){
            getFoodAndNutritionList()
            updatePuzzle()
        }
        setCurrentDate(Date())
        editDateClickedAgain()
        editTimeClickedAgain()
    }

    private fun getFoodAndNutritionList(){

        if (UserManager.isLogin()){

            UserManager.USER_REFERENCE?.let {user ->

                user.get()
                    .addOnSuccessListener {document ->

                        getHistoryUserFoodList(document[COLUMN_USER_FOOD_LIST] as List<String>)
                        getHistoryUserNutritionList(document[COLUMN_USER_NUTRITION_LIST] as List<String>)

                    }
            }
        }
    }

    fun updateFoodAndNuList(){

        if (UserManager.isLogin()){

            UserManager.USER_REFERENCE?.let {user ->

                user.update(COLUMN_USER_FOOD_LIST, toUpdateUserFoodList2Firebase.distinct())
                user.update(COLUMN_USER_NUTRITION_LIST, toUpdateUserNutritionList2Firebase.distinct())

            }
        }
    }


}