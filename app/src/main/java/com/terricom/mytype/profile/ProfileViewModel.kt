package com.terricom.mytype.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.data.*

class ProfileViewModel(private val firebaseRepository: FirebaseRepository): ViewModel() {

    val userName = UserManager.name
    val userPic = UserManager.picture

    private val _status = MutableLiveData<Boolean>()
    val status: LiveData<Boolean>
        get() = _status

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

    private val _puzzle = MutableLiveData<List<Puzzle>>()
    val puzzle :LiveData<List<Puzzle>>
        get() = _puzzle

    private fun setPuzzle(puzzle: List<Puzzle>){
        _puzzle.value = puzzle
    }

    private val _goal = MutableLiveData<List<Goal>>()
    val goal : LiveData<List<Goal>>
        get() = _goal

    private fun setGoal (goal: List<Goal>){
        _goal.value = goal
    }

    private val _isGoalExpanded = MutableLiveData<Boolean>()
    val isGoalExpanded : LiveData<Boolean>
        get() = _isGoalExpanded

    fun expandGoal(){
        _isGoalExpanded.value = true
    }

    fun closeGoal(){
        _isGoalExpanded.value = false
    }

    private val _isGoalGot = MutableLiveData<Boolean>()
    val isGoalGot: LiveData<Boolean>
     get() = _isGoalGot

    private fun getNoGoal(){
        _isGoalGot.value = false
    }

    private fun getGoal(){
        _isGoalGot.value = true
    }

    private val _isPuzzleGot = MutableLiveData<Boolean>()
    val isPuzzleGot: LiveData<Boolean>
        get() = _isPuzzleGot

    private fun getNoPuzzle(){
        _isPuzzleGot.value = false
    }

    private fun getPuzzle(){
        _isPuzzleGot.value = true
    }

    private val _snapPosition = MutableLiveData<Int>()
    private val snapPosition: LiveData<Int>
        get() = _snapPosition

    fun onGalleryScrollChange(
        layoutManager: RecyclerView.LayoutManager?, linearSnapHelper: LinearSnapHelper
    ) {
        val snapView = linearSnapHelper.findSnapView(layoutManager)
        snapView?.let {
            layoutManager?.getPosition(snapView)?.let {
                if (it != snapPosition.value) {
                    _snapPosition.value = it
                }
            }
        }
    }

    init {
        getAndSetGoalFromFirebase()
        getAndSetPuzzleFromFirebase()
        closeGoal()
    }

    private fun getAndSetPuzzleFromFirebase(){

        if (UserManager.isLogin()){

            UserManager.USER_REFERENCE?.let {userDocument ->

                userDocument.collection(FirebaseKey.COLLECTION_PUZZLE)
                    .orderBy(FirebaseKey.TIMESTAMP, Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener {
                        val items = mutableListOf<Puzzle>()

                        for (document in it) {
                            items.add(document.toObject(Puzzle::class.java))
                            items[items.size-1].docId = document.id
                        }
                        if (items.isNullOrEmpty()){

                            getNoPuzzle()

                        }else {

                            setPuzzle(items)
                            getPuzzle()
                            _status.value = true

                        }
                    }
            }
        }
    }


    private fun getAndSetGoalFromFirebase() {

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

                        if (items.isNullOrEmpty()){
                            cheerUp.value = App.applicationContext().getString(R.string.login_greet)
                            getNoGoal()
                        }else {

                            cheerUp.value = items[0].cheerUp
                            setGoal(items)
                            getGoal()
                            _status.value = true
                        }
                    }
            }
        }
    }

}