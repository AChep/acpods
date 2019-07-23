package com.artemchep.acpods.ports

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

/**
 * @author Artem Chepurnoy
 */
@FlowPreview
interface BroadcastPort {

    fun send(context: Context, intent: Intent)

    fun flowOfBroadcastIntents(
        context: Context,
        builder: IntentFilter.() -> Unit
    ): Flow<Intent>

}
