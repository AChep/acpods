package com.artemchep.acpods.base

/**
 * Execute the block
 */
inline fun ifDebug(crossinline block: () -> Unit) {
    block()
}
