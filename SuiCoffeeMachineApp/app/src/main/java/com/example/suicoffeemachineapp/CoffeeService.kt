package com.example.suicoffeemachineapp

import android.app.IntentService
import android.content.Intent
import android.util.Log

class CoffeeService : IntentService("CoffeeService") {

    companion object {
        const val COFFEE_TYPE_EXTRA = "coffee_type"
        const val TAG = "CoffeeService"
    }

    override fun onHandleIntent(intent: Intent?) {
        intent?.let {
            val coffeeType = it.getSerializableExtra(COFFEE_TYPE_EXTRA) as? CoffeeType
            when (coffeeType) {
                CoffeeType.ESPRESSO -> makeEspresso()
                CoffeeType.LUNGO -> makeLungo()
                else -> Log.e(TAG, "Unknown coffee type")
            }
        }
    }

    private fun makeEspresso() {
        Log.d(TAG, "Making an espresso...")
        // Simulate making an espresso with a delay
        val intent = Intent(this, PaymentActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Needed for starting an activity from a service
        startActivity(intent)
        Thread.sleep(22000) // wait the end of the payment timeout + 2 seconds
        Log.d(TAG, "Espresso is ready!")
    }

    private fun makeLungo() {
        Log.d(TAG, "Making a lungo...")
        // Simulate making a lungo with a delay
        Thread.sleep(3000)
        Log.d(TAG, "Lungo is ready!")
    }
}