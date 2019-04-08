package com.artemchep.acpods.ports.broadcast

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.artemchep.acpods.ports.BroadcastPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

/**
 * @author Artem Chepurnoy
 */
@FlowPreview
class GlobalBroadcastPortImpl : BroadcastPort {

    override fun send(context: Context, intent: Intent) {
        context.sendBroadcast(intent)
    }

    @ExperimentalCoroutinesApi
    override fun CoroutineScope.flowOfBroadcastIntents(
        context: Context,
        builder: IntentFilter.() -> Unit
    ): Flow<Intent> {
        return flowOfBroadcastIntents(
            registerReceiver = {
                val filter = IntentFilter().apply(builder)
                context.registerReceiver(it, filter)
            },
            unregisterReceiver = context::unregisterReceiver
        )
    }

}
