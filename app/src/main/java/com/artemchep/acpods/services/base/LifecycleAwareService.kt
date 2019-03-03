package com.artemchep.acpods.services.base

import android.app.Service
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * @author Artem Chepurnoy
 */
abstract class LifecycleAwareService : Service(), LifecycleOwner {

    @Suppress("LeakingThis")
    private val lifecycle = LifecycleRegistry(this)

    override fun onCreate() {
        lifecycle.markState(Lifecycle.State.STARTED)
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.markState(Lifecycle.State.DESTROYED)
    }

    override fun getLifecycle(): Lifecycle = lifecycle

}
