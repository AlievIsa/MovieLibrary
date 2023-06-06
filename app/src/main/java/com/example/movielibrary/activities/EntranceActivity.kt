package com.example.movielibrary.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.movielibrary.R

/**
 * Entrance activity
 * Данная activity является входной точкой приложения.
 * Здесь происходит авторизация пользователя. Отдельня activity нужна для того, чтобы
 * обновлять статус пользователя в MainActivity.
 * @constructor Create empty Entrance activity
 */
class EntranceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrance)
    }
}