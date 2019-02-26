package com.artemchep.acpods.ui.interfaces

import android.view.View

/**
 * @author Artem Chepurnoy
 */
interface OnItemClickListener<in T> {

    fun onItemClick(view: View, data: T, position: Int)

}
