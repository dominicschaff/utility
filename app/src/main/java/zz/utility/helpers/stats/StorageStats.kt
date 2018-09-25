package zz.utility.helpers.stats

import android.content.Context

import zz.utility.helpers.Utilities

class StorageStats(
        val internal: Long,
        val external: Array<Long>,
        val internalFull: Long,
        val externalFull: Array<Long>)

fun Context.getStorageStats() = StorageStats(
        Utilities.getFreeInternalMemory(this),
        Utilities.getFreeExternalMemory(this),
        Utilities.getTotalInternalMemory(this),
        Utilities.getTotalExternalMemory(this)
)