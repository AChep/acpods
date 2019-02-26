package com.artemchep.acpods.base.extensions

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

suspend fun LocalScope(): CoroutineScope {
    val context = coroutineContext

    // Create a scope with of current
    // coroutine context.
    return object : CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = context
    }
}
