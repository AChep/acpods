package com.artemchep.acpods.domain.live

import android.content.Context
import com.artemchep.acpods.domain.ACTION_PERMISSIONS_CHANGED
import com.artemchep.acpods.domain.REQUIRED_PERMISSIONS
import com.artemchep.acpods.domain.injection
import com.artemchep.acpods.domain.live.base.LiveDataWithScope
import com.artemchep.acpods.domain.models.Issue
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * @author Artem Chepurnoy
 */
@FlowPreview
class AirPodsPermissionIssueLiveData(private val context: Context) :
    LiveDataWithScope<Issue.PermissionIssue?>() {
    private val permissionPort = injection.permissionsPost
    private val localBroadcastPort = injection.localBroadcastPost

    override fun onActive() {
        super.onActive()

        updateValue()

        launch {
            with(localBroadcastPort) {
                flowOfBroadcastIntents(context) {
                    addAction(ACTION_PERMISSIONS_CHANGED)
                }
            }
                .collect {
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