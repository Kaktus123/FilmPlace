package com.example.filmfinder.dto.movie.search

import java.io.Serializable

data class SearchDTO(
    val id: String,
    val title: String,
    val year: Int,
    val score: Int,
    val type: String,
    val imdbid: String,
    val tmdbid: Int,
    val traktid: Int,
) : Serializable
