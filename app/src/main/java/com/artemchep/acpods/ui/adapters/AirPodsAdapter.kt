package com.artemchep.acpods.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.artemchep.acpods.R
import com.artemchep.acpods.data.AirPod
import com.artemchep.acpods.data.AirPods
import com.artemchep.acpods.ui.interfaces.OnItemClickListener

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
        holder.apply {
            addressTextView.text = airPods.info.address + ", " + airPods.info.rssi
            stateTextView.text = listOf(
                airPods.leftPod?.batteryLevel to R.string.airpods_left_battery,
                airPods.rightPod?.batteryLevel to R.string.airpods_right_battery,
                airPods.case?.batteryLevel to R.string.airpods_case_battery
            ).mapNotNull { (batteryLevel, stringRes) ->
                batteryLevel?.let { context.getString(stringRes, it) }
            }.joinToString().capitalize()
        }
    }

    private fun bindAirPod(airPod: AirPod?, imageView: ImageView, batteryTextView: TextView) {
        if (airPod != null) {
            batteryTextView.text = "${airPod.batteryLevel}%"
            batteryTextView.alpha = 1.0f
            imageView.alpha = 1.0f
        } else {
            batteryTextView.text = "n/a"
            batteryTextView.alpha = 0.4f
            imageView.alpha = 0.4f
        }
    }

    /**
     * @author Artem Chepurnoy
     */
    class ViewHolder(
        view: View,
        listener: OnItemClickListener<Int>
    ) : AdapterBase.ViewHolderBase(view, listener) {

        internal val addressTextView = view.findViewById<TextView>(R.id.addressView)
        internal val stateTextView = view.findViewById<TextView>(R.id.stateView)

    }

}
