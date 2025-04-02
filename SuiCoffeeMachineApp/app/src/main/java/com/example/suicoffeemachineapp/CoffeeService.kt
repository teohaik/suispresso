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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(COFFEE_TYPE_EXTRA, CoffeeType.ESPRESSO)
        startActivity(intent)
    }

    private fun makeLungo() {
        Log.d(TAG, "Making a lungo...")
        val intent = Intent(this, PaymentActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(COFFEE_TYPE_EXTRA, CoffeeType.LUNGO)
    }
}