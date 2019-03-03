package com.artemchep.acpods.ui

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.artemchep.acpods.R
import com.artemchep.acpods.data.AirPods
import com.artemchep.acpods.ui.activities.MainActivity

private const val REQUEST_CODE_MAIN = 10
private const val REQUEST_CODE_PERMISSION = 20

fun Context.createAirPodsNotification(channel: String, airPods: AirPods?): Notification {
    fun batteryIconOf(batteryLevel: Int) = when {
        batteryLevel >= 95 -> R.drawable.ic_battery_bluetooth_10
        batteryLevel >= 85 -> R.drawable.ic_battery_bluetooth_9
        batteryLevel >= 75 -> R.drawable.ic_battery_bluetooth_8
        batteryLevel >= 65 -> R.drawable.ic_battery_bluetooth_7
        batteryLevel >= 55 -> R.drawable.ic_battery_bluetooth_6
        batteryLevel >= 45 -> R.drawable.ic_battery_bluetooth_5
        batteryLevel >= 35 -> R.drawable.ic_battery_bluetooth_4
        batteryLevel >= 25 -> R.drawable.ic_battery_bluetooth_3
        batteryLevel >= 15 -> R.drawable.ic_battery_bluetooth_2
        batteryLevel >= 5 -> R.drawable.ic_battery_bluetooth_1
        else -> R.drawable.ic_battery_bluetooth_0
    }

    val contentIntent = Intent(this, MainActivity::class.java)
    val contentPi = PendingIntent.getActivity(
        this,
        REQUEST_CODE_MAIN,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    return NotificationCompat.Builder(this, channel)
        .setAutoCancel(true)
        .setShowWhen(false)
        .setContentIntent(contentPi)
        .apply {
            if (airPods != null) {
                setContentTitle(getString(R.string.airpods_connected))

                val text = listOf(
                    airPods.leftPod?.batteryLevel to R.string.airpods_left_battery,
                    airPods.rightPod?.batteryLevel to R.string.airpods_right_battery,
                    airPods.case?.batteryLevel to R.string.airpods_case_battery
                ).mapNotNull { (batteryLevel, stringRes) ->
                    batteryLevel?.let { getString(stringRes, it) }
                }.joinToString().capitalize()
                setContentText(text)

                val meanBattery = listOfNotNull(
                    airPods.leftPod?.batteryLevel,
                    airPods.rightPod?.batteryLevel
                )
                    .takeUnless { it.isEmpty() }
                    ?.let { it.sum() / it.size }
                    ?: 0

                // Set the icon of this notification
                val notificationIcon = batteryIconOf(meanBattery)
                setSmallIcon(notificationIcon)
            } else {
                setContentTitle(getString(R.string.airpods_disconnected))
                setSmallIcon(R.drawable.ic_battery_bluetooth_0)
            }
        }
        .build()
}

fun Context.createMissingPermissionsNotification(channel: String): Notification {
    val contentIntent = Intent(this, MainActivity::class.java)
    val contentPi = PendingIntent.getActivity(
        this,
        REQUEST_CODE_PERMISSION,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    return NotificationCompat.Builder(this, channel)
        .setAutoCancel(true)
        .setSmallIcon(R.drawable.ic_alert)
        .setContentTitle(getString(R.string.notification_permissions_title))
        .setContentText(getString(R.string.notification_permissions_text))
        .setStyle(
            NotificationCompat.BigTextStyle()
                .setBigContentTitle(getString(R.string.notification_permissions_title))
                .bigText(getString(R.string.notification_permissions_text))
        )
        .setContentIntent(contentPi)
        .build()
}
