package com.artemchep.acpods.ports.broadcast

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.artemchep.acpods.ports.BroadcastPort
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

/**
 * @author Artem Chepurnoy
 */
@FlowPreview
class LocalBroadcastPortImpl : BroadcastPort {

    override fun send(context: Context, intent: Intent) {
        val localBroadcastManager = LocalBroadcastManager.getInstance(context)
        localBroadcastManager.sendBroadcast(intent)
    }

    @ExperimentalCoroutinesApi
    override fun flowOfBroadcastIntents(
        context: Context,
        builder: IntentFilter.() -> Unit
    ): Flow<Intent> {
        val localBroadcastManager = LocalBroadcastManager.getInstance(context)
        return flowOfBroadcastIntents(
            registerReceiver = {
                val filter = IntentFilter().apply(builder)
                localBroadcastManager.registerReceiver(it, filter)
            },
            unregisterReceiver = localBroadcastManager::unregisterReceiver
        )
    }

}
