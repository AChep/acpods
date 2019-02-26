package com.artemchep.acpods.ports

/**
 * @author Artem Chepurnoy
 */
interface PermissionsPort {

    fun getMissingPermissions(permissions: List<String>): List<String>

}
