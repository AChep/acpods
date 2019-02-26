package com.artemchep.acpods.live

import android.content.Context
import androidx.lifecycle.LiveData
import com.artemchep.acpods.ACTION_PERMISSIONS_CHANGED
import com.artemchep.acpods.REQUIRED_PERMISSIONS
import com.artemchep.acpods.extensions.localBroadcastReceiver
import com.artemchep.acpods.live.base.LiveDataWithScope
import com.artemchep.acpods.models.Issue
import com.artemchep.acpods.ports.PermissionsPort
import com.artemchep.acpods.ports.permissions.PermissionsPortImpl
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

/**
 * @author Artem Chepurnoy
 */
class AirPodsPermissionIssueLiveData(private val context: Context) :
    LiveDataWithScope<Issue.PermissionIssue?>() {
    private val permissionPort: PermissionsPort = PermissionsPortImpl(context)

    override fun onActive() {
        super.onActive()

        updateValue()

        launch {
            localBroadcastReceiver(context) {
                addAction(ACTION_PERMISSIONS_CHANGED)
            }
                .consumeEach {
                    updateValue()
                }
        }
    }

    private fun updateValue() {
        // Get the list of missing permissions and
        // decide if it's an issue.
        val list = permissionPort.getMissingPermissions(REQUIRED_PERMISSIONS)
        val issue = list.takeUnless { it.isEmpty() }?.let(Issue::PermissionIssue)

        postValue(issue)
    }
}