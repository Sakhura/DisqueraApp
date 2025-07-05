package com.fcterryamigos.disqueraapp.data.repository

import androidx.lifecycle.LiveData
import com.fcterryamigos.disqueraapp.data.local.database.dao.CarritoDao
import com.fcterryamigos.disqueraapp.data.local.database.dao.CarritoItemWithDisco
import com.fcterryamigos.disqueraapp.data.local.database.dao.DiscoDao
import com.fcterryamigos.disqueraapp.data.local.database.dao.PedidoDao
import com.fcterryamigos.disqueraapp.data.local.database.dao.UsuarioDao
import com.fcterryamigos.disqueraapp.data.local.database.entities.CarritoItem
import com.fcterryamigos.disqueraapp.data.local.database.entities.EstadoPedido
import com.fcterryamigos.disqueraapp.data.local.database.entities.Pedido
import com.fcterryamigos.disqueraapp.data.local.database.entities.PedidoItem
import com.fcterryamigos.disqueraapp.data.local.database.entities.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsuarioRepository(
    private val usuarioDao: UsuarioDao
) {
    suspend fun getUsuarioById(id: Long): Usuario? = withContext(Dispatchers.IO) {
        usuarioDao.getUsuarioById(id)
    }

    suspend fun getUsuarioByEmail(email: String): Usuario? = withContext(Dispatchers.IO) {
        usuarioDao.getUsuarioByEmail(email)
    }

    suspend fun insertUsuario(usuario: Usuario): Long = withContext(Dispatchers.IO) {
        usuarioDao.insertUsuario(usuario)
    }

    suspend fun updateUsuario(usuario: Usuario) = withContext(Dispatchers.IO) {
        usuarioDao.updateUsuario(usuario)
    }

    suspend fun getAllUsuarios(): List<Usuario> = withContext(Dispatchers.IO) {
        usuarioDao.getAllUsuarios()
    }
}

