package com.artemchep.acpods.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.artemchep.acpods.R
import com.artemchep.acpods.data.AirPods
import com.artemchep.acpods.ui.interfaces.OnItemClickListener
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_airpods2.*

/**
 * @author Artem Chepurnoy
 */
class AirPodsAdapter : AdapterBase<AirPods, AirPodsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context!!)
        val view = inflater.inflate(R.layout.item_airpods2, parent, false)
        return ViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val airPods = this[position]
        holder.bind(airPods)
    }

    /**
     * @author Artem Chepurnoy
     */
    class ViewHolder(
        view: View,
        listener: OnItemClickListener<Int>
    ) : AdapterBase.ViewHolderBase(view, listener), LayoutContainer {
        override val containerView: View?
            get() = itemView

        fun bind(airPods: AirPods) {
            addressView.text = airPods.info.address
            signalView.text = airPods.info.rssi.toString()

            airPods.leftPod?.batteryLevel.let {
                val isShown = it != null
                leftPodImage.isVisible = isShown
                leftPodBatteryView.isVisible = isShown

                if (isShown) leftPodBatteryView.text = "$it %"
            }

            airPods.rightPod?.batteryLevel.let {
                val isShown = it != null
                rightPodImage.isVisible = isShown
                rightPodBatteryView.isVisible = isShown

                if (isShown) rightPodBatteryView.text = "$it %"
            }
        }
    }

}
