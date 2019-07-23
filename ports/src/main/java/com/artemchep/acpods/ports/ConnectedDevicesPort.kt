package com.artemchep.acpods.ports

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

/**
 * @author Artem Chepurnoy
 */
@FlowPreview
interface ConnectedDevicesPort {

    fun flowOfDevices(): Flow<List<BluetoothDevice>>

}
