package com.artemchep.acpods.extensions

import android.bluetooth.BluetoothDevice
import android.os.ParcelUuid

private val UUIDS_AIRPODS = setOf(
    ParcelUuid.fromString("74ec2172-0bad-4d01-8f77-997b2be0722a"),
    ParcelUuid.fromString("2a72e02b-7b99-778f-014d-ad0b7221ec74")
)

fun BluetoothDevice.info(): String {
    return "<BtDevice name=$name; address=$address; uuids=${uuids?.map { it.uuid }}>"
}

/**
 * Returns `true` if this bluetooth device is an
 * AirPod device, `false` otherwise.
 */
fun BluetoothDevice.isAirPod(): Boolean {
    return uuids?.find { it in UUIDS_AIRPODS } != null
}
