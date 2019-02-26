package com.artemchep.acpods.models

/**
 * @author Artem Chepurnoy
 */
sealed class Issue {
    data class PermissionIssue(val permissions: List<String>) : Issue()
    object BluetoothIssue : Issue()
    object ScreenIssue : Issue()
}