package com.artemchep.acpods.domain.live

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import androidx.core.content.getSystemService
import com.artemchep.acpods.domain.injection
import com.artemchep.acpods.domain.live.base.LiveDataWithScope
import com.artemchep.acpods.domain.models.Issue
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.launch

/**
 * @author Artem Chepurnoy
 */
class AirPodsScreenIssueLiveData(private val context: Context) :
    LiveDataWithScope<Issue.ScreenIssue?>() {
    private val globalBroadcastPost = injection.globalBroadcastPost

    override fun onActive() {
        super.onActive()

        // Update current screen state
        val powerManager = context.getSystemService<PowerManager>()!!
        val isScreenOn = powerManager.isInteractive
        postValue(isScreenOn.asIssue())

        // Observe future screen changes
        launch {
            globalBroadcastPost.produceIntents(context) {
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_SCREEN_ON)
                priority = IntentFilter.SYSTEM_HIGH_PRIORITY - 1
            }
                .map { it.action == Intent.ACTION_SCREEN_ON }
                .consumeEach {
                    postValue(it.asIssue())
                }
        }
    }

    private fun Boolean.asIssue() = if (this) null else Issue.ScreenIssue
}
