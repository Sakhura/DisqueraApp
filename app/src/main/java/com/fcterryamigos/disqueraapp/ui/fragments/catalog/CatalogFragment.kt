package com.fcterryamigos.disqueraapp.ui.fragments.catalog

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fcterryamigos.disqueraapp.R
import com.fcterryamigos.disqueraapp.data.local.database.DisqueraDatabase
import com.fcterryamigos.disqueraapp.data.local.preferences.UserPreferences
import com.fcterryamigos.disqueraapp.data.repository.*
import com.fcterryamigos.disqueraapp.databinding.FragmentCatalogBinding
import com.fcterryamigos.disqueraapp.ui.adapters.DiscoAdapter
import com.fcterryamigos.disqueraapp.ui.fragments.ViewModelFactory

class CatalogFragment : Fragment(), MenuProvider {

    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!

    private lateinit var discoAdapter: DiscoAdapter
    private lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: CatalogViewModel by viewModels { viewModelFactory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDependencies()
        setupRecyclerView()
        setupSearchView()
        setupObservers()
        setupSwipeRefresh()
        setupMenu()
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
        discoAdapter = DiscoAdapter(
            onDiscoClick = { disco ->
                val action = CatalogFragmentDirections.actionCatalogToDetail(disco.id)
                findNavController().navigate(action)
            },
            onAddToCartClick = { disco ->
                // Agregar al carrito usando el ViewModel del carrito
                Toast.makeText(requireContext(), "Agregado al carrito: ${disco.titulo}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvDiscos.apply {
            adapter = discoAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
        }
    }

    private fun setupSearchView() {
        binding.etSearch.setOnEditorActionListener { _, _, _ ->
            val query = binding.etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                viewModel.searchDiscos(query)
            } else {
                viewModel.clearFilters()
            }
            true
        }

        binding.btnFilter.setOnClickListener {
            // TODO: Mostrar dialog de filtros
            showFilterDialog()
        }
    }

    private fun setupObservers() {
        viewModel.discos.observe(viewLifecycleOwner) { discos ->
            discoAdapter.submitList(discos)
            binding.tvEmptyState.visibility = if (discos.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.filteredDiscos.observe(viewLifecycleOwner) { discos ->
            if (discos.isNotEmpty()) {
                discoAdapter.submitList(discos)
                binding.tvEmptyState.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.clearFilters()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun showFilterDialog() {
        // TODO: Implementar dialog de filtros
        Toast.makeText(requireContext(), "Filtros próximamente", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.catalog_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.sort_by_title -> {
                // TODO: Implementar ordenamiento
                true
            }
            R.id.sort_by_artist -> {
                // TODO: Implementar ordenamiento
                true
            }
            R.id.sort_by_price_asc -> {
                // TODO: Implementar ordenamiento
                true
            }
            R.id.sort_by_price_desc -> {
                // TODO: Implementar ordenamiento
                true
            }
            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

