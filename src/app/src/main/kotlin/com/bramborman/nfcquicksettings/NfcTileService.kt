package com.bramborman.nfcquicksettings

import android.app.PendingIntent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.bramborman.nfcquicksettings.internal.NfcState
import com.bramborman.nfcquicksettings.internal.NfcStateManager
import com.bramborman.nfcquicksettings.internal.nfcSettingsIntent

public class NfcTileService : TileService() {
    private val nfcStateManager by lazy { NfcStateManager(this) }

    public override fun onStartListening() {
        super.onStartListening()
        if (nfcStateManager.state != NfcState.UNAVAILABLE) {
            nfcStateManager.startListening(::updateQsTile)
        }
        updateQsTile()
    }

    public override fun onStopListening() {
        nfcStateManager.stopListening()
        super.onStopListening()
    }

    public override fun onTileAdded() {
        super.onTileAdded()
        updateQsTile()
    }

    private fun updateQsTile(nfcState: NfcState? = null) {
        qsTile.apply {
            state = when (nfcState ?: nfcStateManager.state) {
                NfcState.UNAVAILABLE -> Tile.STATE_UNAVAILABLE
                NfcState.OFF, NfcState.TURNING_OFF -> Tile.STATE_INACTIVE
                else -> Tile.STATE_ACTIVE
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                icon = Icon.createWithResource(this@NfcTileService, when (state) {
                    Tile.STATE_ACTIVE, Tile.STATE_UNAVAILABLE -> R.drawable.ic_nfc
                    Tile.STATE_INACTIVE -> R.drawable.ic_nfc_off
                    else -> 0 // Unreachable
                })
            }

            // Android 16 (Baklava) doesn't show On/Off text on system tiles
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && Build.VERSION.SDK_INT < Build.VERSION_CODES.BAKLAVA) {
                subtitle = getText(when (state) {
                    Tile.STATE_ACTIVE -> R.string.on
                    Tile.STATE_INACTIVE -> R.string.off
                    Tile.STATE_UNAVAILABLE -> R.string.unavailable
                    else -> 0 // Unreachable
                })
            }

            updateTile()
        }
    }

    public override fun onClick() {
        super.onClick()

        unlockAndRun {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startActivityAndCollapse(PendingIntent.getActivity(this, 0, nfcSettingsIntent, PendingIntent.FLAG_IMMUTABLE))
            }
            else {
                @Suppress("DEPRECATION", "StartActivityAndCollapseDeprecated")
                startActivityAndCollapse(nfcSettingsIntent)
            }
        }
    }
}
