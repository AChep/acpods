package com.artemchep.acpods.domain.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.artemchep.acpods.data.AirPods
import com.artemchep.acpods.domain.ACTION_PERMISSIONS_CHANGED
import com.artemchep.acpods.domain.injection
import com.artemchep.acpods.domain.live.*
import com.artemchep.acpods.domain.models.Issue

/**
 * @author Artem Chepurnoy
 */
class AirPodsViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private lateinit var instance: AirPodsViewModel

        // It's only possible cause this view model
        // doesn't have internal scope.
        fun getInstance(application: Application) =
            if (::instance.isInitialized) {
                instance
            } else {
                AirPodsViewModel(application)
                    .also {
                        instance = it
                    }
            }
    }

    val issuePermissions = AirPodsPermissionIssueLiveData(application)

    val issueBluetooth = AirPodsBluetoothIssueLiveData(application)

    val issueScreen = AirPodsScreenIssueLiveData(application)

    /** Merged [issuePermissions], [issueScreen] and [issueBluetooth] values */
    val issues = AirPodsIssueLiveData(issueBluetooth, issuePermissions, issueScreen)

    private val internalAirPods = AirPodsLiveData()

    /**
     * The live data that emits lists of visible AirPod devices
     * nearby. The live data only subscribes if there's no active
     * [issues].
     */
    val airPods = object : MediatorLiveData<List<AirPods>>() {
        private val airPodsObserver = Observer { airPods: List<AirPods> ->
            postValue(airPods)
        }

        init {
            addSource(issues) { list ->
                val wasObserving = internalAirPods.hasObservers()
                val shouldObserveAirPods = list.isEmpty()
                if (shouldObserveAirPods) {
                    // Subscribe to air-pods channel if we
                    // wasn't subscribed before.
                    if (!wasObserving) {
                        internalAirPods.observeForever(airPodsObserver)
                    }
                } else {
                    internalAirPods.removeObserver(airPodsObserver)

                    // Clean-up saved data
                    val airPods = emptyList<AirPods>()
                    postValue(airPods)
                }
            }
        }

        override fun onInactive() {
            super.onInactive()
            internalAirPods.removeObserver(airPodsObserver)
        }
    }

    val connectedDevicesLiveData = ConnectedDevicesLiveData()

    val primaryAirPod = MediatorLiveData<AirPods?>()
        .apply {
            val resolver = { _: Any? ->
                val airPodsConnected = !connectedDevicesLiveData.value.isNullOrEmpty()
                val airPods = if (airPodsConnected) {
                    airPods.value?.firstOrNull()
                } else {
                    null
                }

                postValue(airPods)
            }

            addSource(airPods, resolver)
            addSource(connectedDevicesLiveData, resolver)
        }

    val primaryAirPodAndIssues = MediatorLiveData<Pair<AirPods?, List<Issue>>>()
        .apply {
            val resolver = { _: Any? ->
                val airPods = primaryAirPod.value
                val issues = issues.value ?: emptyList()
                postValue(airPods to issues)
            }

            addSource(primaryAirPod, resolver)
            addSource(issues, resolver)
        }

    /**
     * Notifies the domain that runtime permissions may have
     * changed.
     */
    fun notifyPermissionsChanged() {
        val context: Context = getApplication()
        val localBroadcastPort = injection.localBroadcastPost
        localBroadcastPort.send(context, Intent(ACTION_PERMISSIONS_CHANGED))
    }

}
