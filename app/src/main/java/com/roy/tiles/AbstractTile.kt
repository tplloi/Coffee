package com.roy.tiles

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import androidx.annotation.RequiresApi
import com.roy.R
import com.roy.ServiceStatus
import com.roy.coffeeApp
import com.roy.toFormattedTime

@RequiresApi(Build.VERSION_CODES.N)
abstract class AbstractTile : TileService() {
    override fun onCreate() {
        Log.d(TAG, "onCreate()")
        super.onCreate()
        setTileState()
    }

    override fun onStartListening() {
        Log.d(TAG, "onStartListening()")
        setTileState()
        super.onStartListening()
    }

    override fun onTileAdded() {
        Log.d(TAG, "onTileAdded()")
        setTileState()
        super.onTileAdded()
    }

    private fun setTileState() {
        val currentStatus = coffeeApp().lastStatusUpdate
        Log.d(TAG, "setTileState(): running = $currentStatus")
        val tile = qsTile ?: return

        val (tileState, tileSubtitle) = when (currentStatus) {
            is ServiceStatus.Stopped -> Pair(Tile.STATE_INACTIVE, "")
            is ServiceStatus.Running -> {
                if (currentStatus.remaining == null) {
                    Pair(Tile.STATE_ACTIVE, "")
                } else {
                    Pair(Tile.STATE_ACTIVE, currentStatus.remaining.toFormattedTime())
                }
            }
        }

        tile.apply {
            state = tileState
            label = getString(R.string.app_name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                subtitle = tileSubtitle
            }
            updateTile()
        }
    }

    companion object {
        private val TAG = AbstractTile::class.java.simpleName
    }
}
