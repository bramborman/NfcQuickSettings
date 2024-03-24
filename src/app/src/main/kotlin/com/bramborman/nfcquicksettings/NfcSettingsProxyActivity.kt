package com.bramborman.nfcquicksettings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings

class NfcSettingsProxyActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(Settings.ACTION_NFC_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }
}
