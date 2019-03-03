package com.artemchep.acpods.ports

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.channels.Channel

/**
 * @author Artem Chepurnoy
 */
interface BroadcastPort {

    fun send(context: Context, intent: Intent)

    suspend fun produceIntents(context: Context, builder: IntentFilter.() -> Unit): Channel<Intent>

}
