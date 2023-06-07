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
    val movieName: String = "",
    val movieRate: String = "",
    val moviePoster: String = "",
    val movieDescription: String = "",
    val movieGenre: String = "",
    val movieReleaseYear: String = ""
)