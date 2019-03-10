package com.artemchep.acpods.ui

import androidx.recyclerview.widget.DiffUtil
import com.artemchep.acpods.data.AirPods

/**
 * @author Artem Chepurnoy
 */
class AirPodsDiff(
    private val new: List<AirPods>,
    private val old: List<AirPods>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return new[newItemPosition].info.address == old[oldItemPosition].info.address
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return new[newItemPosition] == old[oldItemPosition]
    }

}