package com.seabird.whatsdev.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionManager {

    fun requestStoragePermission(
        activity: Activity,
        permissionAlertDialog: PermissionAlertDialog,
        permissionsLauncher: ActivityResultLauncher<Array<String>>,
        permissionDialogListener: PermissionDialogListener
    ) {
        val hasReadPermission = isPermissionAllowed(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
        val hasWritePermission = isPermissionAllowed(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val minSdk29 = Build.VERSION.SDK_INT > Build.VERSION_CODES.P

        val writePermissionGranted = hasWritePermission || minSdk29

        val permissionsToRequest = mutableListOf<String>()
        if (!writePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!hasReadPermission) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionsToRequest.isNotEmpty()) {
            when {
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showPermissionPopUpForStorage(
                        permissionsLauncher,
                        permissionsToRequest,
                        permissionAlertDialog,
                        permissionDialogListener
                    )
                }
                SharedPreferenceManager.getBooleanValue(AppConstants.STORAGE_PERMISSION_ASKED) -> {
                    permissionAlertDialog.showPermissionInstructionDialog(PermissionAlertDialog.STORAGE_PERMISSION_DENIED,
                        object : PermissionDialogListener {
                            override fun onPositiveButtonClicked() {
                                openSettingsForPermission(activity)
                            }

                            override fun onNegativeButtonClicked() {
                                permissionDialogListener.onNegativeButtonClicked()
                            }
                        })
                }
                else -> {
                    showPermissionPopUpForStorage(permissionsLauncher, permissionsToRequest, permissionAlertDialog, permissionDialogListener)
                }
            }
        }
    }

    private fun showPermissionPopUpForStorage(
        permissionsLauncher: ActivityResultLauncher<Array<String>>,
        permissionsToRequest: MutableList<String>,
        permissionAlertDialog: PermissionAlertDialog,
        permissionDialogListener: PermissionDialogListener
    ) {
        permissionAlertDialog.showPermissionInstructionDialog(PermissionAlertDialog.STORAGE_PERMISSION,
            object : PermissionDialogListener {
                override fun onPositiveButtonClicked() {
                    SharedPreferenceManager.setBooleanValue(AppConstants.STORAGE_PERMISSION_ASKED, true)
                    permissionsLauncher.launch(permissionsToRequest.toTypedArray())
                }

                override fun onNegativeButtonClicked() {
                    permissionDialogListener.onNegativeButtonClicked()
                }
            })
    }

    private fun openSettingsForPermission(activity: Activity) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", activity.packageName, null)
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        activity.startActivity(intent)
    }

    /**
     * Calling this method to check the permission status
     *
     * @param context    Context of the activity
     * @param permission Permission to ask
     * @return boolean True if grand the permission
     */
    fun isPermissionAllowed(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun isReadFilePermissionAllowed(context: Context): Boolean {
        return isPermissionAllowed(context, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    fun isWriteFilePermissionAllowed(context: Context): Boolean {
        val minSdk29 = Build.VERSION.SDK_INT > Build.VERSION_CODES.P
        return minSdk29 || isPermissionAllowed(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}