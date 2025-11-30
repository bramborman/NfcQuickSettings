package com.bramborman.nfcquicksettings.internal

import android.nfc.NfcAdapter

public enum class NfcState {
    UNAVAILABLE,
    OFF,
    ON,
    TURNING_OFF,
    TURNING_ON,
    ;

    companion object {
        public fun fromNfcAdapterState(nfcAdapterState: Int): NfcState {
            return when (nfcAdapterState) {
                NfcAdapter.STATE_OFF -> OFF
                NfcAdapter.STATE_ON -> ON
                NfcAdapter.STATE_TURNING_OFF -> TURNING_OFF
                NfcAdapter.STATE_TURNING_ON -> TURNING_ON
                else -> throw IllegalArgumentException("Unexpected adapter state: $nfcAdapterState")
            }
        }
    }
}
