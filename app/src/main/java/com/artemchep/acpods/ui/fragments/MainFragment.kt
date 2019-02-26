package com.artemchep.acpods.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.artemchep.acpods.ACTION_PERMISSIONS_CHANGED
import com.artemchep.acpods.R
import com.artemchep.acpods.domain.models.AirPod
import com.artemchep.acpods.extensions.containsType
import com.artemchep.acpods.models.Issue
import com.artemchep.acpods.ui.adapters.AirPodsAdapter
import com.artemchep.acpods.viewmodels.AirPodsViewModel
import kotlinx.android.synthetic.main.fragment_main.*
import android.bluetooth.BluetoothAdapter

/**
 * @author Artem Chepurnoy
 */
class MainFragment : Fragment() {

    companion object {
        private const val RC_PERMISSIONS = 1001
    }

    private lateinit var viewModel: AirPodsViewModel

    private lateinit var adapter: AirPodsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AirPodsAdapter()

        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        bluetoothIssueView.setOnClickListener {
            val adapter = BluetoothAdapter.getDefaultAdapter()
            adapter.enable()
        }

        permissionsIssueView.setOnClickListener {
            val permissions = viewModel.issuePermissions.value?.permissions
                ?: return@setOnClickListener
            if (permissions.isNotEmpty()) {
                requestPermissions(permissions.toTypedArray(), RC_PERMISSIONS)
            }
        }

        viewModel = AirPodsViewModel.getInstance(activity!!.application)
        viewModel.setup()
    }

    private fun AirPodsViewModel.setup() {
        issues.observe(viewLifecycleOwner, Observer { issues ->
            bluetoothIssueView.isVisible = issues.containsType<Issue.BluetoothIssue>()
            permissionsIssueView.isVisible = issues.containsType<Issue.PermissionIssue>()

            // Update the visibility of a container basing on a
            // visibility of its children.
            issuesContainer.isVisible = bluetoothIssueView.isVisible ||
                    permissionsIssueView.isVisible
        })

        nearbyAirPods.observe(viewLifecycleOwner, Observer { airPods ->
            // Update empty view
            airPodsEmptyView.isVisible = airPods.isEmpty()

            // Update the list
            adapter.models.apply {
                clear()
                addAll(airPods)
            }

            adapter.notifyDataSetChanged()
        })

        primaryAirPod.observe(viewLifecycleOwner, Observer { airPods ->
            fun bindAirPod(airPod: AirPod?, imageView: ImageView, batteryTextView: TextView) {
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

            bindAirPod(airPods?.leftPod, leftPodImage, leftPodBatteryView)
            bindAirPod(airPods?.rightPod, rightPodImage, rightPodBatteryView)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_PERMISSIONS -> {
                val lbm = LocalBroadcastManager.getInstance(context!!)
                lbm.sendBroadcast(Intent(ACTION_PERMISSIONS_CHANGED))
            }
        }
    }

}
