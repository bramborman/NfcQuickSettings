package com.bramborman.nfcquicksettings.internal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.os.Build

public class NfcStateManager(private val context: Context) {
    private val nfcAdapter: NfcAdapter? by lazy { NfcAdapter.getDefaultAdapter(context) }
    private var nfcStateBroadcastReceiver: BroadcastReceiver? = null
    public val state: NfcState
        get() {
            if (nfcAdapter == null) {
                return NfcState.UNAVAILABLE
            }

            return if (nfcAdapter!!.isEnabled) NfcState.ON else NfcState.OFF
        }

    public fun startListening(onChange: (NfcState?) -> Unit) {
        if (nfcStateBroadcastReceiver != null) {
            return
        }

        nfcStateBroadcastReceiver = object : BroadcastReceiver() {
            public override fun onReceive(context: Context, intent: Intent) {
                onChange(NfcState.fromNfcAdapterState(intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, -1)))
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(nfcStateBroadcastReceiver, nfcStateBroadcastReceiverIntentFilter, Context.RECEIVER_EXPORTED)
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
        private val nfcStateBroadcastReceiverIntentFilter by lazy { IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED) }
    }
}
