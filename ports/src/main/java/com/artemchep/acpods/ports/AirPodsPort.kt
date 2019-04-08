package com.artemchep.acpods.ports

import com.artemchep.acpods.data.AirPods
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

/**
 * @author Artem Chepurnoy
 */
@FlowPreview
interface AirPodsPort {

    fun CoroutineScope.flowOfAirPods(): Flow<List<AirPods>>

}
