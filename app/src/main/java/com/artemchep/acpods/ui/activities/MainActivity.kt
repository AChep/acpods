package com.artemchep.acpods.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.artemchep.acpods.R
import com.artemchep.acpods.ui.adapters.AirPodsAdapter
import kotlinx.coroutines.*

/**
 * @author Artem Chepurnoy
 */
class MainActivity : AppCompatActivity() {

    private lateinit var adapter: AirPodsAdapter

    private lateinit var scannerScope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = AirPodsAdapter()

//        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        scannerScope = CoroutineScope(Dispatchers.Main + Job())
        scannerScope.launch {
            val context = this@MainActivity
        }
    }

    override fun onStop() {
        super.onStop()
        scannerScope.coroutineContext.cancel()
    }

}
