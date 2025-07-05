package com.fcterryamigos.disqueraapp.data.local.database.dao

import androidx.lifecycle.LiveData
import com.fcterryamigos.disqueraapp.data.local.database.entities.CarritoItem
import com.fcterryamigos.disqueraapp.data.local.database.entities.EstadoPedido
import com.fcterryamigos.disqueraapp.data.local.database.entities.Pedido
import com.fcterryamigos.disqueraapp.data.local.database.entities.PedidoItem
import com.fcterryamigos.disqueraapp.data.local.database.entities.Usuario

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun getUsuarioById(id: Long): Usuario?

    @Query("SELECT * FROM usuarios WHERE email = :email")
    suspend fun getUsuarioByEmail(email: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE isActive = 1")
    suspend fun getAllUsuarios(): List<Usuario>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: Usuario): Long

    @Update
    suspend fun updateUsuario(usuario: Usuario)

    @Query("UPDATE usuarios SET isActive = 0 WHERE id = :id")
    suspend fun deleteUsuario(id: Long)
}
