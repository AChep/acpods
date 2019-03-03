package com.artemchep.acpods.domain.live

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.artemchep.acpods.domain.models.Issue

/**
 * @author Artem Chepurnoy
 */
class AirPodsIssueLiveData(
    vararg issuesLiveData: LiveData<out Issue?>
) : MediatorLiveData<List<Issue>>() {
    private val resolver = { _: Any? ->
        val issues = issuesLiveData.mapNotNull { it.value }
        postValue(issues)
    }

    init {
        issuesLiveData
            .forEach {
                addSource(it, resolver)
            }
    }
}