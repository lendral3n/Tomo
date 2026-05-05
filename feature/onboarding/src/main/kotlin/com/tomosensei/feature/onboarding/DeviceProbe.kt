package com.tomosensei.feature.onboarding

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.StatFs
import android.os.Environment

data class DeviceCheckResult(
    val androidVersionOk: Boolean,
    val ramOk: Boolean,
    val ramGb: Float,
    val storageOk: Boolean,
    val storageFreeGb: Float,
    val abiOk: Boolean,
    val abi: String,
) {
    val allGreen: Boolean get() = androidVersionOk && ramOk && storageOk && abiOk
}

object DeviceProbe {

    private const val MIN_RAM_GB = 8f
    private const val MIN_FREE_STORAGE_GB = 5f

    fun run(context: Context): DeviceCheckResult {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = ActivityManager.MemoryInfo().also { am.getMemoryInfo(it) }
        val ramGb = info.totalMem / 1024f / 1024f / 1024f

        val stat = StatFs(Environment.getDataDirectory().path)
        val freeGb = stat.availableBytes / 1024f / 1024f / 1024f

        val abi = Build.SUPPORTED_ABIS.firstOrNull().orEmpty()
        return DeviceCheckResult(
            androidVersionOk = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
            ramOk = ramGb >= MIN_RAM_GB,
            ramGb = ramGb,
            storageOk = freeGb >= MIN_FREE_STORAGE_GB,
            storageFreeGb = freeGb,
            abiOk = abi == "arm64-v8a",
            abi = abi,
        )
    }
}
