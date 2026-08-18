package com.nexus.ai.commandcenter

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log

class NexusVpnService : VpnService() {
    private var vpnInterface: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("NexusVPN", "INITIALIZING SECURE TUNNEL...")
        establishVpnConnection()
        return START_STICKY
    }

    private fun establishVpnConnection() {
        try {
            val builder = Builder()
            builder.addAddress("10.0.0.2", 24)
            builder.addRoute("0.0.0.0", 0)
            builder.addDnsServer("1.1.1.1")
            builder.setSession("Nexus Secure Tunnel")
            vpnInterface = builder.establish()
        } catch (e: Exception) {
            Log.e("NexusVPN", "CONNECTION FAILED: ${e.localizedMessage}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        vpnInterface?.close()
        vpnInterface = null
    }
}
