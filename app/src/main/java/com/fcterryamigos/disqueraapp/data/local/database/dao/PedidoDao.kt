package com.fcterryamigos.disqueraapp.data.local.database.dao

import androidx.lifecycle.LiveData
import com.fcterryamigos.disqueraapp.data.local.database.entities.EstadoPedido
import com.fcterryamigos.disqueraapp.data.local.database.entities.Pedido
import com.fcterryamigos.disqueraapp.data.local.database.entities.PedidoItem


@Dao
interface PedidoDao {
    @Query("SELECT * FROM pedidos WHERE userId = :userId ORDER BY fechaPedido DESC")
    fun getPedidosByUser(userId: Long): LiveData<List<Pedido>>

    @Query("SELECT * FROM pedidos ORDER BY fechaPedido DESC")
    suspend fun getAllPedidos(): List<Pedido>

    @Query("SELECT * FROM pedidos WHERE id = :id")
    suspend fun getPedidoById(id: Long): Pedido?

    @Query("SELECT * FROM pedido_items WHERE pedidoId = :pedidoId")
    suspend fun getPedidoItems(pedidoId: Long): List<PedidoItem>

    @Insert
    suspend fun insertPedido(pedido: Pedido): Long

    @Insert
    suspend fun insertPedidoItems(items: List<PedidoItem>)

    @Update
    suspend fun updatePedido(pedido: Pedido)

    @Query("UPDATE pedidos SET estado = :estado, fechaActualizacion = :fecha WHERE id = :id")
    suspend fun updateEstadoPedido(id: Long, estado: EstadoPedido, fecha: Long = System.currentTimeMillis())
}

// Data class para joins
data class CarritoItemWithDisco(
    val id: Long,
    val discoId: Long,
    val userId: Long,
    val cantidad: Int,
    val precio: Double,
    val fechaAgregado: Long,
    val titulo: String,
    val artista: String,
    val imagenUrl: String
)