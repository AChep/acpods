package com.artemchep.acpods.domain

import com.artemchep.acpods.ports.AirPodsPort
import com.artemchep.acpods.ports.BroadcastPort
import com.artemchep.acpods.ports.ConnectedDevicesPort
import com.artemchep.acpods.ports.PermissionsPort

lateinit var injection: Injection

/**
 * @author Artem Chepurnoy
 */
interface Injection {
    val airPodsPort: AirPodsPort
    val connectedDevicesPort: ConnectedDevicesPort
    val permissionsPost: PermissionsPort
    val globalBroadcastPost: BroadcastPort
    val localBroadcastPost: BroadcastPort
}
