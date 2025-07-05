package com.fcterryamigos.disqueraapp.ui.fragments.detail

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.fcterryamigos.disqueraapp.R
import com.fcterryamigos.disqueraapp.data.local.database.DisqueraDatabase
import com.fcterryamigos.disqueraapp.data.local.preferences.UserPreferences
import com.fcterryamigos.disqueraapp.data.repository.*
import com.fcterryamigos.disqueraapp.databinding.FragmentDiscoDetailBinding
import com.fcterryamigos.disqueraapp.ui.fragments.ViewModelFactory
import java.text.NumberFormat
import java.util.*

class DiscoDetailFragment : Fragment() {

    private var _binding: FragmentDiscoDetailBinding? = null
    private val binding get() = _binding!!

    private val args: DiscoDetailFragmentArgs by navArgs()
    private lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: DiscoDetailViewModel by viewModels { viewModelFactory }

    private var currentQuantity = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDiscoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDependencies()
        setupObservers()
        setupClickListeners()

        // Cargar el disco
        viewModel.loadDisco(args.discoId)
    }

    private fun setupDependencies() {
        val database = DisqueraDatabase.getDatabase(requireContext())
        val userPreferences = UserPreferences(requireContext())

        val discoRepository = DiscoRepository(database.discoDao())
        val carritoRepository = CarritoRepository(database.carritoDao(), database.discoDao())

        viewModelFactory = ViewModelFactory(
            discoRepository,
            UsuarioRepository(database.usuarioDao()),
            carritoRepository,
            PedidoRepository(database.pedidoDao(), database.carritoDao()),
            userPreferences
        )
    }

    private fun setupObservers() {
        viewModel.disco.observe(viewLifecycleOwner) { disco ->
            disco?.let {
                binding.apply {
                    tvTitulo.text = it.titulo
                    tvArtista.text = it.artista
                    tvGenero.text = it.genero
                    tvDescripcion.text = it.descripcion
                    tvFechaLanzamiento.text = "Lanzamiento: ${it.fechaLanzamiento}"
                    tvDuracion.text = "Duración: ${it.duracion}"
                    tvSello.text = "Sello: ${it.sello}"
                    tvStock.text = "Stock disponible: ${it.stock}"

                    val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
                    tvPrecio.text = formatter.format(it.precio)

                    // Cargar imagen
                    Glide.with(ivDiscoImage.context)
                        .load(it.imagenUrl)
                        .placeholder(R.drawable.ic_vinyl)
                        .error(R.drawable.ic_vinyl)
                        .centerCrop()
                        .into(ivDiscoImage)

                    // Configurar disponibilidad
                    btnAddToCart.isEnabled = it.stock > 0
                    btnDecrease.isEnabled = it.stock > 0
                    btnIncrease.isEnabled = it.stock > 0

                    if (it.stock == 0) {
                        btnAddToCart.text = "Sin Stock"
                        tvStock.setTextColor(requireContext().getColor(R.color.error_color))
                    } else {
                        btnAddToCart.text = "Agregar al Carrito"
                        tvStock.setTextColor(requireContext().getColor(R.color.success_color))
                    }

                    // Configurar cantidad máxima
                    btnIncrease.setOnClickListener {
                        if (currentQuantity < it.stock) {
                            currentQuantity++
                            tvQuantity.text = currentQuantity.toString()
                            updateTotalPrice(it.precio)
                        }
                    }
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.layoutContent.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.addToCartResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearMessages()

                if (it.contains("exitosamente")) {
                    // Opcional: navegar al carrito o mostrar confirmación
                    findNavController().navigate(R.id.cart_fragment)
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnDecrease.setOnClickListener {
                if (currentQuantity > 1) {
                    currentQuantity--
                    tvQuantity.text = currentQuantity.toString()
                    viewModel.disco.value?.let { disco ->
                        updateTotalPrice(disco.precio)
                    }
                }
            }

            btnAddToCart.setOnClickListener {
                viewModel.addToCart(args.discoId, currentQuantity)
            }

            fab.setOnClickListener {
                viewModel.addToCart(args.discoId, 1)
            }
        }
    }

    private fun updateTotalPrice(precio: Double) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        val total = precio * currentQuantity
        binding.tvTotalPrice.text = "Total: ${formatter.format(total)}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// CheckoutFragment
class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: ViewModelFactory
    private val cartViewModel: CartViewModel by viewModels { viewModelFactory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDependencies()
        setupObservers()
        setupClickListeners()
        setupPaymentMethods()
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

        // Cargar información del usuario para prellenar formulario
        loadUserInfo()
    }

    private fun setupObservers() {
        cartViewModel.totalCarrito.observe(viewLifecycleOwner) { total ->
            val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
            binding.tvOrderTotal.text = formatter.format(total)

            // Calcular costos adicionales
            val shipping = if (total > 500.0) 0.0 else 50.0 // Envío gratis para pedidos > $500
            val tax = total * 0.16 // IVA 16%
            val finalTotal = total + shipping + tax

            binding.tvShippingCost.text = if (shipping == 0.0) "GRATIS" else formatter.format(shipping)
            binding.tvTaxes.text = formatter.format(tax)
            binding.tvFinalTotal.text = formatter.format(finalTotal)
        }

        cartViewModel.carritoItems.observe(viewLifecycleOwner) { items ->
            if (items.isEmpty()) {
                // Si el carrito está vacío, regresar
                Toast.makeText(requireContext(), "Tu carrito está vacío", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                // Mostrar resumen de productos
                binding.tvItemCount.text = "${items.size} producto(s)"
            }
        }

        cartViewModel.checkoutResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.contains("exitosamente")) {
                    showOrderConfirmation(it)
                } else {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
                cartViewModel.clearMessages()
            }
        }

        cartViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnPlaceOrder.isEnabled = !isLoading
        }
    }

    private fun setupClickListeners() {
        binding.btnPlaceOrder.setOnClickListener {
            processOrder()
        }

        binding.btnBackToCart.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupPaymentMethods() {
        binding.rgPaymentMethods.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_credit_card -> {
                    binding.layoutCardInfo.visibility = View.VISIBLE
                }
                R.id.rb_debit_card -> {
                    binding.layoutCardInfo.visibility = View.VISIBLE
                }
                R.id.rb_cash_on_delivery -> {
                    binding.layoutCardInfo.visibility = View.GONE
                }
                R.id.rb_bank_transfer -> {
                    binding.layoutCardInfo.visibility = View.GONE
                }
            }
        }
    }

    private fun loadUserInfo() {
        val userPreferences = UserPreferences(requireContext())
        // Prellenar formulario con información del usuario si está disponible
        // Esto se podría hacer consultando la base de datos del usuario
    }

    private fun processOrder() {
        // Validar dirección de envío
        val direccion = binding.etShippingAddress.text.toString().trim()
        if (direccion.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor ingresa la dirección de envío", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar método de pago
        val metodoPago = when (binding.rgPaymentMethods.checkedRadioButtonId) {
            R.id.rb_credit_card -> {
                if (!validateCardInfo()) return
                "Tarjeta de Crédito"
            }
            R.id.rb_debit_card -> {
                if (!validateCardInfo()) return
                "Tarjeta de Débito"
            }
            R.id.rb_cash_on_delivery -> "Pago contra entrega"
            R.id.rb_bank_transfer -> "Transferencia bancaria"
            else -> {
                Toast.makeText(requireContext(), "Por favor selecciona un método de pago", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Procesar pedido
        cartViewModel.proceedToCheckout(direccion, metodoPago)
    }

    private fun validateCardInfo(): Boolean {
        if (binding.layoutCardInfo.visibility == View.VISIBLE) {
            val cardNumber = binding.etCardNumber.text.toString().replace(" ", "")
            val expiryDate = binding.etExpiryDate.text.toString()
            val cvv = binding.etCvv.text.toString()
            val cardName = binding.etCardName.text.toString().trim()

            if (cardNumber.length != 16) {
                Toast.makeText(requireContext(), "Número de tarjeta inválido", Toast.LENGTH_SHORT).show()
                return false
            }

            if (expiryDate.length != 5 || !expiryDate.contains("/")) {
                Toast.makeText(requireContext(), "Fecha de vencimiento inválida (MM/AA)", Toast.LENGTH_SHORT).show()
                return false
            }

            if (cvv.length < 3) {
                Toast.makeText(requireContext(), "CVV inválido", Toast.LENGTH_SHORT).show()
                return false
            }

            if (cardName.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor ingresa el nombre del titular", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun showOrderConfirmation(message: String) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("¡Pedido Confirmado!")
            .setMessage(message)
            .setPositiveButton("Ver Mis Pedidos") { _, _ ->
                findNavController().navigate(R.id.orders_fragment)
            }
            .setNegativeButton("Continuar Comprando") { _, _ ->
                findNavController().navigate(R.id.catalog_fragment)
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// OrdersFragment
class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    private lateinit var pedidoAdapter: PedidoAdapter
    private lateinit var viewModelFactory: ViewModelFactory
    private val profileViewModel: ProfileViewModel by viewModels { viewModelFactory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDependencies()
        setupRecyclerView()
        setupObservers()
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
        pedidoAdapter = PedidoAdapter { pedido ->
            showOrderDetails(pedido)
        }

        binding.rvOrders.apply {
            adapter = pedidoAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        profileViewModel.pedidos.observe(viewLifecycleOwner) { pedidos ->
            pedidoAdapter.submitList(pedidos)

            binding.tvEmptyState.visibility = if (pedidos.isEmpty()) View.VISIBLE else View.GONE
            binding.rvOrders.visibility = if (pedidos.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun showOrderDetails(pedido: Pedido) {
        // TODO: Navegar a pantalla de detalle del pedido o mostrar dialog
        Toast.makeText(requireContext(), "Detalles del pedido #${pedido.id}", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}