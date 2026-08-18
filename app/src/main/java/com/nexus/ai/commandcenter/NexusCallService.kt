package com.nexus.ai.commandcenter

import android.telecom.Call
import android.telecom.InCallService
import android.util.Log

class NexusCallService : InCallService() {
    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        if (call.state == Call.STATE_RINGING) {
            Log.d("NexusCall", "INCOMING TRANSMISSION DETECTED.")
        }
    }
}
