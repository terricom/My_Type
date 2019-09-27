package com.terricom.mytype.profile

import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.terricom.mytype.tools.Logger
import com.terricom.mytype.data.Goal
import com.terricom.mytype.data.Puzzle
import com.terricom.mytype.data.UserManager
import java.text.SimpleDateFormat

class ProfileViewModel: ViewModel() {

    val userName = UserManager.name
    val userPic = UserManager.picture
    val userUid = UserManager.uid
    val sdf = SimpleDateFormat("yyyy-MM-dd")

    val outlineProvider = ProfileAvatarOutlineProvider()

    val water =  MutableLiveData<Float>()
    val oil = MutableLiveData<Float>()
    val vegetable = MutableLiveData<Float>()
    val protein = MutableLiveData<Float>()
    val fruit = MutableLiveData<Float>()
    val carbon = MutableLiveData<Float>()
    var weight = MutableLiveData<Float>()
    var bodyFat = MutableLiveData<Float>()
    var muscle = MutableLiveData<Float>()

    val cheerUp = MutableLiveData<String>()

    val date = MutableLiveData<String>()
//    val goal = MutableLiveData<String>()

    val _puzzle = MutableLiveData<List<Puzzle>>()
    val puzzle :LiveData<List<Puzzle>>
        get() = _puzzle

    fun setPazzle(puzzle: List<Puzzle>){
        _puzzle.value = puzzle
        Logger.i("puzzle = $puzzle")
    }

    val _goal = MutableLiveData<List<Goal>>()
    val goal : LiveData<List<Goal>>
        get() = _goal

    fun fireGoalBack (go: List<Goal>){
        _goal.value = go
    }

    fun convertStringToFloat(string: String): Float {
        return try {
            string.toFloat()
        } catch (nfe: NumberFormatException) {
            0.0f
        }
    }

    val _goalExpandClicked = MutableLiveData<Boolean>()
    val goalExpandClicked : LiveData<Boolean>
        get() = _goalExpandClicked

    fun goalExpand(){
        _goalExpandClicked.value = true
    }

    fun goalCLose(){
        _goalExpandClicked.value = false
    }

    val _getGoalOrNot = MutableLiveData<Boolean>()
    val getGoalOrNot: LiveData<Boolean>
     get() = _getGoalOrNot

    fun getGoalNOt(){
        _getGoalOrNot.value = false
    }

    fun getGoal(){
        _getGoalOrNot.value = true
    }

    val _getPazzleOrNot = MutableLiveData<Boolean>()
    val getPazzleOrNot: LiveData<Boolean>
        get() = _getPazzleOrNot

    fun getPazzleNot(){
        _getPazzleOrNot.value = false
    }

    fun getPazzleSuccess(){
        _getPazzleOrNot.value = true
    }

    private val _snapPosition = MutableLiveData<Int>()

    val snapPosition: LiveData<Int>
        get() = _snapPosition

    fun onGalleryScrollChange(layoutManager: RecyclerView.LayoutManager?, linearSnapHelper: LinearSnapHelper) {
        val snapView = linearSnapHelper.findSnapView(layoutManager)
        snapView?.let {
            layoutManager?.getPosition(snapView)?.let {
                if (it != snapPosition.value) {
                    _snapPosition.value = it
                }
            }
        }
    }

    val db = FirebaseFirestore.getInstance()
    val users = db.collection("Users")

    init {
        getThisMonth()
        getPazzle()
        goalCLose()
    }

    @InverseMethod("convertStringToFloat")
    fun floatToString(value:Float) = value.toString()

    fun getPazzle(){
        if (userUid!!.isNotEmpty()){
            val goal = users
                .document(userUid)
                .collection("Puzzle")
                .orderBy("timestamp", Query.Direction.DESCENDING)

            goal
                .get()
                .addOnSuccessListener {
                    val items = mutableListOf<Puzzle>()
                    if (it.isEmpty){
                        getPazzleNot()
                    }else {
                        for (document in it) {
                            items.add(document.toObject(Puzzle::class.java))
                            items[items.size-1].docId = document.id
                        }
                        setPazzle(items)
                        getPazzleSuccess()
                    }
                }
        }
    }


    fun getThisMonth() {
        Logger.i("userUID = $userUid")
        val db = FirebaseFirestore.getInstance()
        val users = db.collection("Users")

        if (userUid!!.isNotEmpty()){
            val goal = users
                .document(userUid)
                .collection("Goal")
                .orderBy("timestamp", Query.Direction.DESCENDING)

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
                        cheerUp.value = items[0].cheerUp
                        if (items.size > 0){
                            fireGoalBack(items)
                            getGoal()
                        }else if (it.isEmpty){
                            getGoalNOt()
                        }
                    }
                }
        }
    }



}