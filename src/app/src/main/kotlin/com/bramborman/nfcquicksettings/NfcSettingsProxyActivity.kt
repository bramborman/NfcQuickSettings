package com.bramborman.nfcquicksettings

import android.app.Activity
import android.os.Bundle

class NfcSettingsProxyActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (NfcManager(this).isAvailable) {
            startActivity(NfcManager.nfcSettingsIntent)
        }

        finish()
    }
}
