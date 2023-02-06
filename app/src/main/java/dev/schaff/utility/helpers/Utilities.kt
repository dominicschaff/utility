package dev.schaff.utility.helpers

import android.content.Context
import android.os.StatFs
import androidx.core.content.ContextCompat
import java.io.File

fun Context.getFreeInternalMemory(): Long = filesDir.getFreeMemory()

fun Context.getTotalInternalMemory(): Long = filesDir.getTotalMemory()

fun Context.getFreeExternalMemory(): Array<Long> =
    ContextCompat.getExternalFilesDirs(this, null)
        .filterNotNull()
        .map { it.getFreeMemory() }.toTypedArray()

fun Context.getTotalExternalMemory(): Array<Long> =
    ContextCompat.getExternalFilesDirs(this, null)
        .filterNotNull()
        .map { it.getTotalMemory() }.toTypedArray()

private fun File.getFreeMemory(): Long = StatFs(path).availableBytes

private fun File.getTotalMemory(): Long = StatFs(path).totalBytes
