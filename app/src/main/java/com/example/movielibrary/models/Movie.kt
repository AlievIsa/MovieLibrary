package com.example.movielibrary.models

/**
 * Movie
 * Этот класс используется работы с фильмами
 * @param movieName - название фильма
 * @param movieRate - рейтинг фильма
 * @param moviePoster - постер фильма
 * @param movieDescription - описание фильма
 * @param movieGenre - жанр фильма
 */
data class Movie (
    val name: String = "",
    val rate: String = "",
    val poster: String = "",
    val description: String = "",
    val genre: String = "",
    val releaseYear: String = ""
)