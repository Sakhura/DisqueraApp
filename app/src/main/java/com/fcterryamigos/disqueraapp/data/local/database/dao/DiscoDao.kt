package com.fcterryamigos.disqueraapp.data.local.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fcterryamigos.disqueraapp.data.local.database.entities.*

@Dao
interface DiscoDao {
    @Query("SELECT * FROM discos WHERE isActive = 1 ORDER BY titulo ASC")
    fun getAllDiscos(): LiveData<List<Disco>>

    @Query("SELECT * FROM discos WHERE id = :id")
    suspend fun getDiscoById(id: Long): Disco?

    @Query("SELECT * FROM discos WHERE titulo LIKE '%' || :searchQuery || '%' OR artista LIKE '%' || :searchQuery || '%' AND isActive = 1")
    suspend fun searchDiscos(searchQuery: String): List<Disco>

    @Query("SELECT * FROM discos WHERE genero = :genero AND isActive = 1")
    suspend fun getDiscosByGenero(genero: String): List<Disco>

    @Query("SELECT * FROM discos WHERE precio BETWEEN :minPrice AND :maxPrice AND isActive = 1")
    suspend fun getDiscosByPriceRange(minPrice: Double, maxPrice: Double): List<Disco>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDisco(disco: Disco): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiscos(discos: List<Disco>)

    @Update
    suspend fun updateDisco(disco: Disco)

    @Query("UPDATE discos SET isActive = 0 WHERE id = :id")
    suspend fun deleteDisco(id: Long)

    @Query("DELETE FROM discos")
    suspend fun deleteAllDiscos()
}

