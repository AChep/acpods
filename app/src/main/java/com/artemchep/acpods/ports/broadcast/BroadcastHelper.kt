package com.artemchep.acpods.ports.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

internal inline fun CoroutineScope.produceIntents(
    crossinline registerReceiver: (BroadcastReceiver) -> Unit,
    crossinline unregisterReceiver: (BroadcastReceiver) -> Unit
): Channel<Intent> {
    val channel = Channel<Intent>()
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
    launch {
        suspendCancellableCoroutine<Unit> {
            registerReceiver(receiver)
        }
    }.invokeOnCompletion {
        unregisterReceiver(receiver)
    }
    return channel
}
