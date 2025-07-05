package com.fcterryamigos.disqueraapp.data.repository

import androidx.lifecycle.LiveData
import com.fcterryamigos.disqueraapp.data.local.database.dao.CarritoDao
import com.fcterryamigos.disqueraapp.data.local.database.dao.PedidoDao
import com.fcterryamigos.disqueraapp.data.local.database.entities.EstadoPedido
import com.fcterryamigos.disqueraapp.data.local.database.entities.Pedido
import com.fcterryamigos.disqueraapp.data.local.database.entities.PedidoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PedidoRepository(
    private val pedidoDao: PedidoDao,
    private val carritoDao: CarritoDao
) {
    fun getPedidosByUser(userId: Long): LiveData<List<Pedido>> =
        pedidoDao.getPedidosByUser(userId)

    suspend fun getAllPedidos(): List<Pedido> = withContext(Dispatchers.IO) {
        pedidoDao.getAllPedidos()
    }

    suspend fun getPedidoById(id: Long): Pedido? = withContext(Dispatchers.IO) {
        pedidoDao.getPedidoById(id)
    }

    suspend fun getPedidoItems(pedidoId: Long): List<PedidoItem> = withContext(Dispatchers.IO) {
        pedidoDao.getPedidoItems(pedidoId)
    }

    suspend fun createPedidoFromCarrito(
        userId: Long,
        direccionEnvio: String,
        metodoPago: String
    ): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val carritoItems = carritoDao.getCarritoItems(userId).value ?: emptyList()
            if (carritoItems.isEmpty()) {
                return@withContext Result.failure(Exception("El carrito está vacío"))
            }

            val total = carritoItems.sumOf { it.cantidad * it.precio }

            val pedido = Pedido(
                userId = userId,
                total = total,
                estado = EstadoPedido.PENDIENTE,
                direccionEnvio = direccionEnvio,
                metodoPago = metodoPago
            )

            val pedidoId = pedidoDao.insertPedido(pedido)

            val pedidoItems = carritoItems.map { carritoItem ->
                PedidoItem(
                    pedidoId = pedidoId,
                    discoId = carritoItem.discoId,
                    cantidad = carritoItem.cantidad,
                    precio = carritoItem.precio,
                    tituloEn = carritoItem.titulo,
                    artistaEn = carritoItem.artista
                )
            }

            pedidoDao.insertPedidoItems(pedidoItems)
            carritoDao.clearCarrito(userId)

            Result.success(pedidoId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateEstadoPedido(pedidoId: Long, nuevoEstado: EstadoPedido) =
        withContext(Dispatchers.IO) {
            pedidoDao.updateEstadoPedido(pedidoId, nuevoEstado)
        }
}