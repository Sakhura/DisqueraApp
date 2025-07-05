package com.fcterryamigos.disqueraapp.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.fcterryamigos.disqueraapp.data.local.database.dao.UsuarioDao
import com.fcterryamigos.disqueraapp.data.local.database.dao.*
import com.fcterryamigos.disqueraapp.data.local.database.entities.*

@Database(
    entities = [
        Disco::class,
        Usuario::class,
        CarritoItem::class,
        Pedido::class,
        PedidoItem::class,
        Genero::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DisqueraDatabase : RoomDatabase() {

    abstract fun discoDao(): DiscoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun carritoDao(): CarritoDao
    abstract fun pedidoDao(): PedidoDao

    companion object {
        @Volatile
        private var INSTANCE: DisqueraDatabase? = null

        fun getDatabase(context: Context): DisqueraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DisqueraDatabase::class.java,
                    "disquera_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Converters para tipos personalizados
class Converters {
    @androidx.room.TypeConverter
    fun fromEstadoPedido(estado: EstadoPedido): String {
        return estado.name
    }

    @androidx.room.TypeConverter
    fun toEstadoPedido(estado: String): EstadoPedido {
        return EstadoPedido.valueOf(estado)
    }
}