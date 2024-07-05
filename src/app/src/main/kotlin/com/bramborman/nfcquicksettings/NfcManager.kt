package com.bramborman.nfcquicksettings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile

public class NfcManager(private val context: Context) {
    private val nfcAdapter: NfcAdapter? by lazy { NfcAdapter.getDefaultAdapter(context) }
    private val nfcStateBroadcastReceiverIntentFilter by lazy { IntentFilter(NfcAdapter.EXTRA_ADAPTER_STATE) }
    private var nfcStateBroadcastReceiver: BroadcastReceiver? = null

    public val isAvailable by lazy { nfcAdapter != null }
    public val isEnabled get() = nfcAdapter?.isEnabled

    public fun startListening(updateQsTile: (Int?) -> Unit) {
        if (!isAvailable || nfcStateBroadcastReceiver != null) {
            return
        }

        nfcStateBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                assert(intent.action == NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)
                updateQsTile(when (intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, -1)) {
                    NfcAdapter.STATE_ON, NfcAdapter.STATE_TURNING_ON -> Tile.STATE_ACTIVE
                    NfcAdapter.STATE_OFF, NfcAdapter.STATE_TURNING_OFF -> Tile.STATE_INACTIVE
                    else -> Tile.STATE_UNAVAILABLE
                })
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(nfcStateBroadcastReceiver, nfcStateBroadcastReceiverIntentFilter, Context.RECEIVER_NOT_EXPORTED)
        }
        else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            context.registerReceiver(nfcStateBroadcastReceiver, nfcStateBroadcastReceiverIntentFilter)
        }
    }

    public fun stopListening() {
        if (nfcStateBroadcastReceiver == null) {
            return
        }

        context.unregisterReceiver(nfcStateBroadcastReceiver)
        nfcStateBroadcastReceiver = null
    }

    companion object {
        public val nfcSettingsIntent by lazy {
            Intent(Settings.ACTION_NFC_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
    }
}
