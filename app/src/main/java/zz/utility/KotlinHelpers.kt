@file:Suppress("NOTHING_TO_INLINE", "DefaultLocale", "unused")

package zz.utility

import android.content.Context
import android.os.Environment
import com.google.gson.JsonObject
import zz.utility.helpers.asJsonObject
import java.io.File

fun String.extension(): String = substring(lastIndexOf(".")).lowercase()

fun StringBuilder.add(format: String, value: Int) {
    try {
        if (value > 0) {
            this.append(format.format(value))
            this.append("\n")
        }
    } catch (ignored: Exception) {
    }
}

fun StringBuilder.add(format: String, value: Double) {
    try {
        if (value > 0) {
            this.append(format.format(value))
            this.append("\n")
        }
    } catch (ignored: Exception) {
    }
}

fun StringBuilder.add(format: String, value: String?) {
    try {
        if (!value.isNullOrEmpty()) {
            this.append(format.format(value))
            this.append("\n")
        }
    } catch (ignored: Exception) {
    }
}


inline fun Context.externalFile(path: String) = File(homeDir(), path)
inline fun Context.sdFile(path: String) = File(Environment.getExternalStorageDirectory(), path)

inline fun Context.homeDir() = getExternalFilesDir(null)!!
inline fun Context.logFile() = externalFile("log.json")
inline fun Context.configFile() = try {
    externalFile("utility.json").asJsonObject()
} catch (e: java.lang.Exception) {
    JsonObject()
}