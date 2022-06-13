package com.example.filmfinder.dto.movie.details

data class MovieDetailsDTO(
    val title: String,
    val year: String,
    val released: String,
    val description: String,
    val runtime: String,
    val score: String,
    val imdbid: String,
    val traktid: String,
    val tmdbid: String,
    val type: String,
    val ratings: List<RatingDTO>,
    val streams: List<StreamDTO>,
    val watch_providers: List<WatchProviderDTO>,
    val keywords: List<KeywordDTO>,
    val language: String,
    val country: String,
    val certification: String,
    val status: String,
    val trailer: String,
    val poster: String,
    val backdrop: String,
    val response: Boolean,
    val apiused: Long
    )
