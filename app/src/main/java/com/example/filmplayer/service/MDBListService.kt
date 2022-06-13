package com.example.filmplayer.service

import android.os.SystemClock
import android.util.Log
import com.example.filmfinder.dto.movie.details.MovieDetailsDTO
import com.example.filmfinder.dto.movie.search.MoviesDTO
import com.example.filmplayer.fragment.MovieDetailsFragment
import com.example.filmplayer.fragment.SearchResultsFragment
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class MDBListService {
    private val client = OkHttpClient()

    private companion object {
        const val TAG = "MDB_LIST_SERVICE"
        const val SCHEME = "https"
        const val HOST = "mdblist.p.rapidapi.com"
        const val X_RAPID_API_HOST_HEADER_NAME = "X-RapidAPI-Host"
        const val X_RAPID_API_HOST_HEADER_VALUE = "mdblist.p.rapidapi.com"
        const val X_RAPID_API_KEY_HEADER_NAME = "X-RapidAPI-Key"
        const val X_RAPID_API_KEY_HEADER_VALUE = "33494387dbmshad4f6c24a79b25cp13e6f5jsn7a84429b6282"
    }

    fun searchForMovie(phrase: String, searchResultsFragment: SearchResultsFragment) {
        val request = createOkHttpRequest("s", phrase)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "onFailure: Message ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val respBody = response.body?.string()
                Log.d(TAG, "onResponse: Resp: ${respBody}")
                val movies: MoviesDTO = Gson().fromJson(respBody, MoviesDTO::class.java)

                searchResultsFragment.updateView(movies)
            }
        })
    }

    fun getMovieDetails( movieId: String, movieDetailsFragment: MovieDetailsFragment) {
        val request = createOkHttpRequest("i", movieId)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "onFailure: Message ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val respBody = response.body?.string()
                Log.d(TAG, "onResponse: Movie details response: ${respBody}")
                val movieDetails: MovieDetailsDTO =
                    Gson().fromJson(respBody, MovieDetailsDTO::class.java)
                movieDetailsFragment.updateView(movieDetails)
            }
        })
    }

    fun getMovieDetailsList(movieIdList: List<String>): List<MovieDetailsDTO> {
        val movies = mutableListOf<MovieDetailsDTO>()
        for (movieId in movieIdList) {
            val request = createOkHttpRequest("i", movieId)
            val response: Response = client.newCall(request).execute()
            SystemClock.sleep(50)
            val respBody = response.body?.string()
            val movieDetails: MovieDetailsDTO = Gson().fromJson(respBody, MovieDetailsDTO::class.java)
            movies.add(movieDetails)
        }
        return movies
    }

    private fun createOkHttpRequest(paramName: String, paramValue: String): Request {
        val build = HttpUrl.Builder()
            .scheme(SCHEME)
            .host(HOST)
            .addQueryParameter(paramName, paramValue)
            .build()

        return Request.Builder()
            .url(build)
            .get()
            .header(X_RAPID_API_HOST_HEADER_NAME, X_RAPID_API_HOST_HEADER_VALUE)
            .header(X_RAPID_API_KEY_HEADER_NAME, X_RAPID_API_KEY_HEADER_VALUE)
            .build()
    }
}