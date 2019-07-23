package com.artemchep.acpods.ports.connecteddevices

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import com.artemchep.acpods.utils.awaitCancel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@FlowPreview
internal fun flowOfConnectedDevices(context: Context): Flow<List<BluetoothDevice>> =
    flow {
        coroutineScope {
            val channel = Channel<List<BluetoothDevice>>(Channel.RENDEZVOUS)

            launch {
                val scanner = ConnectedDevicesScanner(this, channel)
                try {
                    val initialized = scanner.init(context)
                    if (initialized) {
                        scanner.start()
                        awaitCancel()
                    } else {
                        throw RuntimeException()
                    }
                } finally {
                    // Stop the scanner before killing the
                    // job.
                    if (scanner.isStarted) {
                        scanner.stop()
                    }
                }
            }

            channel.consumeEach {
                emit(it)
            }
        }
    }

/**
 * @author Artem Chepurnoy
 */
internal class ConnectedDevicesScanner(
    private val scope: CoroutineScope,
    private val actor: SendChannel<List<BluetoothDevice>>
) : BluetoothProfile.ServiceListener {

    companion object {
        private const val PERIOD = 2000L
    }

    private lateinit var context: Context

    private lateinit var btAdapter: BluetoothAdapter

    private var btProxyDeviceUpdateJob: Job? = null

    private var btProxy: BluetoothProfile? = null

    /**
     * `true` if the scanner is started,
     * `false` otherwise.
     */
    @Volatile
    var isStarted = false
        private set

    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
        btProxy = proxy
    }

    override fun onServiceDisconnected(profile: Int) {
        btProxy = null
    }

    fun init(context: Context): Boolean {
        this.context = context
        this.btAdapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)
            ?.adapter
            ?: return false
        return true
    }

    fun start() {
        if (isStarted) {
            error("Service is not stopped.")
        } else isStarted = true

        btAdapter.getProfileProxy(context, this, BluetoothProfile.HEADSET)

        btProxyDeviceUpdateJob = scope.launch {
            while (isActive) {
                val devices = try {
                    btProxy?.connectedDevices
                } catch (e: Exception) {
                    null
                }.orEmpty()

                try {
                    actor.send(devices)
                } catch (e: ClosedSendChannelException) {
                }

                // Wait before next update.
                delay(PERIOD)
            }
        }
    }

    fun stop() {
        if (isStarted) {
            isStarted = false
        } else error("Service is not started.")

        val proxy = btProxy
        if (proxy != null) {
            btAdapter.closeProfileProxy(BluetoothProfile.HEADSET, proxy)
        }

        btProxyDeviceUpdateJob?.cancel()
        btProxyDeviceUpdateJob = null
    }

}