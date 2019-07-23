package com.artemchep.acpods.ports.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.artemchep.acpods.utils.awaitCancel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@ExperimentalCoroutinesApi
@FlowPreview
internal inline fun flowOfBroadcastIntents(
    crossinline registerReceiver: (BroadcastReceiver) -> Unit,
    crossinline unregisterReceiver: (BroadcastReceiver) -> Unit
): Flow<Intent> = flow {
    coroutineScope {
        val channel = Channel<Intent>(Channel.RENDEZVOUS)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                launch {
                    channel.send(intent)
                }
            }
        }

        launch(Dispatchers.Main) {
            try {
                registerReceiver(receiver)
                awaitCancel()
            } finally {
                unregisterReceiver(receiver)
            }
        }

        channel.consumeEach {
            emit(it)
        }
    }
}