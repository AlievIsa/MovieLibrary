package com.example.movielibrary.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.movielibrary.R
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Main activity
 * Это главная activity приложения. Здесь происходит навигация между фрагментами.
 * @constructor создает пустой Main activity
 */
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController // Отвечает за навигацию между фрагментами

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        // Находим NavHostFragment и получаем NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController
        // Устанавливаем BottomNavigationView для NavController
        bottomNavigationView.setupWithNavController(navController)
    }
}