package com.example.fleetmanager

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.fleetmanager.databinding.ActivityMainBinding
import com.example.fleetmanager.ui.view.viewmodel.LoadingState
import com.example.fleetmanager.ui.view.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val viewModel: MainViewModel by viewModels()
        viewModel.loadingStateLiveData.observe(this) { loadingState ->
            when {
                loadingState == LoadingState.ERROR -> {
                println("Failed to assign shipments to drivers")
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder
                    .setTitle("Something went wrong")
                    .setMessage("Oops. Looks like something went wrong.")
                    .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                    }
                alertDialogBuilder.create().show()
                }
            }
        }
        viewModel.assignmentsLiveData.observe(this) {
            println("Assigned shipments to drivers")
            for ((driver, destination) in it) println("Driver ${driver} -> Destination ${destination}")
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder
                .setTitle("Shipments assigned")
                .setMessage("Shipments have been auto-assigned to drivers")
                .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                }
            alertDialogBuilder.create().show()
        }
        viewModel.assignShipmentsToDrivers()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}