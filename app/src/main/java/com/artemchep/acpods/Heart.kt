package com.artemchep.acpods

import android.app.Application
import com.artemchep.acpods.domain.Injection
import com.artemchep.acpods.domain.injection
import com.artemchep.acpods.ports.AirPodsPort
import com.artemchep.acpods.ports.BroadcastPort
import com.artemchep.acpods.ports.PermissionsPort
import com.artemchep.acpods.ports.airpods.AirPodsPortImpl
import com.artemchep.acpods.ports.broadcast.GlobalBroadcastPortImpl
import com.artemchep.acpods.ports.broadcast.LocalBroadcastPortImpl
import com.artemchep.acpods.ports.permissions.PermissionsPortImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

/**
 * @author Artem Chepurnoy
 */
class Heart : Application() {

    @ExperimentalCoroutinesApi
    val sessionManager by lazy { SessionManager(this) }

    @FlowPreview
    override fun onCreate() {
        super.onCreate()
        injection = object : Injection {
            override val airPodsPort: AirPodsPort = AirPodsPortImpl(this@Heart)
            override val permissionsPost: PermissionsPort = PermissionsPortImpl(this@Heart)
            override val globalBroadcastPost: BroadcastPort = GlobalBroadcastPortImpl()
            override val localBroadcastPost: BroadcastPort = LocalBroadcastPortImpl()
        }
    }

}
