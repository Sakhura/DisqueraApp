package com.fcterryamigos.disqueraapp.ui.fragments.home

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
import com.fcterryamigos.disqueraapp.ui.fragments.profile.ViewModelFactory


// HomeFragment
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var featuredAdapter: DiscoAdapter
    private lateinit var viewModelFactory: ViewModelFactory
    private val catalogViewModel: CatalogViewModel by viewModels { viewModelFactory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDependencies()
        setupFeaturedRecyclerView()
        setupObservers()
        setupClickListeners()
        loadWelcomeMessage()
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

    private fun setupFeaturedRecyclerView() {
        featuredAdapter = DiscoAdapter(
            onDiscoClick = { disco ->
                val action = HomeFragmentDirections.actionHomeToDetail(disco.id)
                findNavController().navigate(action)
            },
            onAddToCartClick = { disco ->
                Toast.makeText(requireContext(), "Agregado al carrito: ${disco.titulo}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvFeaturedDiscos.apply {
            adapter = featuredAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        catalogViewModel.discos.observe(viewLifecycleOwner) { discos ->
            // Mostrar solo los primeros 5 discos como destacados
            featuredAdapter.submitList(discos.take(5))
        }
    }

    private fun setupClickListeners() {
        binding.btnViewCatalog.setOnClickListener {
            findNavController().navigate(R.id.catalog_fragment)
        }

        binding.btnViewCart.setOnClickListener {
            findNavController().navigate(R.id.cart_fragment)
        }
    }

    private fun loadWelcomeMessage() {
        val userPreferences = UserPreferences(requireContext())
        val userName = userPreferences.userName ?: "Usuario"
        binding.tvWelcome.text = "¡Hola, $userName!"

        // Mensaje dinámico basado en la hora
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 6..11 -> "Buenos días"
            in 12..17 -> "Buenas tardes"
            else -> "Buenas noches"
        }
        binding.tvGreeting.text = "$greeting, descubre los mejores vinilos"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}