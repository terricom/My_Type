package com.terricom.mytype.data

import java.sql.Timestamp

class DefaultFirebaseRepository(private val firebaseDataSource: FirebaseDataSource): FirebaseRepository {

    override suspend fun queryFoodie(key: String, type: String): List<Foodie> {
        return firebaseDataSource.queryFoodie(key= key, type= type)
    }

    override suspend fun updatePuzzle(): Int {
        return firebaseDataSource.updatePuzzle()
    }

    override suspend fun deleteObjects(collection: String, any: Any) {
        return firebaseDataSource.deleteObjects(collection= collection, any = any )
    }

    override suspend fun getObjects(collection: String, start: Timestamp, end: Timestamp): List<Any> {
        return firebaseDataSource.getObjects(collection= collection, start = start, end = end)
    }

}

