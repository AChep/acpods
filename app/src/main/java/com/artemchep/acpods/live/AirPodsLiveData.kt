package com.artemchep.acpods.live

import android.content.Context
import com.artemchep.acpods.data.AirPods
import com.artemchep.acpods.live.base.LiveDataWithScope
import com.artemchep.acpods.ports.AirPodsPort
import com.artemchep.acpods.ports.airpods.AirPodsPortImpl
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * @author Artem Chepurnoy
 */
class AirPodsLiveData(context: Context) : LiveDataWithScope<List<AirPods>>() {
    private val airPodsPort: AirPodsPort = AirPodsPortImpl(context)

    override fun onActive() {
        super.onActive()

        launch {
            lateinit var channel: Channel<List<AirPods>>
            while (isActive) {
                // Try to get the AirPods channel to load; the Bluetooth or
                // something may be off.
                try {
                    channel = airPodsPort.produceAirPods()
                    break
                } catch (e: Exception) {
                    // Reschedule the AirPods channel after a
                    // small delay.
                    delay(60 * 1000)
                }
            }

            channel.consumeEach(::postValue)
        }
    }
}