package com.artemchep.acpods.extensions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService
import com.artemchep.acpods.Heart
import com.artemchep.acpods.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi

private val Context.heart: Heart
    get() = applicationContext as Heart

/**
 * The instance of a session manager
 * of this app.
 */
@ExperimentalCoroutinesApi
val Context.sessionManager: SessionManager
    get() = heart.sessionManager

//
// Notifications
//

inline fun Context.createNotificationChannel(block: Context.() -> NotificationChannel) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val nm = getSystemService<NotificationManager>()!!
        nm.createNotificationChannel(block())
    }
}
