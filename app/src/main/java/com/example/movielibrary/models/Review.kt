package com.example.movielibrary.models

import kotlin.random.Random

data class Review (
    val id: Int = getAutoId(),
    val userId: String = "",
    val movieName: String = "",
    var text: String = ""
) {
    companion object {
        fun getAutoId(): Int {
            val random = Random
            return random.nextInt(1000)
        }
    }
}