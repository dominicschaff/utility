@file:Suppress("NOTHING_TO_INLINE")

package dev.schaff.utility.helpers

fun Long.formatSize(): String {
    var s = "B"
    var n = this * 1.0

    if (n >= 1024) {
        s = "KB"
        n /= 1024.0
    }

    if (n >= 1024) {
        s = "MB"
        n /= 1024.0
    }

    if (n >= 1024) {
        s = "GB"
        n /= 1024.0
    }
    return "%.2f $s".format(n)
}