package com.example.filmplayer.service

import android.util.Log
import com.example.filmplayer.entity.UserInfo
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserService {
    private val database: DatabaseReference = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference(DATABASE_PATH)
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private companion object {
        const val TAG = "USER_SERVICE"
        const val FIREBASE_DATABASE_URL = "https://filmplayer-58038-default-rtdb.europe-west1.firebasedatabase.app"
        const val DATABASE_PATH = "info"
    }

    fun addMovieToFavourites(movieId: String) {
        addMovie(movieId, addToFavourites = true, addToWatchlist = false)
    }

    fun addMovieToWatchlist(movieId: String) {
        addMovie(movieId, addToFavourites = false, addToWatchlist = true)
    }

    fun removeMovieFromFavourites(movieId: String) {
        removeMovie(movieId, removeFromFavourites = true, removeFromWatchlist = false)
    }

    fun removeMovieFromWatchlist(movieId: String) {
        removeMovie(movieId, removeFromFavourites = false, removeFromWatchlist = true)
    }

    fun getUserInfo(): Task<DataSnapshot> {
        val userUID = firebaseAuth.currentUser.uid
        return database.child(userUID).get()
    }

    private fun addMovie(movieId: String, addToFavourites: Boolean, addToWatchlist: Boolean) {
        val userUID = firebaseAuth.currentUser.uid
        database.child(userUID).get().addOnSuccessListener {
            Log.d(TAG, "Retrieved document from DB: ${it.value}")

            val favouriteMovies = mutableListOf<String>()
            val watchlistMovies = mutableListOf<String>()

            if(addToFavourites){
                favouriteMovies.add(movieId)
            }

            if(addToWatchlist){
                watchlistMovies.add(movieId)
            }

            if (it.value != null) {
                val currentUserInfo: UserInfo? = it.getValue(UserInfo::class.java)
                currentUserInfo!!.favouriteMoviesId?.let { it1 -> favouriteMovies.addAll(it1) }
                currentUserInfo.watchlistMoviesId?.let { it1 -> watchlistMovies.addAll(it1) }
            }
            val userInfo = UserInfo(userUID, favouriteMovies.distinct(), watchlistMovies.distinct())

            database.child(userUID).setValue(userInfo)
        }.addOnFailureListener{
            Log.e(TAG, "Error while saving to DB", it)
        }
    }

    private fun removeMovie(movieId: String, removeFromFavourites: Boolean, removeFromWatchlist: Boolean) {
        val userUID = firebaseAuth.currentUser.uid
        database.child(userUID).get().addOnSuccessListener {
            Log.d(TAG, "Retrieved document from DB: ${it.value}")

            val favouriteMovies = mutableListOf<String>()
            val watchlistMovies = mutableListOf<String>()

            val currentUserInfo: UserInfo? = it.getValue(UserInfo::class.java)
            currentUserInfo!!.favouriteMoviesId?.let { it1 -> favouriteMovies.addAll(it1) }
            currentUserInfo.watchlistMoviesId?.let { it1 -> watchlistMovies.addAll(it1) }

            if (removeFromFavourites) {
                favouriteMovies.remove(movieId)
            }

            if (removeFromWatchlist) {
                watchlistMovies.remove(movieId)
            }

            val userInfo = UserInfo(userUID, favouriteMovies.distinct(), watchlistMovies.distinct())

            database.child(userUID).setValue(userInfo)
        }.addOnFailureListener{
            Log.e(TAG, "Error while removing from DB", it)
        }
    }
}