package com.fcterryamigos.disqueraapp.data.local.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fcterryamigos.disqueraapp.data.local.database.entities.CarritoItem
import com.fcterryamigos.disqueraapp.data.local.database.relations.CarritoItemWithDisco

@Dao
interface CarritoDao {

    @Transaction
    @Query("""
        SELECT ci.*, d.titulo, d.artista, d.imagenUrl 
        FROM carrito_items ci 
        INNER JOIN discos d ON ci.discoId = d.id 
        WHERE ci.userId = :userId
    """)
    fun getCarritoItems(userId: Long): LiveData<List<CarritoItemWithDisco>>

    @Query("SELECT * FROM carrito_items WHERE userId = :userId AND discoId = :discoId")
    suspend fun getCarritoItem(userId: Long, discoId: Long): CarritoItem?

    @Query("SELECT SUM(cantidad * precio) FROM carrito_items WHERE userId = :userId")
    suspend fun getTotalCarrito(userId: Long): Double?

    @Query("SELECT COUNT(*) FROM carrito_items WHERE userId = :userId")
    suspend fun getCarritoItemCount(userId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCarritoItem(item: CarritoItem): Long

    @Update
    suspend fun updateCarritoItem(item: CarritoItem): Int

    @Delete
    suspend fun deleteCarritoItem(item: CarritoItem): Int

    @Query("DELETE FROM carrito_items WHERE userId = :userId")
    suspend fun clearCarrito(userId: Long): Int

    @Query("DELETE FROM carrito_items WHERE userId = :userId AND discoId = :discoId")
    suspend fun removeFromCarrito(userId: Long, discoId: Long): Int
}