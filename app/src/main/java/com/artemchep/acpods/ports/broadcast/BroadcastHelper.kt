package com.artemchep.acpods.ports.broadcast

import android.content.BroadcastReceiver
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowViaChannel

@ExperimentalCoroutinesApi
@FlowPreview
internal inline fun CoroutineScope.flowOfBroadcastIntents(
    crossinline registerReceiver: (BroadcastReceiver) -> Unit,
    crossinline unregisterReceiver: (BroadcastReceiver) -> Unit
): Flow<Intent> = flowViaChannel { channel ->
    /*
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            launch {
                try {
                    channel.send(intent)
                } catch (e: ClosedSendChannelException) {
                    com.artemchep.acpods.base.ifDebug {
                        throw RuntimeException(e)
                    }
                }
            }
        }
    }

    val unregister = {
        try {
            unregisterReceiver(receiver)
        } catch (_: IllegalArgumentException) {
        }
    }

    channel.invokeOnClose {
        unregister()
    }

    // Register receiver for broadcast
    // intents.
    launch {
        suspendCancellableCoroutine<Unit> {
            registerReceiver(receiver)
        }
    }.invokeOnCompletion {
        unregister()
    }
     */
}
