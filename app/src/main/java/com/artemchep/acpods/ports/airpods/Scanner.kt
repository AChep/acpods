package com.artemchep.acpods.ports.airpods

import android.bluetooth.BluetoothAssignedNumbers
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.util.Log
import com.artemchep.acpods.base.ifDebug
import com.artemchep.acpods.data.AirPods
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowViaChannel
import java.util.concurrent.Executors
import kotlin.coroutines.resumeWithException

@FlowPreview
internal fun CoroutineScope.flowOfAirPodsEvents(context: Context): Flow<AirPodsScanner.Event> =
    flowViaChannel { channel ->
        val scanner = AirPodsScanner(this, channel)
        launch {
            suspendCancellableCoroutine<Unit> {
                val initialized = scanner.init(context)
                if (initialized) {
                    scanner.start()
                    // Do not resume the continuation, so it never stops
                    // before we cancel the scope.
                } else {
                    it.resumeWithException(RuntimeException())
                }
            }
        }.invokeOnCompletion {
            if (scanner.isStarted) {
                scanner.stop()
            }
        }
    }

/**
 * @author Artem Chepurnoy
 */
internal class AirPodsScanner(scope: CoroutineScope, private val actor: SendChannel<Event>) {

    companion object {
        const val TAG = "AirPodsScanner"
    }

    private val decoderScope = scope + Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private val decoder = Decoder()

    /**
     * `true` if the scanner is started,
     * `false` otherwise.
     */
    @Volatile
    var isStarted = false
        private set

    private lateinit var btScanner: BluetoothLeScanner

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            decoderScope.launch {
                val airPods = decoder.decode(result)

                // Send new model to the
                // actor.
                try {
                    ifDebug {
                        Log.d(TAG, "Sending an AirPod=$airPods to an observer.")
                    }

                    actor.send(
                        when (callbackType) {
                            ScanSettings.CALLBACK_TYPE_MATCH_LOST -> Event.Remove(airPods)
                            else -> Event.Add(airPods)
                        }
                    )
                } catch (e: ClosedSendChannelException) {
                    ifDebug {
                        throw RuntimeException(e)
                    }
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e(TAG, "Scan has failed! Error code is $errorCode")
        }
    }

    fun init(context: Context): Boolean {
        btScanner = (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)
            ?.adapter
            ?.bluetoothLeScanner
            ?: return false
        return true
    }

    /**
     * Starts scanning for AirPods.
     */
    fun start() {
        if (isStarted) {
            error("Service is not stopped.")
        } else isStarted = true

        GlobalScope.launch(Dispatchers.Default) {
            ifDebug {
                Log.d(TAG, "Starting to scan...")
            }

            val scanFilters = createScanFilters()
            val scanSettings = ScanSettings.Builder()
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
                .build()
            btScanner.startScan(scanFilters, scanSettings, scanCallback)
        }
    }

    private fun createScanFilters(): List<ScanFilter> {

        fun createScanFilter(size: Int): ScanFilter {
            val a = ByteArray(size).apply { this[0] = 7 }
            return ScanFilter.Builder()
                .setManufacturerData(BluetoothAssignedNumbers.APPLE, a, a)
                .build()
        }

        return mutableListOf(
            createScanFilter(16),
            createScanFilter(26)
        )
    }

    /**
     * Stop scanning for AirPods.
     */
    fun stop() {
        if (isStarted) {
            isStarted = false
        } else error("Service is not started.")

        GlobalScope.launch(Dispatchers.Default) {
            ifDebug {
                Log.d(TAG, "Stopping to scan...")
            }

            btScanner.stopScan(scanCallback)
        }
    }

    /**
     * @author Artem Chepurnoy
     */
    internal sealed class Event {
        internal data class Add(val airPods: AirPods) : Event()
        internal data class Remove(val airPods: AirPods) : Event()
    }

}
