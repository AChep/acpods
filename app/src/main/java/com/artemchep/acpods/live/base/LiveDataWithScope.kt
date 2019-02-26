package com.artemchep.acpods.live.base

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * @author Artem Chepurnoy
 */
open class LiveDataWithScope<T> : LiveData<T>(), CoroutineScope {
    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onActive() {
        super.onActive()
        job = Job()
    }

    override fun onInactive() {
        job.cancel()
        super.onInactive()
    }
}
