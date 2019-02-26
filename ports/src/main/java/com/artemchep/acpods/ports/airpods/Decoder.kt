package com.artemchep.acpods.ports.airpods

import android.bluetooth.BluetoothAssignedNumbers
import android.bluetooth.le.ScanResult
import android.util.Log
import com.artemchep.acpods.domain.models.AirCase
import com.artemchep.acpods.domain.models.AirPod
import com.artemchep.acpods.data.AirPods

/**
 * @author Artem Chepurnoy
 */
internal class Decoder {

    companion object {
        const val TAG = "AirPodsDecoder"
    }

    fun decode(source: ScanResult): AirPods {
        val address = source.device.address
        val data = source.appleSpecificData
        val info = AirPods.Info(
            address = address,
            name = source.device.name,
            rssi = source.rssi,
            uuids = source.device.uuids?.map { it.uuid.toString() }?.toSet() ?: emptySet(),
            property = AirPods.Property.UnknownProperty
        )

        Log.d(TAG, "Is bond ${source.device.bondState}")

        if (data == null) {
            Log.d(TAG, "Empty manufacturer-specific data: address=$address")
            return AirPods(info)
        }

        val masterPod = decodeMasterAirPod(data)
        val slavePod = decodeSlaveAirPod(data)
        val case = decodeCase(data)

        val isLeftPodMaster = data[5].toInt() and 0b00100000 == 0
        val leftPod = if (isLeftPodMaster) masterPod else slavePod
        val rightPod = if (isLeftPodMaster) slavePod else masterPod

        return AirPods(
            info = info,
            leftPod = leftPod,
            rightPod = rightPod,
            case = case
        )
    }

    private fun decodeMasterAirPod(data: ByteArray): AirPod? {
        val batteryData = data[6].toInt() and 0b00001111
        if (batteryData > 10) {
            // The device is not present in this
            // data.
            return null
        }

        return AirPod(
            batteryLevel = batteryData * 10,
            isCharging = data[7].toInt() notContains 0b00010000,
            isInEar = true,
            isMaster = true
        )
    }

    private fun decodeSlaveAirPod(data: ByteArray): AirPod? {
        val batteryData = data[6].toInt() and 0b11110000 shr 4
        if (batteryData > 10) {
            // The device is not present in this
            // data.
            return null
        }

        return AirPod(
            batteryLevel = batteryData * 10,
            isCharging = data[7].toInt() notContains 0b00100000,
            isInEar = true,
            isMaster = false
        )
    }

    private fun decodeCase(data: ByteArray): AirCase? {
        val batteryData = data[7].toInt() and 0b00001111
        if (batteryData > 10) {
            // The device is not present in this
            // data.
            return null
        }

        return AirCase(
            batteryLevel = batteryData * 10,
            isCharging = data[7].toInt() notContains 0b01000000
        )
    }

    /**
     * Manufacturer specific data with the ID of
     * the Apple company.
     */
    private val ScanResult.appleSpecificData
        get() = scanRecord?.getManufacturerSpecificData(BluetoothAssignedNumbers.APPLE)

    private infix fun Int.notContains(mask: Int) = (this and mask) != mask

}
