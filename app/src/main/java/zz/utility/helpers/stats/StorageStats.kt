package zz.utility.helpers.stats

import android.content.Context
import zz.utility.helpers.getFreeExternalMemory
import zz.utility.helpers.getFreeInternalMemory
import zz.utility.helpers.getTotalExternalMemory
import zz.utility.helpers.getTotalInternalMemory

class StorageStats(
        val internal: Long,
        val external: Array<Long>,
        val internalFull: Long,
        val externalFull: Array<Long>)

fun Context.getStorageStats() = StorageStats(
        this.getFreeInternalMemory(),
        this.getFreeExternalMemory(),
        this.getTotalInternalMemory(),
        this.getTotalExternalMemory()
)