package com.terricom.mytype.data.source.remote

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FieldValue
import com.terricom.mytype.data.*
import com.terricom.mytype.data.FirebaseKey.Companion.COLUMN_USER_FOOD_LIST
import com.terricom.mytype.data.FirebaseKey.Companion.COLUMN_USER_NUTRITION_LIST
import com.terricom.mytype.data.source.MyTypeDataSource
import com.terricom.mytype.tools.FORMAT_YYYY_MM_DD
import com.terricom.mytype.tools.Logger
import com.terricom.mytype.tools.toDateFormat
import kotlinx.coroutines.tasks.await
import java.sql.Timestamp
import java.util.*

object MyTypeRemoteDataSource: MyTypeDataSource {

    override suspend fun isGoalInLocal(id: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun insertGoal(goal: Goal) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateGoal(goal: Goal) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun clearGoal() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getGoal(): LiveData<List<Goal>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun queryFoodie(key: String, type: String): List<Foodie> {

        val foodieListFromFirebase = mutableListOf<Foodie>()

        UserManager.USER_REFERENCE?.let {

            for (foodie in it.collection(FirebaseKey.COLLECTION_FOODIE)
                .whereArrayContains(type, key)
                .get().await()){

                foodieListFromFirebase.add(foodie.toObject(Foodie::class.java))
                foodieListFromFirebase[foodieListFromFirebase.lastIndex].docId = foodie.id
            }

        }
        return foodieListFromFirebase
    }

    override suspend fun updatePuzzle(): Int{

        val listFromFirebase = mutableListOf<Any>()
        val dates = mutableListOf<String>()

        UserManager.USER_REFERENCE?.let {

            for (foodie in it.collection(FirebaseKey.COLLECTION_FOODIE)
                .orderBy(FirebaseKey.TIMESTAMP)
                .get().await()){

                dates.add(java.sql.Date(foodie.toObject(Foodie::class.java).timestamp!!.time).toDateFormat(
                    FORMAT_YYYY_MM_DD
                ))
                listFromFirebase.add(foodie.toObject(Foodie::class.java))
                (listFromFirebase[listFromFirebase.lastIndex] as Foodie).docId =
                    foodie.id
            }

            Logger.i("dates.distinct() = ${dates.distinct().size}")
            if (dates.distinct().size % 7 == 0){

                val puzzleAll = mutableListOf<Puzzle>()
                //全新使用者
                for (puzzle in it.collection(FirebaseKey.COLLECTION_PUZZLE)
                    .orderBy(FirebaseKey.TIMESTAMP)
                    .get().await()){

                    puzzleAll.add(puzzle.toObject(Puzzle::class.java))
                    puzzleAll[puzzleAll.lastIndex].docId = puzzle.id
                }
                when (dates.size){

                    0 -> {
                        if (UserManager.getPuzzleNewUser == "0"  && puzzleAll.size == 0){

                            UserManager.getPuzzleNewUser = UserManager.getPuzzleNewUser.toString().toInt().plus(1).toString()

                            it.collection(FirebaseKey.COLLECTION_PUZZLE).document().set(
                                hashMapOf(
                                    FirebaseKey.COLUMN_PUZZLE_POSITION to listOf((0..14).random()),
                                    FirebaseKey.COLUMN_PUZZLE_IMGURL to PuzzleImg.values()[0].value,
                                    FirebaseKey.COLUMN_PUZZLE_RECORDEDDATES to listOf(
                                        Date().toDateFormat(
                                            FORMAT_YYYY_MM_DD
                                        )),
                                    FirebaseKey.TIMESTAMP to FieldValue.serverTimestamp()
                                )
                            )

                            return 0 //New user get a piece of puzzle
                        } else if (UserManager.getPuzzleNewUser == "1"  && puzzleAll.size == 1){

                            UserManager.getPuzzleNewUser = UserManager.getPuzzleNewUser.toString().toInt().plus(1).toString()
                            return 0 //New user get a piece of puzzle
                        }
                    }

                    else -> {
                        return 1 //Old user get a piece of puzzle
                    }
                }

            } else {
                return 2 //No user get a piece of puzzle
            }

            return 2 //No user get a piece of puzzle
        }
        return 2 //No user get a piece of puzzle
    }

    override suspend fun deleteObjects(collection: String, any: Any) {

        when(collection){
            FirebaseKey.COLLECTION_GOAL -> {

                UserManager.USER_REFERENCE?.let {

                    it.collection(collection).document((any as Goal).docId).delete()
                }
            }

            FirebaseKey.COLLECTION_FOODIE -> {

                UserManager.USER_REFERENCE?.let {

                    it.collection(collection).document((any as Foodie).docId).delete()
                }
            }

            FirebaseKey.COLLECTION_SHAPE -> {

                UserManager.USER_REFERENCE?.let {

                    it.collection(collection).document((any as Shape).docId).delete()

                }
            }

            FirebaseKey.COLLECTION_SLEEP -> {

                UserManager.USER_REFERENCE?.let {

                    it.collection(collection).document((any as Sleep).docId).delete()

                }
            }

            FirebaseKey.COLLECTION_PUZZLE -> {

                UserManager.USER_REFERENCE?.let {

                    it.collection(collection).document((any as Puzzle).docId).delete()

                }
            }

        }
    }

    override suspend fun getObjects(
        collection: String,
        start: Timestamp,
        end: Timestamp
    ): List<Any> {

        val listFromFirebase = mutableListOf<Any>()

        when(collection){
            FirebaseKey.COLLECTION_GOAL -> {

                UserManager.USER_REFERENCE?.let {

                    for (goal in it.collection(collection)
                        .orderBy(FirebaseKey.TIMESTAMP)
                        .get().await()){

                        listFromFirebase.add(goal.toObject(Goal::class.java))
                        (listFromFirebase[listFromFirebase.lastIndex] as Goal).docId =
                            goal.id
                    }
                }
            }

            FirebaseKey.COLLECTION_FOODIE -> {

                UserManager.USER_REFERENCE?.let {

                    for (foodie in it.collection(collection)
                        .orderBy(FirebaseKey.TIMESTAMP)
                        .whereGreaterThanOrEqualTo(FirebaseKey.TIMESTAMP, start)
                        .whereLessThanOrEqualTo(FirebaseKey.TIMESTAMP, end)
                        .get().await()){

                        listFromFirebase.add(foodie.toObject(Foodie::class.java))
                        (listFromFirebase[listFromFirebase.lastIndex] as Foodie).docId =
                            foodie.id
                    }
                }
            }

            FirebaseKey.COLLECTION_SHAPE -> {

                UserManager.USER_REFERENCE?.let {

                    for (shape in it.collection(collection)
                        .orderBy(FirebaseKey.TIMESTAMP)
                        .whereGreaterThanOrEqualTo(FirebaseKey.TIMESTAMP, start)
                        .whereLessThanOrEqualTo(FirebaseKey.TIMESTAMP, end)
                        .get().await()){

                        listFromFirebase.add(shape.toObject(Shape::class.java))
                        (listFromFirebase[listFromFirebase.lastIndex] as Shape).docId =
                            shape.id
                    }

                }
            }

            FirebaseKey.COLLECTION_SLEEP -> {

                UserManager.USER_REFERENCE?.let {

                    for (sleep in it.collection(collection)
                        .orderBy(FirebaseKey.TIMESTAMP)
                        .whereGreaterThanOrEqualTo(FirebaseKey.TIMESTAMP, start)
                        .whereLessThanOrEqualTo(FirebaseKey.TIMESTAMP, end)
                        .get().await()){

                        listFromFirebase.add(sleep.toObject(Sleep::class.java))
                        (listFromFirebase[listFromFirebase.lastIndex] as Sleep).docId =
                            sleep.id
                    }

                }
            }

            FirebaseKey.COLLECTION_PUZZLE -> {

                UserManager.USER_REFERENCE?.let {

                    for (puzzle in it.collection(collection)
                        .orderBy(FirebaseKey.TIMESTAMP)
                        .get().await()){

                        listFromFirebase.add(puzzle.toObject(Puzzle::class.java))
                        (listFromFirebase[listFromFirebase.lastIndex] as Puzzle).docId =
                            puzzle.id
                    }

                }
            }

            COLUMN_USER_FOOD_LIST -> {

                UserManager.USER_REFERENCE?.let {

                    val foodList = it.get().await()[COLUMN_USER_FOOD_LIST]
                    for (food in foodList as List<String>){
                        listFromFirebase.add(food)
                    }
                }
            }

            COLUMN_USER_NUTRITION_LIST -> {

                UserManager.USER_REFERENCE?.let {

                    val nutritionList = it.get().await()[COLUMN_USER_NUTRITION_LIST]
                    for (nutrition in nutritionList as List<String>){
                        listFromFirebase.add(nutrition)
                    }
                }
            }
        }

        Logger.i("getListFromFirebase = $collection -> $listFromFirebase")
        return listFromFirebase
    }

    override suspend fun setOrUpdateObjects(
        collection: String,
        any: Any,
        documentId: String
    ) {

        when(collection){
            FirebaseKey.COLLECTION_GOAL -> {

                UserManager.USER_REFERENCE?.let {

                    when(documentId){
                        "" -> it.collection(collection).document().set(any)
                        else -> it.collection(collection).document(documentId).update(any as HashMap<String, Any>)
                    }
                }
            }

            FirebaseKey.COLLECTION_FOODIE -> {

                UserManager.USER_REFERENCE?.let {

                    when(documentId){
                        "" -> it.collection(collection).document().set(any)
                        else -> it.collection(collection).document(documentId).update(any as HashMap<String, Any>)
                    }
                }
            }

            FirebaseKey.COLLECTION_SHAPE -> {

                UserManager.USER_REFERENCE?.let {

                    when(documentId){
                        "" -> it.collection(collection).document().set(any)
                        else -> it.collection(collection).document(documentId).update(any as HashMap<String, Any>)
                    }
                }
            }

            COLUMN_USER_FOOD_LIST -> {

                UserManager.USER_REFERENCE?.let {
                    it.update(COLUMN_USER_FOOD_LIST, any)
                }
            }

            COLUMN_USER_NUTRITION_LIST -> {

                UserManager.USER_REFERENCE?.let {
                    it.update(COLUMN_USER_NUTRITION_LIST, any)
                }
            }

        }

        Logger.i("getListFromFirebase = $collection -> $any")
    }
}