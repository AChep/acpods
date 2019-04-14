package com.artemchep.acpods.domain.live

import android.bluetooth.BluetoothDevice
import com.artemchep.acpods.domain.injection
import com.artemchep.acpods.domain.live.base.LiveDataWithScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

/**
 * @author Artem Chepurnoy
 */
@FlowPreview
class ConnectedDevicesLiveData : LiveDataWithScope<List<BluetoothDevice>>() {
    private val connectedDevicesPort = injection.connectedDevicesPort

    override fun onActive() {
        super.onActive()

        launch {
            while (isActive) {
                // Try to get the connected devices channel to load; the Bluetooth or
                // something may be off.
                try {
                    coroutineScope {
                        with(connectedDevicesPort) {
                            flowOfDevices()
                        }.collect {
                            postValue(it)
                        }
                    }
                    break
                } catch (e: Exception) {
                    // Reschedule the channel.
                    delay(60 * 1000)
                }
            }
        }
    }
}