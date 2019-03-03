package com.artemchep.acpods.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.content.getSystemService
import androidx.lifecycle.Observer
import com.artemchep.acpods.domain.viewmodels.AirPodsViewModel
import com.artemchep.acpods.extensions.createNotificationChannel
import com.artemchep.acpods.services.base.LifecycleAwareService
import com.artemchep.acpods.ui.createAirPodsNotification
import com.artemchep.acpods.ui.createMissingPermissionsNotification
import kotlinx.coroutines.ObsoleteCoroutinesApi

/**
 * @author Artem Chepurnoy
 */
@ObsoleteCoroutinesApi
class ConnectedAirPodsStateService : LifecycleAwareService() {

    companion object {
        const val EXTRA_ADDRESS = "address"

        const val NOTIFICATION_CHANNEL = "airpods-status"
        const val NOTIFICATION_ID = 111
    }

    private lateinit var viewModel: AirPodsViewModel

    private var notification: Notification? = null
        set(value) {
            createNotificationChannel()

            val nm = getSystemService<NotificationManager>()!!
            nm.notify(NOTIFICATION_ID, value)
        }

    override fun onCreate() {
        super.onCreate()

        viewModel = AirPodsViewModel.getInstance(application)
        viewModel.setup()
    }

    private fun AirPodsViewModel.setup() {
        val lifecycleOwner = this@ConnectedAirPodsStateService
        primaryAirPodAndIssues.observe(lifecycleOwner, Observer { (airPods, issues) ->
            notification = if (issues.isNotEmpty()) {
                createMissingPermissionsNotification(NOTIFICATION_CHANNEL)
            } else {
                createAirPodsNotification(NOTIFICATION_CHANNEL, airPods)
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            NOTIFICATION_ID,
            notification
                ?: createAirPodsNotification(NOTIFICATION_CHANNEL, null)
        )
        return Service.START_REDELIVER_INTENT
    }

    private fun createNotificationChannel() = createNotificationChannel {
        NotificationChannel(
            NOTIFICATION_CHANNEL,
            "TODO",
            NotificationManager.IMPORTANCE_LOW
        )
    }

    override fun onBind(intent: Intent?): IBinder? = null

}