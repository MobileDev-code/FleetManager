package com.example.fleetmanager.ui.view.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fleetmanager.data.repositories.FleetShipmentsRepository
import com.example.fleetmanager.databinding.FragmentHomeBinding
import com.example.fleetmanager.ui.home.adapters.DriversRecyclerAdapter
import com.example.fleetmanager.ui.view.viewmodel.HomeViewModel
import com.example.fleetmanager.ui.view.viewmodel.LoadingState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private lateinit var viewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: DriversRecyclerAdapter
    @Inject
    lateinit var repository: FleetShipmentsRepository

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.loadingStateLiveData.observe(viewLifecycleOwner) { loadingState ->
            when {
                loadingState == LoadingState.ERROR -> {
                    val alertDialogBuilder = AlertDialog.Builder(requireContext())
                    alertDialogBuilder
                        .setTitle("Something went wrong")
                        .setMessage("Oops. Looks like something went wrong.")
                        .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                        }
                    alertDialogBuilder.create().show()
                }
            }
        }
        viewModel.driversLiveData.observe(viewLifecycleOwner) { drivers ->
            // create adapter with listener
            adapter = DriversRecyclerAdapter(drivers) { driver ->
                val shipmentAddress = viewModel.getAssignedShipment(driver)
                if (!shipmentAddress.isEmpty()) {
                    val alertDialogBuilder = AlertDialog.Builder(requireContext())
                    val message = "Driver: ${driver}\n${shipmentAddress}"
                    alertDialogBuilder
                        .setTitle("Shipment assigned")
                        .setMessage(message)
                        .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                        }
                    alertDialogBuilder.create().show()
                } else {
                    val alertDialogBuilder = AlertDialog.Builder(requireContext())
                    alertDialogBuilder
                        .setTitle("No shipments available")
                        .setMessage("No shipments available for driver.")
                        .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                        }
                    alertDialogBuilder.create().show()
                }
            }
            binding.driversRecyclerview.adapter = adapter
        }
        viewModel.getDrivers()

        linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.driversRecyclerview.layoutManager = linearLayoutManager

        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}