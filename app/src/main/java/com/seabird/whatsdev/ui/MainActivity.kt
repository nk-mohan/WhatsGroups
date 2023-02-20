package com.seabird.whatsdev.ui

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.seabird.whatsdev.R
import com.seabird.whatsdev.databinding.ActivityMainBinding
import com.seabird.whatsdev.viewmodels.StatusSaverViewModel
import com.seabird.whatsdev.ui.views.StatusSavingAlertDialog
import com.seabird.whatsdev.utils.PermissionAlertDialog
import com.seabird.whatsdev.utils.PermissionDialogListener
import com.seabird.whatsdev.utils.PermissionManager
import com.seabird.whatsdev.viewmodels.FavouriteGroupViewModel
import com.seabird.whatsdev.viewmodels.GroupViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ActivityListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var actionMode: ActionMode? = null
    private var searchMenuItem: MenuItem? = null

    private val statusSaverViewModel: StatusSaverViewModel by viewModels()
    private val favouriteGroupViewModel: FavouriteGroupViewModel by viewModels()
    private val groupViewModel: GroupViewModel by viewModels()

    /**
     * The handler to delay the search
     */
    private lateinit var mHandler: Handler

    private var canSearchShow = false

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
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        mHandler = Handler(Looper.getMainLooper())

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_category, R.id.nav_status_saver, R.id.nav_favorite_groups, R.id.nav_trending_groups
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.appBarMain.addGroup.setOnClickListener {
            navController.navigate(R.id.nav_add_group)
        }

        navView.menu.findItem(R.id.nav_share_app).setOnMenuItemClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_status_desc))
            startActivity(Intent.createChooser(intent, "Share via"))
            true
        }

        navView.menu.findItem(R.id.nav_give_star).setOnMenuItemClickListener {
            val uri: Uri = Uri.parse("market://details?id=$packageName")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")))
            }
            true
        }

        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
    }

    private fun onActionMenuClick(itemId: Int): Boolean {
        return when (itemId) {
            R.id.action_save_status -> {
                checkWritePermission()
                false
            }
            R.id.action_delete_favourite -> {
                favouriteGroupViewModel.deleteSelectedItems()
                mHandler.postDelayed({
                    actionMode?.finish()
                }, 250)
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
            setUpStatusActionMode()
        } else
            actionMode?.title = statusSaverViewModel.selectedList.size.toString()
    }

    override fun onFavouriteGroupSelected() {
        if (favouriteGroupViewModel.selectedList.isEmpty())
            actionMode?.finish()
        else if (actionMode == null) {
            setUpFavouriteActionMode()
        } else
            actionMode?.title = favouriteGroupViewModel.selectedList.size.toString()
    }

    override fun hideAddGroupAction() {
        if (::binding.isInitialized)
            binding.appBarMain.addGroup.visibility = View.GONE
    }

    override fun showAddGroupAction() {
        if (::binding.isInitialized)
            binding.appBarMain.addGroup.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_group_menu, menu)

        searchMenuItem = menu?.findItem(R.id.action_search)
        val mSearchView = menu?.findItem(R.id.action_search)?.actionView as SearchView

        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            override fun onQueryTextChange(searchString: String): Boolean {
                val searchKey = searchString.trim()
                mHandler.removeCallbacksAndMessages(null)
                mHandler.postDelayed({
                    groupViewModel.searchGroup(searchKey)
                }, 500)
                return true
            }
        })

        searchMenuItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                mSearchView.maxWidth = Integer.MAX_VALUE
                lockNavigationDrawer()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                unlockNavigationDrawer()
                return true
            }
        })

        searchMenuItem?.isVisible = canSearchShow

        if (canSearchShow)
            unlockNavigationDrawer()
        else
            lockNavigationDrawer()

        return true

    }

    override fun showSearchGroup() {
        searchMenuItem?.isVisible = true
        canSearchShow = true
    }

    override fun hideSearchGroup() {
        searchMenuItem?.isVisible = false
        canSearchShow = false
    }


    private fun setUpStatusActionMode() {
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

    private fun setUpFavouriteActionMode() {
        actionMode = binding.appBarMain.toolbar.startActionMode(object : ActionMode.Callback{
            override fun onCreateActionMode(mode: ActionMode, menu: Menu?): Boolean {
                mode.menuInflater?.inflate(R.menu.favourite_menu, menu)
                mode.title = favouriteGroupViewModel.selectedList.size.toString()
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
                favouriteGroupViewModel.clearAllData()
                actionMode = null
            }

        })
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (statusSaverViewModel.selectedList.isEmpty())
                onBackPressedDispatcher.onBackPressed()
            else {
                statusSaverViewModel.clearAllData()
                actionMode?.finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshStatusList()
    }

    fun refreshStatusList() {
        statusSaverViewModel.updateData()
    }
}