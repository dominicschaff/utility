package zz.utility.helpers.stats

import android.app.ActivityManager
import android.content.Context

import android.content.Context.ACTIVITY_SERVICE

class MemoryStats(
        val total: Long,
        val available: Long,
        val threshold: Long
)

fun Context.getMemoryStats(): MemoryStats {
    val actManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
    val memInfo = ActivityManager.MemoryInfo()
    actManager.getMemoryInfo(memInfo)

    return MemoryStats(memInfo.totalMem, memInfo.availMem, memInfo.threshold)
}