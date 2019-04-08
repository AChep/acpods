package com.artemchep.acpods.ports.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowViaChannel

@ExperimentalCoroutinesApi
@FlowPreview
internal inline fun CoroutineScope.flowOfBroadcastIntents(
    crossinline registerReceiver: (BroadcastReceiver) -> Unit,
    crossinline unregisterReceiver: (BroadcastReceiver) -> Unit
): Flow<Intent> = flowViaChannel { channel ->
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

    channel.invokeOnClose {
        unregisterReceiver(receiver)
    }

    // Register receiver for broadcast
    // intents.
    launch {
        suspendCancellableCoroutine<Unit> {
            registerReceiver(receiver)
        }
    }.invokeOnCompletion {
        unregisterReceiver(receiver)
    }
}
