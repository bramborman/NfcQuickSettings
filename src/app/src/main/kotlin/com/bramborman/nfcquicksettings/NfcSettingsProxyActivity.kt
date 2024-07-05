package com.bramborman.nfcquicksettings

import android.app.Activity
import android.os.Bundle

public class NfcSettingsProxyActivity : Activity() {
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (NfcManager(this).isAvailable) {
            startActivity(NfcManager.nfcSettingsIntent)
        }

        finish()
    }
}
