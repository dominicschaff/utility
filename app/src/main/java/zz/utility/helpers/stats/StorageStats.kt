package zz.utility.helpers.stats

import android.content.Context

import zz.utility.helpers.Utilities

class StorageStats(
        val internal: Long,
        val external: Array<Long>,
        val internalFull: Long,
        val externalFull: Array<Long>) {

    companion object {
        operator fun get(context: Context): StorageStats = StorageStats(
                Utilities.getFreeInternalMemory(context),
                Utilities.getFreeExternalMemory(context),
                Utilities.getTotalInternalMemory(context),
                Utilities.getTotalExternalMemory(context)
        )
    }
}
