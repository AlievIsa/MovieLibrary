package com.example.movielibrary.data

import com.example.movielibrary.models.User

/**
 * User manager
 * This class is used to manage the current user data.
 * @constructor Create empty User manager
 */
object UserManager {
    var currentUser: User = User()
}