package com.artemchep.acpods.ports.permissions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.artemchep.acpods.ports.PermissionsPort

/**
 * @author Artem Chepurnoy
 */
class PermissionsPortImpl(private val context: Context) : PermissionsPort {
    override fun getMissingPermissions(permissions: List<String>) =
        permissions.filterNot(::isGranted)

    private fun isGranted(permission: String): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        } else true
}
