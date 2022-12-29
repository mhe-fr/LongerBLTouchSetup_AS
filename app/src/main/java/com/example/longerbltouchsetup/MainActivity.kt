package com.example.longerbltouchsetup

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    fun onClickConnect(view: View) {
        // Find all available drivers from attached devices.
        val manager = getSystemService(Context.USB_SERVICE) as UsbManager
        val availableDrivers: List<UsbSerialDriver> =
            UsbSerialProber.getDefaultProber().findAllDrivers(manager)
        if (availableDrivers.isEmpty()) {
            return
        }

        // Open a connection to the first available driver.
        val driver: UsbSerialDriver = availableDrivers[0]
        val connection = manager.openDevice(driver.getDevice())
            ?: // add UsbManager.requestPermission(driver.getDevice(), ..) handling here

        // val usbSerialPort = driver.ports[portNum]
        val port: UsbSerialPort =
            driver.getPorts().get(0) // Most devices have just one port (port 0)
        val usbConnection: UsbDeviceConnection = manager.openDevice(driver.device)
        if (usbConnection == null && usbPermission === UsbPermission.Unknown && !manager.hasPermission(
                driver.device
            )
        ) {
            usbPermission = UsbPermission.Requested
            val flags =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
            val usbPermissionIntent =
                PendingIntent.getBroadcast(getActivity(), 0, Intent(INTENT_ACTION_GRANT_USB), flags)
            manager.requestPermission(driver.device, usbPermissionIntent)
            return
        }
        if (usbConnection == null) {
            if (!manager.hasPermission(driver.device)) status("connection failed: permission denied") else status(
                "connection failed: open failed"
            )
            return
        }
        return



        port.open(connection)
        port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)// Do something in response to button click
    }
}