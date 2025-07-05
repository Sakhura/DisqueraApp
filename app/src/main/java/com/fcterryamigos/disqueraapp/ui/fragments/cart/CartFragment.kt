package com.fcterryamigos.disqueraapp.ui.fragments.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fcterryamigos.disqueraapp.R
import com.fcterryamigos.disqueraapp.data.local.database.DisqueraDatabase
import com.fcterryamigos.disqueraapp.data.local.preferences.UserPreferences
import com.fcterryamigos.disqueraapp.data.repository.CarritoRepository
import com.fcterryamigos.disqueraapp.data.repository.DiscoRepository
import com.fcterryamigos.disqueraapp.data.repository.PedidoRepository
import com.fcterryamigos.disqueraapp.data.repository.UsuarioRepository
import com.fcterryamigos.disqueraapp.ui.adapters.DiscoAdapter
import com.fcterryamigos.disqueraapp.ui.fragments.catalog.CatalogViewModel

// CartFragment
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartAdapter: CartAdapter
    private lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: CartViewModel by viewModels { viewModelFactory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDependencies()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupDependencies() {
        val database = DisqueraDatabase.getDatabase(requireContext())
        val userPreferences = UserPreferences(requireContext())

        val discoRepository = DiscoRepository(database.discoDao())
        val usuarioRepository = UsuarioRepository(database.usuarioDao())
        val carritoRepository = CarritoRepository(database.carritoDao(), database.discoDao())
        val pedidoRepository = PedidoRepository(database.pedidoDao(), database.carritoDao())

        viewModelFactory = ViewModelFactory(
            discoRepository, usuarioRepository, carritoRepository, pedidoRepository, userPreferences
        )
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityChanged = { discoId, newQuantity ->
                viewModel.updateQuantity(discoId, newQuantity)
            },
            onRemoveClick = { discoId ->
                viewModel.removeFromCart(discoId)
            }
        )

        binding.rvCartItems.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        viewModel.carritoItems.observe(viewLifecycleOwner) { items ->
            cartAdapter.submitList(items)

            val isEmpty = items.isEmpty()
            binding.layoutEmptyCart.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.layoutCartContent.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }

        viewModel.totalCarrito.observe(viewLifecycleOwner) { total ->
            val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("es", "MX"))
            binding.tvTotal.text = "Total: ${formatter.format(total)}"
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnCheckout.isEnabled = !isLoading
        }

        viewModel.checkoutResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearMessages()

                if (it.contains("exitosamente")) {
                    // Navegar a la pantalla de pedidos o mostrar confirmación
                    findNavController().navigate(R.id.action_cart_to_orders)
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnCheckout.setOnClickListener {
            val action = CartFragmentDirections.actionCartToCheckout()
            findNavController().navigate(action)
        }

        binding.btnContinueShopping.setOnClickListener {
            findNavController().navigate(R.id.catalog_fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

