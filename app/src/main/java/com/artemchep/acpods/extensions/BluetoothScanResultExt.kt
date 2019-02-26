package com.artemchep.acpods.extensions

import android.bluetooth.BluetoothAssignedNumbers
import android.bluetooth.le.ScanResult

/**
 * Manufacturer specific data with the ID of
 * the Apple company.
 */
val ScanResult.appleSpecificData
    get() = scanRecord?.getManufacturerSpecificData(BluetoothAssignedNumbers.APPLE)
