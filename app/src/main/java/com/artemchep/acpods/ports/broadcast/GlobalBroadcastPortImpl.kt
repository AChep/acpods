package com.artemchep.acpods.ports.broadcast

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.artemchep.acpods.base.extensions.LocalScope
import com.artemchep.acpods.ports.BroadcastPort
import kotlinx.coroutines.channels.Channel

/**
 * @author Artem Chepurnoy
 */
class GlobalBroadcastPortImpl : BroadcastPort {

    override fun send(context: Context, intent: Intent) {
        context.sendBroadcast(intent)
    }

    override suspend fun produceIntents(
        context: Context,
        builder: IntentFilter.() -> Unit
    ): Channel<Intent> {
        return LocalScope().produceIntents(
            registerReceiver = {
                val filter = IntentFilter().apply(builder)
                context.registerReceiver(it, filter)
            },
            unregisterReceiver = context::unregisterReceiver
        )
    }

}
