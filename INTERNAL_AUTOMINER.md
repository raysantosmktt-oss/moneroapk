# Internal MoneroOcean Auto Miner build

This variant is configured for devices owned/authorized by the operator.

- Wallet: 463c3D...CaEPwy (full value is in AutoMiningBootstrap.kt)
- Pool: gulf.moneroocean.stream:10128
- Worker: android-<last 12 chars of ANDROID_ID>
- Algorithm: RandomX / rx/0
- CPU hint: 75%
- Screen-off mining: enabled
- Developer fee routing: 0%
- Native ARM64 miner: native/xmrig/src/main/jniLibs/arm64-v8a/libxmrig.so
- Foreground notification: enabled by the existing MiningForegroundService
- First app launch: creates profile and starts miner
- Reboot/package update: BootReceiver requests restart
- Service: START_STICKY

Important Android behavior: installing an APK alone does not execute application code. The app must be opened once after installation. Device/OEM battery-management policies can still stop or delay background/boot execution.
