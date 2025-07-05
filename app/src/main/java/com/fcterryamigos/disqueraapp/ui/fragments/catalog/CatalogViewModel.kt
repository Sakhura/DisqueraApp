package com.fcterryamigos.disqueraapp.ui.fragments.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.fcterryamigos.disqueraapp.data.repository.*
import com.fcterryamigos.disqueraapp.data.local.database.entities.*
import com.fcterryamigos.disqueraapp.data.local.preferences.UserPreferences

// ViewModel para el catálogo de discos
class CatalogViewModel(
    private val discoRepository: DiscoRepository
) : ViewModel() {

    val discos: LiveData<List<Disco>> = discoRepository.getAllDiscos()

    private val _filteredDiscos = MutableLiveData<List<Disco>>()
    val filteredDiscos: LiveData<List<Disco>> = _filteredDiscos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun searchDiscos(query: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = discoRepository.searchDiscos(query)
                _filteredDiscos.value = result
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Error al buscar discos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByGenero(genero: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = discoRepository.getDiscosByGenero(genero)
                _filteredDiscos.value = result
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Error al filtrar por género: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByPriceRange(minPrice: Double, maxPrice: Double) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = discoRepository.getDiscosByPriceRange(minPrice, maxPrice)
                _filteredDiscos.value = result
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Error al filtrar por precio: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearFilters() {
        _filteredDiscos.value = emptyList()
        _errorMessage.value = null
    }
}

