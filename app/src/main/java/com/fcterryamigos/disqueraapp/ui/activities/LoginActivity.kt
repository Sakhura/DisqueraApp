package com.fcterryamigos.disqueraapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fcterryamigos.disqueraapp.data.local.database.DisqueraDatabase
import com.fcterryamigos.disqueraapp.data.local.preferences.UserPreferences
import com.fcterryamigos.disqueraapp.data.repository.UsuarioRepository
import kotlinx.coroutines.launch


// LoginActivity
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userPreferences: UserPreferences
    private lateinit var usuarioRepository: UsuarioRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences(this)
        val database = DisqueraDatabase.getDatabase(this)
        usuarioRepository = UsuarioRepository(database.usuarioDao())

        setupUI()

        // Si ya está logueado, ir a MainActivity
        if (userPreferences.isSessionValid()) {
            goToMainActivity()
        }
    }

    private fun setupUI() {
        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        binding.btnRegister.setOnClickListener {
            performRegister()
        }

        binding.tvForgotPassword.setOnClickListener {
            // TODO: Implementar recuperación de contraseña
            Toast.makeText(this, "Funcionalidad en desarrollo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val rememberMe = binding.cbRememberMe.isChecked

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnLogin.isEnabled = false
        binding.progressBar.visibility = android.view.View.VISIBLE

        lifecycleScope.launch {
            try {
                val usuario = usuarioRepository.getUsuarioByEmail(email)

                if (usuario != null && usuario.isActive) {
                    // En un entorno real, verificarías la contraseña hasheada
                    // Por simplicidad, asumimos que la validación es correcta

                    userPreferences.saveUserSession(usuario, rememberMe)

                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Bienvenido ${usuario.nombre}", Toast.LENGTH_SHORT).show()
                        goToMainActivity()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                runOnUiThread {
                    binding.btnLogin.isEnabled = true
                    binding.progressBar.visibility = android.view.View.GONE
                }
            }
        }
    }

    private fun performRegister() {
        val email = binding.etEmail.text.toString().trim()
        val nombre = binding.etNombre.text.toString().trim()
        val apellido = binding.etApellido.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Por favor ingresa un email válido", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnRegister.isEnabled = false
        binding.progressBar.visibility = android.view.View.VISIBLE

        lifecycleScope.launch {
            try {
                // Verificar si el email ya existe
                val existingUser = usuarioRepository.getUsuarioByEmail(email)
                if (existingUser != null) {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Este email ya está registrado", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Crear nuevo usuario
                val nuevoUsuario = Usuario(
                    email = email,
                    nombre = nombre,
                    apellido = apellido,
                    telefono = null,
                    direccion = null,
                    ciudad = null,
                    codigoPostal = null,
                    isAdmin = false
                )

                val userId = usuarioRepository.insertUsuario(nuevoUsuario)
                val usuarioCreado = nuevoUsuario.copy(id = userId)

                userPreferences.saveUserSession(usuarioCreado, binding.cbRememberMe.isChecked)

                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Registro exitoso. Bienvenido $nombre", Toast.LENGTH_SHORT).show()
                    goToMainActivity()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Error al registrarse: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                runOnUiThread {
                    binding.btnRegister.isEnabled = true
                    binding.progressBar.visibility = android.view.View.GONE
                }
            }
        }
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}