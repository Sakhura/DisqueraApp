package com.fcterryamigos.disqueraapp.data.repository


import androidx.lifecycle.LiveData
import com.fcterryamigos.disqueraapp.data.local.database.dao.*
import com.fcterryamigos.disqueraapp.data.local.database.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DiscoRepository(
    private val discoDao: DiscoDao
) {
    fun getAllDiscos(): LiveData<List<Disco>> = discoDao.getAllDiscos()

    suspend fun getDiscoById(id: Long): Disco? = withContext(Dispatchers.IO) {
        discoDao.getDiscoById(id)
    }

    suspend fun searchDiscos(query: String): List<Disco> = withContext(Dispatchers.IO) {
        discoDao.searchDiscos(query)
    }

    suspend fun getDiscosByGenero(genero: String): List<Disco> = withContext(Dispatchers.IO) {
        discoDao.getDiscosByGenero(genero)
    }

    suspend fun getDiscosByPriceRange(minPrice: Double, maxPrice: Double): List<Disco> =
        withContext(Dispatchers.IO) {
            discoDao.getDiscosByPriceRange(minPrice, maxPrice)
        }

    suspend fun insertDisco(disco: Disco): Long = withContext(Dispatchers.IO) {
        discoDao.insertDisco(disco)
    }

    suspend fun updateDisco(disco: Disco) = withContext(Dispatchers.IO) {
        discoDao.updateDisco(disco)
    }

    suspend fun deleteDisco(id: Long) = withContext(Dispatchers.IO) {
        discoDao.deleteDisco(id)
    }

    suspend fun insertInitialData() = withContext(Dispatchers.IO) {
        val initialDiscos = listOf(
            Disco(
                titulo = "Abbey Road",
                artista = "The Beatles",
                genero = "Rock",
                precio = 29.99,
                descripcion = "Álbum icónico de The Beatles",
                imagenUrl = "https://example.com/abbey-road.jpg",
                stock = 10,
                fechaLanzamiento = "1969-09-26",
                duracion = "47:20",
                sello = "Apple Records"
            ),
            Disco(
                titulo = "Dark Side of the Moon",
                artista = "Pink Floyd",
                genero = "Progressive Rock",
                precio = 32.99,
                descripcion = "Obra maestra del rock progresivo",
                imagenUrl = "https://example.com/dark-side.jpg",
                stock = 15,
                fechaLanzamiento = "1973-03-01",
                duracion = "42:59",
                sello = "Harvest Records"
            )
        )
        discoDao.insertDiscos(initialDiscos)
    }
}

