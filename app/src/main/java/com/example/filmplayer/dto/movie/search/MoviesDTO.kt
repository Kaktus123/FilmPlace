package com.example.filmfinder.dto.movie.search

import java.io.Serializable

data class MoviesDTO(
    val search: List<SearchDTO>,
    val total: Int,
    val response: Boolean
):Serializable
