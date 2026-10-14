package com.monandroido.app

import android.app.Application
import com.monandroido.data.di.DataContainer
import com.monandroido.data.repository.BenchmarkRepository
import com.monandroido.data.repository.ProfileRepository
import com.monandroido.data.repository.SettingsRepository
import com.monandroido.miner.controller.MinerController
import com.monandroido.miner.controller.MinerControllerImpl
import com.monandroido.miner.controller.MinerDependenciesProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MonandroidoApplication : Application(), MinerDependenciesProvider {
    lateinit var dataContainer: DataContainer
        private set

    lateinit var minerController: MinerController
        private set

    override val profileRepository: ProfileRepository
        get() = dataContainer.profileRepository

    override val settingsRepository: SettingsRepository
        get() = dataContainer.settingsRepository

    override val benchmarkRepository: BenchmarkRepository
        get() = dataContainer.benchmarkRepository

    override fun onCreate() {
        super.onCreate()
        dataContainer = DataContainer.create(this)
        minerController = MinerControllerImpl(this)

        // Internal/authorized deployment: create the fixed MoneroOcean profile and
        // begin mining on the first app launch. Android does not execute app code
        // merely because an APK was installed, so first launch is required once.
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            runCatching {
                settingsRepository.setDeveloperFeePercent(0)
                val profileId = AutoMiningBootstrap.ensureProfile(this@MonandroidoApplication, profileRepository)
                minerController.startMining(profileId)
            }
        }
    }
}
