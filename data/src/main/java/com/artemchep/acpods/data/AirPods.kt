package com.artemchep.acpods.data

import com.artemchep.acpods.domain.models.AirCase
import com.artemchep.acpods.domain.models.AirPod

/**
 * @author Artem Chepurnoy
 */
data class AirPods(
    val info: Info,
    /**
     * Left AirPod if detected,
     * `null` otherwise.
     */
    val leftPod: AirPod? = null,
    /**
     * Right AirPod if detected,
     * `null` otherwise.
     */
    val rightPod: AirPod? = null,
    val case: AirCase? = null
) {

    /**
     * @author Artem Chepurnoy
     */
    data class Info(
        val name: String?,
        val address: String,
        val rssi: Int,
        val uuids: Set<String>,
        val property: Property
    )

    /**
     * @author Artem Chepurnoy
     */
    sealed class Property {
        object MyProperty : Property()
        object UnknownProperty : Property()
    }

}
