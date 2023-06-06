package com.example.movielibrary.models

/**
 * User
 * Этот класс используется работы с пользователями
 * @param id - идентификатор пользователя в firebase
 * @param name - имя пользователя
 * @param email - почта пользователя
 * @param profile_picture - фотография профиля пользователя
 */
data class User(
    val id: String = "",
    var name: String = "",
    val email: String = "",
    var profile_picture: String = "",
)
