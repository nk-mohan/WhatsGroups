package com.seabird.whatsdev.utils

import android.app.Activity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.seabird.whatsdev.R
import com.seabird.whatsdev.databinding.PermissionInstructionPopupBinding
import javax.inject.Inject

class PermissionAlertDialog @Inject constructor(private var activity: Activity) {

    fun showPermissionInstructionDialog(
        permissionType: String,
        permissionDialogListener: PermissionDialogListener
    ) {
        val dialogBuilder = AlertDialog.Builder(activity, R.style.CustomAlertDialog)
        val inflater: LayoutInflater = activity.layoutInflater
        val dialogBinding = PermissionInstructionPopupBinding.inflate(inflater)
        dialogBinding.dialogIcon.setImageResource(getDialogIcon(permissionType))
        dialogBinding.dialogDescription.text = getDialogDescription(permissionType)
        dialogBuilder.apply {
            setCancelable(false)
            setView(dialogBinding.root)
            setPositiveButton(activity.getString(R.string.continue_label)) { dialog, _ ->
                dialog.dismiss()
                permissionDialogListener.onPositiveButtonClicked()
            }
            setNegativeButton(activity.getString(R.string.not_now_label)) { dialog, _ ->
                dialog.dismiss()
                permissionDialogListener.onNegativeButtonClicked()
            }
        }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        adjustAlertDialogWidth(activity, alertDialog)
    }

    private fun getDialogDescription(permissionType: String): CharSequence {
        return when (permissionType) {
            STORAGE_PERMISSION -> activity.getString(R.string.storage_permission_alert_label)
            STORAGE_PERMISSION_DENIED -> activity.getString(R.string.storage_permission_denied_alert_label)
            else -> activity.getString(R.string.storage_permission_alert_label)
        }
    }

    private fun getDialogIcon(permissionType: String): Int {
        return when(permissionType) {
            STORAGE_PERMISSION, STORAGE_PERMISSION_DENIED -> R.drawable.ic_storage_permission_popup
            else -> R.drawable.ic_menu_category
        }
    }

    private fun adjustAlertDialogWidth(activity: Activity, alertDialog: AlertDialog) {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alertDialog.window!!.attributes)
        layoutParams.width = (AppUtils.getScreenWidth(activity) * 0.78).toInt()
        alertDialog.window!!.attributes = layoutParams
    }

    companion object {

        const val STORAGE_PERMISSION = "storage_permission"
        const val STORAGE_PERMISSION_DENIED = "storage_permission_denied"
    }
}