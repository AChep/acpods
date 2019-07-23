package com.artemchep.acpods.ports.connecteddevices

import android.content.Context
import com.artemchep.acpods.ports.ConnectedDevicesPort

/**
 * @author Artem Chepurnoy
 */
class ConnectedDevicesPortImpl(
    val context: Context
) : ConnectedDevicesPort {

    override fun flowOfDevices() = flowOfConnectedDevices(context)

}