package com.fcterryamigos.disqueraapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.fcterryamigos.disqueraapp.R
import com.fcterryamigos.disqueraapp.data.local.database.DisqueraDatabase
import com.fcterryamigos.disqueraapp.data.local.preferences.UserPreferences
import com.fcterryamigos.disqueraapp.data.repository.*
import com.fcterryamigos.disqueraapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences(this)

        // Verificar si el usuario tiene una sesión válida
        checkUserSession()

        setupNavigation()
        initializeDatabase()
    }

    private fun checkUserSession() {
        if (!userPreferences.isSessionValid()) {
            // Si no hay sesión válida, ir al login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        // Configurar listener para mostrar badge en el carrito
        updateCartBadge()
    }

    private fun updateCartBadge() {
        val database = DisqueraDatabase.getDatabase(this)
        val carritoRepository = CarritoRepository(database.carritoDao(), database.discoDao())

        lifecycleScope.launch {
            try {
                val itemCount = carritoRepository.getCarritoItemCount(userPreferences.userId)
                val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
                val badge = bottomNav.getOrCreateBadge(R.id.cart_fragment)

                if (itemCount > 0) {
                    badge.isVisible = true
                    badge.number = itemCount
                } else {
                    badge.isVisible = false
                }
            } catch (e: Exception) {
                // Handle error silently for badge
            }
        }
    }

    private fun initializeDatabase() {
        val database = DisqueraDatabase.getDatabase(this)
        val discoRepository = DiscoRepository(database.discoDao())

        // Insertar datos iniciales si es la primera vez
        lifecycleScope.launch {
            try {
                discoRepository.insertInitialData()
            } catch (e: Exception) {
                // Los datos ya existen, no hacer nada
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge()
    }
}
