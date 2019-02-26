package com.artemchep.acpods

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.artemchep.acpods.services.ConnectedAirPodsStateService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.runBlocking

/**
 * @author Artem Chepurnoy
 */
@ExperimentalCoroutinesApi
class SessionManager internal constructor(val context: Context) {

    val channel = ConflatedBroadcastChannel<Set<String>>()

    private var addresses: Set<String> = emptySet()
        private set(value) {
            field = value

            // Control the AirPods state
            // service.
            val intent = Intent(context, ConnectedAirPodsStateService::class.java)
            if (value.isEmpty()) {
                context.stopService(intent)
            } else {
                intent.putExtra(ConnectedAirPodsStateService.EXTRA_ADDRESS, value.toTypedArray())
                ContextCompat.startForegroundService(context, intent)
            }

            // Send to a channel broadcast
            runBlocking {
                channel.send(value)
            }
        }

    @Suppress("SuspiciousCollectionReassignment")
    fun put(device: BluetoothDevice) {
        addresses += device.address
    }

    @Suppress("SuspiciousCollectionReassignment")
    fun remove(device: BluetoothDevice) {
        addresses -= device.address
    }

}
