package com.artemchep.acpods.ports

import com.artemchep.acpods.data.AirPods
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

/**
 * @author Artem Chepurnoy
 */
@FlowPreview
interface AirPodsPort {

    fun flowOfAirPods(): Flow<List<AirPods>>

}
