package com.fcterryamigos.disqueraapp.ui.fragments.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fcterryamigos.disqueraapp.data.local.database.entities.Disco
import com.fcterryamigos.disqueraapp.data.local.database.entities.Pedido
import com.fcterryamigos.disqueraapp.data.local.database.entities.Usuario
import com.fcterryamigos.disqueraapp.data.local.preferences.UserPreferences
import com.fcterryamigos.disqueraapp.data.repository.CarritoRepository
import com.fcterryamigos.disqueraapp.data.repository.DiscoRepository
import com.fcterryamigos.disqueraapp.data.repository.PedidoRepository
import com.fcterryamigos.disqueraapp.data.repository.UsuarioRepository
import com.fcterryamigos.disqueraapp.ui.fragments.catalog.CatalogViewModel

// ViewModel para el detalle del disco
class DiscoDetailViewModel(
    private val discoRepository: DiscoRepository,
    private val carritoRepository: CarritoRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _disco = MutableLiveData<Disco?>()
    val disco: LiveData<Disco?> = _disco

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _addToCartResult = MutableLiveData<String?>()
    val addToCartResult: LiveData<String?> = _addToCartResult

    fun loadDisco(discoId: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = discoRepository.getDiscoById(discoId)
                _disco.value = result
            } catch (e: Exception) {
                _addToCartResult.value = "Error al cargar el disco: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToCart(discoId: Long, cantidad: Int = 1) {
        if (!userPreferences.isLoggedIn) {
            _addToCartResult.value = "Debes iniciar sesión para agregar al carrito"
            return
        }

        viewModelScope.launch {
            try {
                carritoRepository.addToCarrito(userPreferences.userId, discoId, cantidad)
                _addToCartResult.value = "Disco agregado al carrito exitosamente"
            } catch (e: Exception) {
                _addToCartResult.value = "Error al agregar al carrito: ${e.message}"
            }
        }
    }

    fun clearMessages() {
        _addToCartResult.value = null
    }
}

