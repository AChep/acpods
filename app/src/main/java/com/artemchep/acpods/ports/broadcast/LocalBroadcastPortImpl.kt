package com.artemchep.acpods.ports.broadcast

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.artemchep.acpods.base.extensions.LocalScope
import com.artemchep.acpods.ports.BroadcastPort
import kotlinx.coroutines.channels.Channel

/**
 * @author Artem Chepurnoy
 */
class LocalBroadcastPortImpl : BroadcastPort {

    override fun send(context: Context, intent: Intent) {
        val localBroadcastManager = LocalBroadcastManager.getInstance(context)
        localBroadcastManager.sendBroadcast(intent)
    }

    override suspend fun produceIntents(
        context: Context,
        builder: IntentFilter.() -> Unit
    ): Channel<Intent> {
        val localBroadcastManager = LocalBroadcastManager.getInstance(context)
        return LocalScope().produceIntents(
            registerReceiver = {
                val filter = IntentFilter().apply(builder)
                localBroadcastManager.registerReceiver(it, filter)
            },
            unregisterReceiver = localBroadcastManager::unregisterReceiver
        )
    }

}
