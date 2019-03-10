package com.artemchep.acpods.ui.fragments

import android.bluetooth.BluetoothAdapter
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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.artemchep.acpods.R
import com.artemchep.acpods.data.AirPod
import com.artemchep.acpods.domain.models.Issue
import com.artemchep.acpods.domain.viewmodels.AirPodsViewModel
import com.artemchep.acpods.extensions.containsType
import com.artemchep.acpods.ui.AirPodsDiff
import com.artemchep.acpods.ui.adapters.AirPodsAdapter
import kotlinx.android.synthetic.main.fragment_main.*

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

        airPods.observe(viewLifecycleOwner, Observer { airPods ->
            // Update empty view
            airPodsEmptyView.isVisible = airPods.isEmpty()

            val airPodsOld = adapter.models.toList()

            // Update the list
            adapter.models.apply {
                clear()
                addAll(airPods)
            }

            val diffResult = DiffUtil.calculateDiff(AirPodsDiff(airPods, airPodsOld))
            diffResult.dispatchUpdatesTo(adapter)
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
            RC_PERMISSIONS -> viewModel.notifyPermissionsChanged()
        }
    }

}
