package com.example.filmplayer.entity

import java.io.Serializable

class UserInfo(
    var googleId: String? = null,
    var favouriteMoviesId: List<String>? = null,
    var watchlistMoviesId: List<String>? = null
) : Serializable
