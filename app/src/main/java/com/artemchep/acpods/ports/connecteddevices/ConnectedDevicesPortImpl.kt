package com.artemchep.acpods.ports.connecteddevices

import android.content.Context
import com.artemchep.acpods.ports.ConnectedDevicesPort
import kotlinx.coroutines.CoroutineScope

/**
 * @author Artem Chepurnoy
 */
class ConnectedDevicesPortImpl(
    val context: Context
) : ConnectedDevicesPort {

    override fun CoroutineScope.flowOfDevices() = flowOfConnectedDevices(context)

}