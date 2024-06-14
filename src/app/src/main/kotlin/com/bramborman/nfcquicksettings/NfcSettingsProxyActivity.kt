package com.bramborman.nfcquicksettings

import android.app.Activity
import android.os.Bundle

class NfcSettingsProxyActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(NfcManager.nfcSettingsIntent)
        finish()
    }
}
