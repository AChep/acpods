package com.artemchep.acpods.utils

import kotlinx.coroutines.suspendCancellableCoroutine

suspend fun awaitCancel() = suspendCancellableCoroutine<Unit> { }
