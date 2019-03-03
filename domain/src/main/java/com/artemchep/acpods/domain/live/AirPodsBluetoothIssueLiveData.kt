package com.artemchep.acpods.domain.live

import android.content.Context
import androidx.lifecycle.LiveData
import com.artemchep.acpods.domain.models.Issue

/**
 * @author Artem Chepurnoy
 */
class AirPodsBluetoothIssueLiveData(context: Context) : LiveData<Issue.BluetoothIssue?>() {
    override fun onActive() {
        super.onActive()
        postValue(null)
    }
}
