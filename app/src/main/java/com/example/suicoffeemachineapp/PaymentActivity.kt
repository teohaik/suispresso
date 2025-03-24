package com.example.suicoffeemachineapp

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.math.BigDecimal
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONException

class PaymentActivity : Activity() {

    companion object {
        const val PAYMENT_TIMEOUT_MS = 40000L
        const val SUI_FULLNODE_URL = "https://fullnode.testnet.sui.io:443"
        const val ADMIN_ADDRESS = "0x85fdd0ade17a35d9725e4e8e9ce34ed053d78b4eebe7782919372bb9d5584d99"
        const val QR_CODE_CONTENT = ADMIN_ADDRESS
        const val SUI_COIN = "0x0000000000000000000000000000000000000000000000000000000000000002::sui::SUI"

        const val PAYMENT_AMOUNT = 100000000

        private const val TAG = "PaymentActivity"
    }

    private lateinit var timer: CountDownTimer
    private val client = OkHttpClient()
    private var initialBalance: BigDecimal = BigDecimal.ZERO


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val qrCodeImageView = findViewById<ImageView>(R.id.qrCodeImageView)
        val timerTextView = findViewById<TextView>(R.id.timerTextView)

        // Generate QR code
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.encodeBitmap(QR_CODE_CONTENT, BarcodeFormat.QR_CODE, 400, 400)
        qrCodeImageView.setImageBitmap(bitmap)

        // Get the initial balance before starting the timer
        getInitialBalance()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel timer if activity is destroyed
        timer.cancel()
    }



    private fun getInitialBalance() {
        val request = prepareRequest()


        // Make the API call asynchronously
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e(TAG, "API call failed", e)
                // Handle failure (e.g., display an error message)
                runOnUiThread{
                    Toast.makeText(this@PaymentActivity, "API call failed", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    // Process the successful response
                    Log.d(TAG, "API call successful: $body")
                    try {
                        val jsonResponse = JSONObject(body)
                        val result = jsonResponse.getJSONObject("result")
                        val balance = result.getString("totalBalance")
                        initialBalance = BigDecimal(balance)
                        runOnUiThread {
                            // Start the countdown timer after the initial balance is fetched
                            timer = object : CountDownTimer(PAYMENT_TIMEOUT_MS, 1000) {
                                override fun onTick(millisUntilFinished: Long) {
                                    findViewById<TextView>(R.id.timerTextView).text = "Time left: ${millisUntilFinished / 1000}s"

                                    // Check for payment on each tick
                                    checkPaymentStatus()
                                }

                                override fun onFinish() {
                                    // Close activity after timeout
                                    finish()
                                }
                            }.start()
                        }
                    } catch (e: JSONException) {
                        Log.e(TAG, "Error in balance parsing: ${e.message}")
                        runOnUiThread{
                            Toast.makeText(this@PaymentActivity, "Error in balance parsing", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Log.e(TAG, "API call unsuccessful: ${response.code} - $body")
                    runOnUiThread{
                        Toast.makeText(this@PaymentActivity, "API call unsuccessful: ${response.code} - $body", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }


    private fun checkPaymentStatus() {
        val request = prepareRequest()

        // Make the API call asynchronously
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e(TAG, "API call failed", e)
                // Handle failure (e.g., display an error message)
                runOnUiThread{
                    Toast.makeText(this@PaymentActivity, "API call failed", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    // Process the successful response
                    Log.d(TAG, "API call successful: $body")
                    try {
                        val newBalance = BigDecimal(JSONObject(body).getJSONObject("result").getString("totalBalance"))
                        val diff = newBalance.subtract(initialBalance)
                        if (diff.compareTo(BigDecimal(PAYMENT_AMOUNT)) >= 0) {
                            // payment success
                            runOnUiThread {
                                Toast.makeText(this@PaymentActivity, "Enjoy your coffee!", Toast.LENGTH_LONG).show()
                            }
                            finish()
                        }
                    } catch (e: JSONException) {
                        Log.e(TAG, "Error in balance parsing: ${e.message}")
                        runOnUiThread{
                            Toast.makeText(this@PaymentActivity, "Error in balance parsing", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Log.e(TAG, "API call unsuccessful: ${response.code} - $body")
                    // Handle unsuccessful response
                    runOnUiThread{
                        Toast.makeText(this@PaymentActivity, "API call unsuccessful: ${response.code} - $body", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun prepareRequest(): Request {
        // Prepare JSON request body
        val jsonObject = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("id", 1)
            put("method", "suix_getBalance")
            // Create a JSON array for the params
            val paramsArray = JSONArray().apply {
                put(ADMIN_ADDRESS)
                put(SUI_COIN)
            }
            put("params", paramsArray)
        }

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaType())

        // Build the request
        val request = Request.Builder()
            .url(SUI_FULLNODE_URL)
            .post(requestBody)
            .build()
        return request
    }

}