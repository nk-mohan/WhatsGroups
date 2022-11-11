package com.seabird.whatsdev.ui

import android.Manifest
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.seabird.whatsdev.R
import com.seabird.whatsdev.databinding.ActivityMainBinding
import com.seabird.whatsdev.ui.statussaver.StatusSaverViewModel
import com.seabird.whatsdev.ui.statussaver.StatusSavingAlertDialog
import com.seabird.whatsdev.utils.PermissionAlertDialog
import com.seabird.whatsdev.utils.PermissionDialogListener
import com.seabird.whatsdev.utils.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ActivityListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var actionMode: ActionMode? = null

    private val statusSaverViewModel: StatusSaverViewModel by viewModels()

    @Inject
    lateinit var permissionAlertDialog: PermissionAlertDialog

    @Inject
    lateinit var statusAlertDialog: StatusSavingAlertDialog

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: PermissionManager.isReadFilePermissionAllowed(this)
        if (readPermissionGranted) {
            performClickAction()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_category, R.id.nav_status_saver
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun setUpActionMode() {
        actionMode = binding.appBarMain.toolbar.startActionMode(object : ActionMode.Callback{
            override fun onCreateActionMode(mode: ActionMode, menu: Menu?): Boolean {
                mode.menuInflater?.inflate(R.menu.save_status_menu, menu)
                mode.title = statusSaverViewModel.selectedList.size.toString()
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem): Boolean {
                return onActionMenuClick(item.itemId)
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                mode?.finish()
                statusSaverViewModel.clearAllData()
                actionMode = null
            }

        })
    }

    private fun onActionMenuClick(itemId: Int): Boolean {
        return when (itemId) {
            R.id.action_save_status -> {
                checkWritePermission()
                false
            }
            else -> false
        }
    }

    private fun checkWritePermission() {
        if (PermissionManager.isReadFilePermissionAllowed(this)
            && PermissionManager.isWriteFilePermissionAllowed(this)) {
            performClickAction()
        } else {
            PermissionManager.requestStoragePermission(this, permissionAlertDialog, storagePermissionLauncher, object :
                PermissionDialogListener {
                override fun onPositiveButtonClicked() {
                    //Not Needed
                }

                override fun onNegativeButtonClicked() {
                    // Not needed
                }
            })
        }
    }

    private fun performClickAction() {
        statusAlertDialog.saveStatuses(statusSaverViewModel.selectedList)
        statusSaverViewModel.clearAllData()
        actionMode?.finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun lockNavigationDrawer() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun unlockNavigationDrawer() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun onStatusItemSelected() {
        if (statusSaverViewModel.selectedList.isEmpty())
            actionMode?.finish()
        else if (actionMode == null) {
            setUpActionMode()
        } else
            actionMode?.title = statusSaverViewModel.selectedList.size.toString()
    }

    override fun onBackPressed() {
        if (statusSaverViewModel.selectedList.isEmpty())
            onBackPressedDispatcher.onBackPressed()
        else {
            statusSaverViewModel.clearAllData()
            actionMode?.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        statusSaverViewModel.updateData()
    }
}