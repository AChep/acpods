package com.artemchep.acpods.extensions

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Wrapper for an Android broadcast receiver to
 * Kotlin's channel.
 */
fun CoroutineScope.broadcastReceiver(
    context: Context,
    builder: IntentFilter.() -> Unit
): Channel<Intent> {
    return internalBroadcastReceiver(
        registerReceiver = {
            val filter = IntentFilter().apply(builder)
            context.registerReceiver(it, filter)
        },
        unregisterReceiver = context::unregisterReceiver
    )
}

/**
 * Wrapper for an Android local broadcast receiver to
 * Kotlin's channel.
 */
fun CoroutineScope.localBroadcastReceiver(
    context: Context,
    builder: IntentFilter.() -> Unit
): Channel<Intent> {
    val localBroadcastManager = LocalBroadcastManager.getInstance(context)
    return internalBroadcastReceiver(
        registerReceiver = {
            val filter = IntentFilter().apply(builder)
            localBroadcastManager.registerReceiver(it, filter)
        },
        unregisterReceiver = localBroadcastManager::unregisterReceiver
    )
}

private inline fun CoroutineScope.internalBroadcastReceiver(
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
