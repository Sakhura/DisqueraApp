package com.fcterryamigos.disqueraapp.data.local.database.entities


import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Entity(tableName = "discos")
@Parcelize
data class Disco(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val artista: String,
    val genero: String,
    val precio: Double,
    val descripcion: String,
    val imagenUrl: String,
    val stock: Int,
    val fechaLanzamiento: String,
    val duracion: String,
    val sello: String,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable

@Entity(tableName = "usuarios")
@Parcelize
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val nombre: String,
    val apellido: String,
    val telefono: String?,
    val direccion: String?,
    val ciudad: String?,
    val codigoPostal: String?,
    val isAdmin: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable

@Entity(tableName = "carrito_items")
data class CarritoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val discoId: Long,
    val userId: Long,
    val cantidad: Int,
    val precio: Double,
    val fechaAgregado: Long = System.currentTimeMillis()
)

@Entity(tableName = "pedidos")
data class Pedido(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val total: Double,
    val estado: EstadoPedido,
    val direccionEnvio: String,
    val metodoPago: String,
    val fechaPedido: Long = System.currentTimeMillis(),
    val fechaActualizacion: Long = System.currentTimeMillis()
)

@Entity(tableName = "pedido_items")
data class PedidoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pedidoId: Long,
    val discoId: Long,
    val cantidad: Int,
    val precio: Double,
    val tituloEn: String, // Snapshot del título por si se modifica el disco
    val artistaEn: String // Snapshot del artista
)

@Entity(tableName = "generos")
data class Genero(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val descripcion: String?,
    val isActive: Boolean = true
)

enum class EstadoPedido {
    PENDIENTE,
    PROCESANDO,
    ENVIADO,
    ENTREGADO,
    CANCELADO
}