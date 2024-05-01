package com.bramborman.nfcquicksettings

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class NfcTileService : TileService() {

    private val nfcAdapter: NfcAdapter? by lazy { NfcAdapter.getDefaultAdapter(this) }
    private val isNfcAvailable by lazy { nfcAdapter != null }
    private val intentFilter by lazy { IntentFilter(NfcAdapter.EXTRA_ADAPTER_STATE) }
    private val nfcStateBroadcastReceiver by lazy { object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            assert(intent.action == NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)
            updateQsTile(when (intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, -1)) {
                NfcAdapter.STATE_ON -> Tile.STATE_ACTIVE
                NfcAdapter.STATE_OFF -> Tile.STATE_INACTIVE
                else -> Tile.STATE_UNAVAILABLE
            })
        }
    }}

    override fun onStartListening() {
        super.onStartListening()

        if (!isNfcAvailable) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(nfcStateBroadcastReceiver, intentFilter, RECEIVER_NOT_EXPORTED)
        }
        else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(nfcStateBroadcastReceiver, intentFilter)
        }

        updateQsTile()
    }

    override fun onStopListening() {
        if (isNfcAvailable) {
            unregisterReceiver(nfcStateBroadcastReceiver)
        }

        super.onStopListening()
    }

    override fun onTileAdded() {
        super.onTileAdded()
        updateQsTile()
    }

    private fun updateQsTile(tileState: Int? = null) {
        qsTile.apply {
            state = when {
                tileState != null -> tileState
                !isNfcAvailable -> Tile.STATE_UNAVAILABLE
                !nfcAdapter!!.isEnabled -> Tile.STATE_INACTIVE
                else -> Tile.STATE_ACTIVE
            }
            subtitle = getText(when (state) {
                Tile.STATE_ACTIVE -> R.string.on
                Tile.STATE_INACTIVE -> R.string.off
                Tile.STATE_UNAVAILABLE -> R.string.unavailable
                else -> throw IllegalStateException("Unexpected Quick Settings Tile state: $state")
            })

            updateTile()
        }
    }

    override fun onClick() {
        super.onClick()

        unlockAndRun {
            val intent = Intent(Settings.ACTION_NFC_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startActivityAndCollapse(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE))
            }
            else {
                @Suppress("DEPRECATION", "StartActivityAndCollapseDeprecated")
                startActivityAndCollapse(intent)
            }
        }
    }
}
