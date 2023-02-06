package dev.schaff.utility.helpers.stats

import android.content.Context
import dev.schaff.utility.helpers.getFreeExternalMemory
import dev.schaff.utility.helpers.getFreeInternalMemory
import dev.schaff.utility.helpers.getTotalExternalMemory
import dev.schaff.utility.helpers.getTotalInternalMemory

class StorageStats(
    val internal: Long,
    val external: Array<Long>,
    val internalFull: Long,
    val externalFull: Array<Long>
)

fun Context.getStorageStats() = StorageStats(
    this.getFreeInternalMemory(),
    this.getFreeExternalMemory(),
    this.getTotalInternalMemory(),
    this.getTotalExternalMemory()
)