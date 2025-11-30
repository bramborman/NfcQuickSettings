package com.bramborman.nfcquicksettings

import android.app.Activity
import android.os.Bundle
import com.bramborman.nfcquicksettings.internal.NfcState
import com.bramborman.nfcquicksettings.internal.NfcStateManager
import com.bramborman.nfcquicksettings.internal.nfcSettingsIntent

public class NfcSettingsProxyActivity : Activity() {
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (NfcStateManager(this).state != NfcState.UNAVAILABLE) {
            startActivity(nfcSettingsIntent)
        }

        finish()
    }
}
