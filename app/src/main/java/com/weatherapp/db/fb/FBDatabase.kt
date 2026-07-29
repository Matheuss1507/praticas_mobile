package com.weatherapp.db.fb

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class FBDatabase {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    fun register(user: FBUser) {
        val userAuth = auth.currentUser ?: throw RuntimeException("User not logged in!")
        db.collection("users").document(userAuth.uid).set(user)
    }

    fun add(city: FBCity) {
        val userAuth = auth.currentUser ?: throw RuntimeException("User not logged in!")
        if (city.name.isNullOrEmpty()) throw RuntimeException("City with null or empty name!")

        db.collection("users").document(userAuth.uid)
            .collection("cities").document(city.name!!).set(city)
    }

    fun remove(city: FBCity) {
        val userAuth = auth.currentUser ?: throw RuntimeException("User not logged in!")
        if (city.name.isNullOrEmpty()) throw RuntimeException("City with null or empty name!")

        db.collection("users").document(userAuth.uid)
            .collection("cities").document(city.name!!).delete()
    }

    fun update(city: FBCity) {
        val userAuth = auth.currentUser ?: throw RuntimeException("Not logged in!")
        if (city.name.isNullOrEmpty()) throw RuntimeException("City with null or empty name!")

        val changes = mapOf(
            "lat" to city.lat,
            "lng" to city.lng,
            "monitored" to city.isMonitored
        )
        db.collection("users").document(userAuth.uid)
            .collection("cities").document(city.name!!).update(changes)
    }

    val user: Flow<FBUser>
        get() {
            val currentUser = auth.currentUser ?: return emptyFlow()
            return db.collection("users")
                .document(currentUser.uid)
                .snapshots()
                .mapNotNull { it.toObject(FBUser::class.java) }
        }

    val cities: Flow<List<FBCity>>
        get() {
            val currentUser = auth.currentUser ?: return emptyFlow()
            return db.collection("users")
                .document(currentUser.uid)
                .collection("cities")
                .snapshots()
                .map { snapshot -> snapshot.toObjects(FBCity::class.java) }
        }
}