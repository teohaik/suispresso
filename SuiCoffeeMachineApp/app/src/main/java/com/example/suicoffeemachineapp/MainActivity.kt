package com.example.suicoffeemachineapp

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.example.suicoffeemachineapp.CoffeeService.Companion.COFFEE_TYPE_EXTRA
import java.util.UUID

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnEspresso = findViewById<LinearLayout>(R.id.btnEspresso)
        val btnLungo = findViewById<LinearLayout>(R.id.btnLungo)

        btnEspresso.setOnClickListener{
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra(COFFEE_TYPE_EXTRA, CoffeeType.ESPRESSO)
            startActivity(intent)
        }
        btnLungo.setOnClickListener{
            //do something else
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra(COFFEE_TYPE_EXTRA, CoffeeType.LUNGO)
            startActivity(intent)
        }

        requestBlePermissions()
    }


    @SuppressLint("MissingPermission")
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            BluetoothFacade.startScan(this) // ✅ use the Facade here
        } else {
            Toast.makeText(this, "Permissions are required to use BLE", Toast.LENGTH_SHORT).show()
        }
    }


    private fun requestBlePermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            BluetoothFacade.startScan(this) // already granted
        }
    }

}