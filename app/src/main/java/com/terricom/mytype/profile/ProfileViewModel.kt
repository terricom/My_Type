package com.terricom.mytype.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.data.FirebaseKey
import com.terricom.mytype.data.Goal
import com.terricom.mytype.data.Puzzle
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.data.source.MyTypeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Timestamp

class ProfileViewModel(private val myTypeRepository: MyTypeRepository): ViewModel() {

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

    init {
        closeGoal()
    }

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun getAndSetPuzzleFromFirebase(){

        coroutineScope.launch {

            val puzzleList = myTypeRepository.getObjects(FirebaseKey.COLLECTION_PUZZLE, Timestamp(946656000), Timestamp(4701859200))
            if (puzzleList.isNullOrEmpty()){
                getNoPuzzle()
            }else {
                setPuzzle(puzzleList as List<Puzzle>)
                getPuzzle()
                _status.value = true
            }
        }
    }

    fun getAndSetGoalFromFirebase() {

        coroutineScope.launch {

            val goalList = myTypeRepository.getObjects(FirebaseKey.COLLECTION_GOAL, Timestamp(946656000), Timestamp(4701859200))
            if (goalList.isNullOrEmpty()){
                cheerUp.value = App.applicationContext().getString(R.string.login_greet)
                getNoGoal()
            }else {
                cheerUp.value = (goalList[0] as Goal).cheerUp
                setGoal(goalList as List<Goal>)
                getGoal()
                _status.value = true
            }
        }
    }
}