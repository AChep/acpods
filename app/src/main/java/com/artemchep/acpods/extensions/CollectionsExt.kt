package com.artemchep.acpods.extensions

inline fun <reified T> Iterable<*>.containsType() = firstOrNull { it is T } != null
