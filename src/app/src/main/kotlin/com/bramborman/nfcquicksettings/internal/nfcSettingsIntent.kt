package com.bramborman.nfcquicksettings.internal

import android.content.Intent
import android.provider.Settings

public val nfcSettingsIntent by lazy {
    Intent(Settings.ACTION_NFC_SETTINGS).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
}
