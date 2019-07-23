package com.artemchep.acpods.ports.airpods

import android.content.Context
import android.os.SystemClock
import com.artemchep.acpods.base.ifDebug
import com.artemchep.acpods.data.AirPods
import com.artemchep.acpods.ports.AirPodsPort
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author Artem Chepurnoy
 */
@FlowPreview
class AirPodsPortImpl(
    val context: Context
) : AirPodsPort {
    companion object {
        private const val GATHER_MAX_DELAY = 5L * 1000L
        private const val GATHER_MIN_DELAY = 2L * 1000L
    }

    override fun flowOfAirPods(): Flow<List<AirPods>> =
        flow {
            coroutineScope {
                val initialized = AtomicBoolean(false)

                val map = ConcurrentHashMap<String, Pair<AirPods, Long>>()

                launch {
                    flowOfAirPodsEvents(context)
                        .collect {
                            when (it) {
                                is AirPodsScanner.Event.Add -> map[it.airPods.info.address] =
                                    it.airPods to SystemClock.elapsedRealtime()
                                is AirPodsScanner.Event.Remove -> map.remove(it.airPods.info.address)
                            }

                            initialized.set(true)
                        }
                }

                val channel = Channel<List<AirPods>>(Channel.RENDEZVOUS)

                launch(Dispatchers.Default) {
                    while (isActive) {
                        launch {
                            // Remove outdated entries from a
                            // map.
                            val now = SystemClock.elapsedRealtime()
                            map.entries
                                .filter { now - it.value.second > GATHER_MAX_DELAY }
                                .map { it.key }
                                .forEach {
                                    map.remove(it)
                                }

                            // Form a list of AirPods.
                            val list = map.values
                                .map { it.first }
                                .sortedWith(
                                    compareBy(
                                        { it.info.property == AirPods.Property.MyProperty },
                                        { it.info.rssi }
                                    )
                                )
                                .asReversed()
                                .toList()
                            try {
                                channel.send(list)
                            } catch (e: ClosedSendChannelException) {
                                ifDebug {
                                    throw RuntimeException(e)
                                }
                            }
                        }

                        val time = if (initialized.get()) {
                            GATHER_MAX_DELAY
                        } else {
                            GATHER_MIN_DELAY
                        }
                        delay(time)
                    }
                }

                channel.consumeEach {
                    emit(it)
                }
            }
        }
}
