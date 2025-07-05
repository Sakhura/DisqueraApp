package com.fcterryamigos.disqueraapp.ui.fragments.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fcterryamigos.disqueraapp.data.local.database.entities.Pedido
import com.fcterryamigos.disqueraapp.data.local.database.entities.Usuario
import com.fcterryamigos.disqueraapp.data.local.preferences.UserPreferences
import com.fcterryamigos.disqueraapp.data.repository.CarritoRepository
import com.fcterryamigos.disqueraapp.data.repository.DiscoRepository
import com.fcterryamigos.disqueraapp.data.repository.PedidoRepository
import com.fcterryamigos.disqueraapp.data.repository.UsuarioRepository
import com.fcterryamigos.disqueraapp.ui.fragments.cart.CartViewModel
import com.fcterryamigos.disqueraapp.ui.fragments.catalog.CatalogViewModel
import com.fcterryamigos.disqueraapp.ui.fragments.detail.DiscoDetailViewModel

// ViewModel para el perfil y pedidos del usuario
class ProfileViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val pedidoRepository: PedidoRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: LiveData<Usuario?> = _usuario

    val pedidos: LiveData<List<Pedido>> =
        pedidoRepository.getPedidosByUser(userPreferences.userId)

    private val _updateResult = MutableLiveData<String?>()
    val updateResult: LiveData<String?> = _updateResult

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val user = usuarioRepository.getUsuarioById(userPreferences.userId)
                _usuario.value = user
            } catch (e: Exception) {
                _updateResult.value = "Error al cargar perfil: ${e.message}"
            }
        }
    }

    fun updateProfile(usuario: Usuario) {
        viewModelScope.launch {
            try {
                usuarioRepository.updateUsuario(usuario)
                userPreferences.saveUserSession(usuario, userPreferences.rememberMe)
                _updateResult.value = "Perfil actualizado exitosamente"
                loadUserProfile()
            } catch (e: Exception) {
                _updateResult.value = "Error al actualizar perfil: ${e.message}"
            }
        }
    }

    fun logout() {
        userPreferences.clearUserSession()
    }

    fun clearMessages() {
        _updateResult.value = null
    }
}

// Factory para crear ViewModels con dependencias
class ViewModelFactory(
    private val discoRepository: DiscoRepository,
    private val usuarioRepository: UsuarioRepository,
    private val carritoRepository: CarritoRepository,
    private val pedidoRepository: PedidoRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            CatalogViewModel::class.java -> {
                CatalogViewModel(discoRepository) as T
            }
            DiscoDetailViewModel::class.java -> {
                DiscoDetailViewModel(discoRepository, carritoRepository, userPreferences) as T
            }
            CartViewModel::class.java -> {
                CartViewModel(carritoRepository, pedidoRepository, userPreferences) as T
            }
            ProfileViewModel::class.java -> {
                ProfileViewModel(usuarioRepository, pedidoRepository, userPreferences) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}