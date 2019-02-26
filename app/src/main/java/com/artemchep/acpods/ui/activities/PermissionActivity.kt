package com.artemchep.acpods.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.artemchep.acpods.ACTION_PERMISSIONS_CHANGED

/**
 * @author Artem Chepurnoy
 */
class PermissionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onPause() {
        sendPermissionsChangedBroadcast()
        super.onPause()
    }

    private fun sendPermissionsChangedBroadcast() {
        val lbm = LocalBroadcastManager.getInstance(this)
        lbm.sendBroadcast(Intent(ACTION_PERMISSIONS_CHANGED))
    }

}