package com.terricom.mytype.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.data.*
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

            when (val puzzleResult = myTypeRepository.getObjects<Puzzle>(FirebaseKey.COLLECTION_PUZZLE, Timestamp(946656000), Timestamp(4701859200))) {
                is Result.Success -> {
                    setPuzzle(puzzleResult.data)
                    getPuzzle()
                    _status.value = true
                }
                else -> getNoPuzzle()
            }
        }
    }

    fun getAndSetGoalFromFirebase() {

        coroutineScope.launch {

            val goalResult = myTypeRepository.getObjects<Goal>(FirebaseKey.COLLECTION_GOAL, Timestamp(946656000), Timestamp(4701859200))

            when (goalResult) {
                is Result.Success -> {
                    cheerUp.value = goalResult.data[0].cheerUp
                    setGoal(goalResult.data)
                    getGoal()
                    _status.value = true
                }
                else -> {
                    cheerUp.value = App.applicationContext().getString(R.string.login_greet)
                    getNoGoal()
                }
            }
        }
    }
}