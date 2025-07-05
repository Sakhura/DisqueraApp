package com.fcterryamigos.disqueraapp.ui.fragments.cart

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
import com.fcterryamigos.disqueraapp.ui.fragments.catalog.CatalogViewModel
import com.fcterryamigos.disqueraapp.ui.fragments.detail.DiscoDetailViewModel

// ViewModel para el carrito
class CartViewModel(
    private val carritoRepository: CarritoRepository,
    private val pedidoRepository: PedidoRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val carritoItems: LiveData<List<CarritoItemWithDisco>> =
        carritoRepository.getCarritoItems(userPreferences.userId)

    private val _totalCarrito = MutableLiveData<Double>()
    val totalCarrito: LiveData<Double> = _totalCarrito

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _checkoutResult = MutableLiveData<String?>()
    val checkoutResult: LiveData<String?> = _checkoutResult

    init {
        loadCartTotal()
    }

    private fun loadCartTotal() {
        viewModelScope.launch {
            val total = carritoRepository.getTotalCarrito(userPreferences.userId)
            _totalCarrito.value = total
        }
    }

    fun updateQuantity(discoId: Long, newQuantity: Int) {
        viewModelScope.launch {
            try {
                carritoRepository.updateCarritoItemQuantity(
                    userPreferences.userId,
                    discoId,
                    newQuantity
                )
                loadCartTotal()
            } catch (e: Exception) {
                _checkoutResult.value = "Error al actualizar cantidad: ${e.message}"
            }
        }
    }

    fun removeFromCart(discoId: Long) {
        viewModelScope.launch {
            try {
                carritoRepository.removeFromCarrito(userPreferences.userId, discoId)
                loadCartTotal()
            } catch (e: Exception) {
                _checkoutResult.value = "Error al eliminar del carrito: ${e.message}"
            }
        }
    }

    fun proceedToCheckout(direccionEnvio: String, metodoPago: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = pedidoRepository.createPedidoFromCarrito(
                    userPreferences.userId,
                    direccionEnvio,
                    metodoPago
                )

                if (result.isSuccess) {
                    _checkoutResult.value = "Pedido realizado exitosamente. ID: ${result.getOrNull()}"
                    loadCartTotal()
                } else {
                    _checkoutResult.value = "Error al realizar el pedido: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _checkoutResult.value = "Error al procesar el pedido: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        _checkoutResult.value = null
    }
}

