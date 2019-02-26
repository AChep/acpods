package com.artemchep.acpods.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.artemchep.acpods.extensions.createNotificationChannel
import com.artemchep.acpods.ui.createAirPodsNotification
import com.artemchep.acpods.ui.createMissingPermissionsNotification
import com.artemchep.acpods.viewmodels.AirPodsViewModel
import kotlinx.coroutines.ObsoleteCoroutinesApi

/**
 * @author Artem Chepurnoy
 */
@ObsoleteCoroutinesApi
class ConnectedAirPodsStateService : Service(), LifecycleOwner {

    companion object {
        const val EXTRA_ADDRESS = "address"

        const val NOTIFICATION_CHANNEL = "airpods-status"
        const val NOTIFICATION_ID = 111
    }

    private lateinit var viewModel: AirPodsViewModel

    private val lifecycle = LifecycleRegistry(this)

    private var notification: Notification? = null
        set(value) {
            createNotificationChannel()

            val nm = getSystemService<NotificationManager>()!!
            nm.notify(NOTIFICATION_ID, value)
        }

    private val observer = {
        val issues = viewModel.issues.value
        val airPods = viewModel.primaryAirPod.value

        notification = if (issues?.isNotEmpty() != false) {
            createMissingPermissionsNotification(NOTIFICATION_CHANNEL)
        } else {
            createAirPodsNotification(NOTIFICATION_CHANNEL, airPods)
        }
    }

    override fun onCreate() {
        lifecycle.markState(Lifecycle.State.STARTED)
        super.onCreate()

        viewModel = AirPodsViewModel.getInstance(application)
        viewModel.setup()
    }

    private fun AirPodsViewModel.setup() {
        issues.observe(this@ConnectedAirPodsStateService, Observer { observer() })
        primaryAirPod.observe(this@ConnectedAirPodsStateService, Observer { observer() })
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

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.markState(Lifecycle.State.DESTROYED)
    }

    override fun getLifecycle(): Lifecycle = lifecycle

    override fun onBind(intent: Intent?): IBinder? = null

}