package com.fcterryamigos.disqueraapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fcterryamigos.disqueraapp.R
import com.fcterryamigos.disqueraapp.data.local.database.entities.Disco
import com.fcterryamigos.disqueraapp.databinding.ItemDiscoBinding
import java.text.NumberFormat
import java.util.*

class DiscoAdapter(
    private val onDiscoClick: (Disco) -> Unit,
    private val onAddToCartClick: (Disco) -> Unit
) : ListAdapter<Disco, DiscoAdapter.DiscoViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoViewHolder {
        val binding = ItemDiscoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DiscoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiscoViewHolder, position: Int) {
        val disco = getItem(position)
        holder.bind(disco)
    }

    inner class DiscoViewHolder(
        private val binding: ItemDiscoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(disco: Disco) {
            binding.apply {
                tvTitulo.text = disco.titulo
                tvArtista.text = disco.artista
                tvGenero.text = disco.genero

                // Formatear precio
                val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
                tvPrecio.text = formatter.format(disco.precio)

                // Cargar imagen usando Glide
                Glide.with(ivDiscoImage.context)
                    .load(disco.imagenUrl)
                    .placeholder(R.drawable.ic_vinyl)
                    .error(R.drawable.ic_vinyl)
                    .centerCrop()
                    .into(ivDiscoImage)

                // Click listeners
                root.setOnClickListener {
                    onDiscoClick(disco)
                }

                btnAddToCart.setOnClickListener {
                    onAddToCartClick(disco)
                }

                // Deshabilitar botón si no hay stock
                btnAddToCart.isEnabled = disco.stock > 0
                if (disco.stock == 0) {
                    btnAddToCart.text = "Sin Stock"
                } else {
                    btnAddToCart.text = "Agregar"
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Disco>() {
        override fun areItemsTheSame(oldItem: Disco, newItem: Disco): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Disco, newItem: Disco): Boolean {
            return oldItem == newItem
        }
    }
}

