package com.artemchep.acpods.receivers

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.artemchep.acpods.base.ifDebug
import com.artemchep.acpods.extensions.info
import com.artemchep.acpods.extensions.isAirPod
import com.artemchep.acpods.extensions.sessionManager

/**
 * @author Artem Chepurnoy
 */
class ConnectedAirPodsStateReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "BtDeviceStateReceiver"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val device = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

        ifDebug {
            val msg = "Received action=`${intent?.action}` from device `${device?.info()}`"
            Log.d(TAG, msg)
        }

        if (device != null && device.isAirPod()) {
            when (intent.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> context.sessionManager.put(device)
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> context.sessionManager.remove(device)
            }
        }
    }

}