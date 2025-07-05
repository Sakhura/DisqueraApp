package com.fcterryamigos.disqueraapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fcterryamigos.disqueraapp.R
import java.text.NumberFormat
import java.util.Locale

// Adaptador para pedidos
class PedidoAdapter(
    private val onPedidoClick: (Pedido) -> Unit
) : ListAdapter<Pedido, PedidoAdapter.PedidoViewHolder>(PedidoDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val binding = ItemPedidoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PedidoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = getItem(position)
        holder.bind(pedido)
    }

    inner class PedidoViewHolder(
        private val binding: ItemPedidoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pedido: Pedido) {
            binding.apply {
                tvPedidoId.text = "Pedido #${pedido.id}"

                val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
                tvTotal.text = formatter.format(pedido.total)

                // Formatear fecha
                val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                tvFecha.text = dateFormat.format(Date(pedido.fechaPedido))

                // Estado del pedido
                tvEstado.text = when (pedido.estado) {
                    EstadoPedido.PENDIENTE -> "Pendiente"
                    EstadoPedido.PROCESANDO -> "Procesando"
                    EstadoPedido.ENVIADO -> "Enviado"
                    EstadoPedido.ENTREGADO -> "Entregado"
                    EstadoPedido.CANCELADO -> "Cancelado"
                }

                // Color del estado
                val colorRes = when (pedido.estado) {
                    EstadoPedido.PENDIENTE -> R.color.warning_color
                    EstadoPedido.PROCESANDO -> R.color.primary_color
                    EstadoPedido.ENVIADO -> R.color.secondary_color
                    EstadoPedido.ENTREGADO -> R.color.success_color
                    EstadoPedido.CANCELADO -> R.color.error_color
                }
                tvEstado.setTextColor(itemView.context.getColor(colorRes))

                root.setOnClickListener {
                    onPedidoClick(pedido)
                }
            }
        }
    }

    companion object PedidoDiffCallback : DiffUtil.ItemCallback<Pedido>() {
        override fun areItemsTheSame(oldItem: Pedido, newItem: Pedido): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Pedido, newItem: Pedido): Boolean {
            return oldItem == newItem
        }
    }
}

