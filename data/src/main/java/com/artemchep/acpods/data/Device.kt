package com.artemchep.acpods.data

/**
 * @author Artem Chepurnoy
 */
sealed class Device {
    /**
     * Battery level, from 0% to 100%; negative if
     * undefined.
     */
    abstract val batteryLevel: Int

    /**
     * `true` if the device is charging right now,
     * `false` otherwise.
     */
    abstract val isCharging: Boolean
}

/**
 * @author Artem Chepurnoy
 */
data class AirPod(
    override val batteryLevel: Int,
    override val isCharging: Boolean,

    /**
     * `true` if the device is in ear right now,
     * `false` otherwise.
     */
    val isInEar: Boolean,

    /**
     * `true` if the device is used as a bridge between
     * AirPods and an Android device, `false` otherwise.
     */
    val isMaster: Boolean
) : Device()

/**
 * @author Artem Chepurnoy
 */
class AirCase(
    override val batteryLevel: Int,
    override val isCharging: Boolean
) : Device()
