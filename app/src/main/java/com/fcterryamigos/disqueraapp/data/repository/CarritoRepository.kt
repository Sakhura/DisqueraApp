package com.fcterryamigos.disqueraapp.data.repository

import androidx.lifecycle.LiveData
import com.fcterryamigos.disqueraapp.data.local.database.dao.CarritoDao
import com.fcterryamigos.disqueraapp.data.local.database.dao.CarritoItemWithDisco
import com.fcterryamigos.disqueraapp.data.local.database.dao.DiscoDao
import com.fcterryamigos.disqueraapp.data.local.database.dao.PedidoDao
import com.fcterryamigos.disqueraapp.data.local.database.entities.CarritoItem
import com.fcterryamigos.disqueraapp.data.local.database.entities.EstadoPedido
import com.fcterryamigos.disqueraapp.data.local.database.entities.Pedido
import com.fcterryamigos.disqueraapp.data.local.database.entities.PedidoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CarritoRepository(
    private val carritoDao: CarritoDao,
    private val discoDao: DiscoDao
) {
    fun getCarritoItems(userId: Long): LiveData<List<CarritoItemWithDisco>> =
        carritoDao.getCarritoItems(userId)

    suspend fun addToCarrito(userId: Long, discoId: Long, cantidad: Int = 1) =
        withContext(Dispatchers.IO) {
            val disco = discoDao.getDiscoById(discoId)
            disco?.let {
                val existingItem = carritoDao.getCarritoItem(userId, discoId)
                if (existingItem != null) {
                    val updatedItem = existingItem.copy(cantidad = existingItem.cantidad + cantidad)
                    carritoDao.updateCarritoItem(updatedItem)
                } else {
                    val newItem = CarritoItem(
                        discoId = discoId,
                        userId = userId,
                        cantidad = cantidad,
                        precio = disco.precio
                    )
                    carritoDao.insertCarritoItem(newItem)
                }
            }
        }

    suspend fun updateCarritoItemQuantity(userId: Long, discoId: Long, newQuantity: Int) =
        withContext(Dispatchers.IO) {
            if (newQuantity <= 0) {
                carritoDao.removeFromCarrito(userId, discoId)
            } else {
                val item = carritoDao.getCarritoItem(userId, discoId)
                item?.let {
                    carritoDao.updateCarritoItem(it.copy(cantidad = newQuantity))
                }
            }
        }

    suspend fun removeFromCarrito(userId: Long, discoId: Long) = withContext(Dispatchers.IO) {
        carritoDao.removeFromCarrito(userId, discoId)
    }

    suspend fun clearCarrito(userId: Long) = withContext(Dispatchers.IO) {
        carritoDao.clearCarrito(userId)
    }

    suspend fun getTotalCarrito(userId: Long): Double = withContext(Dispatchers.IO) {
        carritoDao.getTotalCarrito(userId) ?: 0.0
    }

    suspend fun getCarritoItemCount(userId: Long): Int = withContext(Dispatchers.IO) {
        carritoDao.getCarritoItemCount(userId)
    }
}

