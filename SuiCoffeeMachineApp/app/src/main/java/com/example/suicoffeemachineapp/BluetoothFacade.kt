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
import android.util.Log
import androidx.annotation.RequiresPermission
import java.util.UUID

object BluetoothFacade {

    private var isConnected = false

    private val espressoUUID = UUID.fromString("00002AC1-0000-1000-8000-00805F9B34FB")
    private val lungoUUID = UUID.fromString("00002AC2-0000-1000-8000-00805F9B34FB")
    private var bluetoothGatt: BluetoothGatt? = null

    private val TAG = "BluetoothService"

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScan(context: Context) {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val scanner = bluetoothAdapter.bluetoothLeScanner

        val filter = ScanFilter.Builder()
            .setDeviceName("TeoArduino")
            .build()
        val settings = ScanSettings.Builder().build()

        scanner.startScan(listOf(filter), settings, object : ScanCallback() {

            @SuppressLint("MissingPermission")
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                result?.device?.let  { device ->
                    scanner.stopScan(this)
                    connectToDevice(context, device)
                }
            }
        })
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun connectToDevice(context: Context, device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(context, false, object : BluetoothGattCallback() {
            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    isConnected = true
                    gatt.discoverServices()
                    Log.d(TAG, "Bluetooth Connected to Arduino")
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    isConnected = false
                    Log.d(TAG, "Bluetooth Disconnected")
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                Log.d(TAG, "Services discovered")
            }
        })
    }

    fun isDeviceConnected(): Boolean = isConnected

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun sendEspressoCommandToMachine() {
        while(!isDeviceConnected()){
            Log.d(TAG, "Waiting for device to connect")
        }
        writeCharacteristic(espressoUUID, byteArrayOf(1))
        Log.d(TAG, "Espresso command sent")
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun sendLungoCommandToMachine() {
        while(!isDeviceConnected()){
            Log.d(TAG, "Waiting for device to connect")
        }
        writeCharacteristic(lungoUUID, byteArrayOf(1))
        Log.d(TAG, "Lungo command sent")
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun writeCharacteristic(uuid: UUID, value: ByteArray) {
        val service = bluetoothGatt?.getService(UUID.fromString("0000180A-0000-1000-8000-00805F9B34FB"))
        val characteristic = service?.getCharacteristic(uuid)
        if (characteristic != null) {

            characteristic.value = value
            Log.e(TAG, "Writing characteristic : $value")
            bluetoothGatt?.writeCharacteristic(characteristic)
        } else {
            Log.e(TAG, "Characteristic not found: $uuid")
        }
    }

}