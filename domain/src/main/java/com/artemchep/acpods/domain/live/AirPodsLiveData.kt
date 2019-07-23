package com.artemchep.acpods.domain.live

import com.artemchep.acpods.data.AirPods
import com.artemchep.acpods.domain.injection
import com.artemchep.acpods.domain.live.base.LiveDataWithScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * @author Artem Chepurnoy
 */
@FlowPreview
class AirPodsLiveData : LiveDataWithScope<List<AirPods>>() {
    private val airPodsPort = injection.airPodsPort

    override fun onActive() {
        super.onActive()

        launch {
            while (isActive) {
                // Try to get the AirPods channel to load; the Bluetooth or
                // something may be off.
                try {
                    airPodsPort.flowOfAirPods()
                        .collect {
                            postValue(it)
                        }
                    break
                } catch (e: Exception) {
                    // Reschedule the AirPods channel after a
                    // small delay.
                    delay(60 * 1000)
                }
            }
        }
    }
}