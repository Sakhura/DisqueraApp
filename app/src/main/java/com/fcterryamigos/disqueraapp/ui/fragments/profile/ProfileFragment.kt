package com.fcterryamigos.disqueraapp.ui.fragments.profile

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

// ProfileFragment
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: ProfileViewModel by viewModels { viewModelFactory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDependencies()
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

    private fun setupObservers() {
        viewModel.usuario.observe(viewLifecycleOwner) { usuario ->
            usuario?.let {
                binding.etNombre.setText(it.nombre)
                binding.etApellido.setText(it.apellido)
                binding.etEmail.setText(it.email)
                binding.etTelefono.setText(it.telefono ?: "")
                binding.etDireccion.setText(it.direccion ?: "")
                binding.etCiudad.setText(it.ciudad ?: "")
                binding.etCodigoPostal.setText(it.codigoPostal ?: "")
            }
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearMessages()
            }
        }

        viewModel.pedidos.observe(viewLifecycleOwner) { pedidos ->
            binding.tvOrderCount.text = "Tienes ${pedidos.size} pedidos realizados"
        }
    }

    private fun setupClickListeners() {
        binding.btnSaveProfile.setOnClickListener {
            updateProfile()
        }

        binding.btnViewOrders.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileToOrders()
            findNavController().navigate(action)
        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun updateProfile() {
        val currentUser = viewModel.usuario.value ?: return

        val updatedUser = currentUser.copy(
            nombre = binding.etNombre.text.toString().trim(),
            apellido = binding.etApellido.text.toString().trim(),
            email = binding.etEmail.text.toString().trim(),
            telefono = binding.etTelefono.text.toString().trim().takeIf { it.isNotEmpty() },
            direccion = binding.etDireccion.text.toString().trim().takeIf { it.isNotEmpty() },
            ciudad = binding.etCiudad.text.toString().trim().takeIf { it.isNotEmpty() },
            codigoPostal = binding.etCodigoPostal.text.toString().trim().takeIf { it.isNotEmpty() }
        )

        if (updatedUser.nombre.isEmpty() || updatedUser.apellido.isEmpty() || updatedUser.email.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor completa los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(updatedUser.email).matches()) {
            Toast.makeText(requireContext(), "Por favor ingresa un email válido", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.updateProfile(updatedUser)
    }

    private fun showLogoutConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que quieres cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                viewModel.logout()

                // Navegar al LoginActivity
                val intent = android.content.Intent(requireContext(), com.tuempresa.disqueraapp.ui.activities.LoginActivity::class.java)
                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
