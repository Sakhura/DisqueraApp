package com.fcterryamigos.disqueraapp.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "carrito_items")
data class CarritoItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val discoId: Long,
    val cantidad: Int,
    val precio: Double,
    val fechaAgregado: Date = Date()
) {
    // Función helper para calcular el subtotal
    fun subtotal(): Double = cantidad * precio
}