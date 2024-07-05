package com.bramborman.nfcquicksettings

import android.app.PendingIntent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

public class NfcTileService : TileService() {

    private val nfcManager by lazy { NfcManager(this) }

    public override fun onStartListening() {
        super.onStartListening()
        nfcManager.startListening(::updateQsTile)
        updateQsTile()
    }

    public override fun onStopListening() {
        nfcManager.stopListening()
        super.onStopListening()
    }

    public override fun onTileAdded() {
        super.onTileAdded()
        updateQsTile()
    }

    private fun updateQsTile(tileState: Int? = null) {
        qsTile.apply {
            state = when {
                tileState != null -> tileState
                !nfcManager.isAvailable -> Tile.STATE_UNAVAILABLE
                !nfcManager.isEnabled!! -> Tile.STATE_INACTIVE
                else -> Tile.STATE_ACTIVE
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                icon = Icon.createWithResource(this@NfcTileService, when (state) {
                    Tile.STATE_ACTIVE, Tile.STATE_UNAVAILABLE -> R.drawable.ic_nfc
                    Tile.STATE_INACTIVE -> R.drawable.ic_nfc_off
                    else -> 0 // Unreachable
                })
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
                startActivityAndCollapse(PendingIntent.getActivity(this, 0, NfcManager.nfcSettingsIntent, PendingIntent.FLAG_IMMUTABLE))
            }
            else {
                @Suppress("DEPRECATION", "StartActivityAndCollapseDeprecated")
                startActivityAndCollapse(NfcManager.nfcSettingsIntent)
            }
        }
    }
}
