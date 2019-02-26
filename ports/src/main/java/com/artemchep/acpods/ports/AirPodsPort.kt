package com.artemchep.acpods.ports

import com.artemchep.acpods.data.AirPods
import kotlinx.coroutines.channels.Channel

/**
 * @author Artem Chepurnoy
 */
interface AirPodsPort {

    suspend fun produceAirPods(): Channel<List<AirPods>>

}
