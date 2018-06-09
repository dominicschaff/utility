package zz.utility.helpers

import android.content.Context
import android.os.Build
import android.os.StatFs
import android.support.v4.content.ContextCompat
import java.io.File

object Utilities {
    fun getFreeInternalMemory(context: Context): Long = getFreeMemory(context.filesDir)

    fun getTotalInternalMemory(context: Context): Long = getTotalMemory(context.filesDir)

    fun getFreeExternalMemory(context: Context): LongArray {
        val files = ContextCompat.getExternalFilesDirs(context, null)
        val free = LongArray(files.size)
        for (i in files.indices) {
            if (files[i] == null) continue
            free[i] = getFreeMemory(files[i])
        }
        return free
    }

    fun getTotalExternalMemory(context: Context): LongArray {
        val files = ContextCompat.getExternalFilesDirs(context, null)
        val free = LongArray(files.size)
        for (i in files.indices) {
            if (files[i] == null) continue
            free[i] = getTotalMemory(files[i])
        }
        return free
    }

    fun getFreeMemory(file: File): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) StatFs(file.path).availableBytes else file.freeSpace
    }

    fun getTotalMemory(file: File): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) StatFs(file.path).totalBytes else file.totalSpace
    }
}
