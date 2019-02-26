package com.artemchep.acpods.extensions

infix fun Int.contains(mask: Int) = (this and mask) == mask

infix fun Int.notContains(mask: Int) = !(this contains mask)
