package zz.utility.helpers.stats

import android.content.Context

import zz.utility.helpers.Utilities

class StorageStats(
        val internal: Long,
        val external: LongArray,
        val internalFull: Long,
        val externalFull: LongArray) {

    companion object {
        operator fun get(context: Context): StorageStats = StorageStats(
                Utilities.getFreeInternalMemory(context),
                Utilities.getFreeExternalMemory(context),
                Utilities.getTotalInternalMemory(context),
                Utilities.getTotalExternalMemory(context)
        )
    }
}
