package com.artemchep.acpods.live

import android.content.Context
import androidx.lifecycle.LiveData
import com.artemchep.acpods.models.Issue

/**
 * @author Artem Chepurnoy
 */
class AirPodsBluetoothIssueLiveData(context: Context) : LiveData<Issue.BluetoothIssue?>() {
    override fun onActive() {
        super.onActive()
        postValue(null)
    }
}
