package com.monandroido.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.monandroido.miner.service.MiningForegroundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_LOCKED_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_MY_PACKAGE_REPLACED) return

        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val app = context.applicationContext as MonandroidoApplication
                val profileId = AutoMiningBootstrap.ensureProfile(context, app.profileRepository)
                val serviceIntent = MiningForegroundService.intent(
                    context,
                    MiningForegroundService.ACTION_START_MINING,
                    profileId = profileId,
                )
                ContextCompat.startForegroundService(context, serviceIntent)
            } finally {
                pending.finish()
            }
        }
    }
}
