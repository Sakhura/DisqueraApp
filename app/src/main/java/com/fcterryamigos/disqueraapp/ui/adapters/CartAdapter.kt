package com.fcterryamigos.disqueraapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fcterryamigos.disqueraapp.R
import java.text.NumberFormat
import java.util.Locale

// Adaptador para el carrito
class CartAdapter(
    private val onQuantityChanged: (Long, Int) -> Unit,
    private val onRemoveClick: (Long) -> Unit
) : ListAdapter<CarritoItemWithDisco, CartAdapter.CartViewHolder>(CartDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CarritoItemWithDisco) {
            binding.apply {
                tvTitulo.text = item.titulo
                tvArtista.text = item.artista
                tvCantidad.text = item.cantidad.toString()

                val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
                tvPrecio.text = formatter.format(item.precio)
                tvSubtotal.text = formatter.format(item.precio * item.cantidad)

                // Cargar imagen
                Glide.with(ivDiscoImage.context)
                    .load(item.imagenUrl)
                    .placeholder(R.drawable.ic_vinyl)
                    .error(R.drawable.ic_vinyl)
                    .centerCrop()
                    .into(ivDiscoImage)

                // Botones de cantidad
                btnDecrease.setOnClickListener {
                    val newQuantity = item.cantidad - 1
                    onQuantityChanged(item.discoId, newQuantity)
                }

                btnIncrease.setOnClickListener {
                    val newQuantity = item.cantidad + 1
                    onQuantityChanged(item.discoId, newQuantity)
                }

                // Botón eliminar
                btnRemove.setOnClickListener {
                    onRemoveClick(item.discoId)
                }

                // Deshabilitar botón de disminuir si cantidad es 1
                btnDecrease.isEnabled = item.cantidad > 1
            }
        }
    }

    companion object CartDiffCallback : DiffUtil.ItemCallback<CarritoItemWithDisco>() {
        override fun areItemsTheSame(oldItem: CarritoItemWithDisco, newItem: CarritoItemWithDisco): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CarritoItemWithDisco, newItem: CarritoItemWithDisco): Boolean {
            return oldItem == newItem
        }
    }
}

