package com.artemchep.acpods.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.artemchep.acpods.data.AirPods
import com.artemchep.acpods.live.*

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

    private val airPods = AirPodsLiveData(application)

    /**
     * The live data that emits lists of visible AirPod devices
     * nearby. The live data only subscribes if there's no active
     * [issues].
     */
    val nearbyAirPods = object : MediatorLiveData<List<AirPods>>() {
        private val airPodsObserver = Observer { airPods: List<AirPods> ->
            postValue(airPods)
        }

        init {
            addSource(issues) { list ->
                val wasObserving = airPods.hasObservers()
                val shouldObserveAirPods = list.isEmpty()
                if (shouldObserveAirPods) {
                    // Subscribe to air-pods channel if we
                    // wasn't subscribed before.
                    if (!wasObserving) {
                        airPods.observeForever(airPodsObserver)
                    }
                } else {
                    airPods.removeObserver(airPodsObserver)

                    // Clean-up saved data
                    val airPods = emptyList<AirPods>()
                    postValue(airPods)
                }
            }
        }

        override fun onInactive() {
            super.onInactive()
            airPods.removeObserver(airPodsObserver)
        }
    }

    val primaryAirPod = MediatorLiveData<AirPods?>()
        .apply {
            addSource(nearbyAirPods) { airPods ->
                postValue(airPods.firstOrNull())
            }
        }

}
