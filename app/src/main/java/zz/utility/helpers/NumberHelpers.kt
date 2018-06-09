@file:Suppress("unused", "NOTHING_TO_INLINE")

package zz.utility.helpers

inline fun String.intOr(default: Int = 0): Int {
    return try {
        Integer.parseInt(this)
    } catch (n: NumberFormatException) {
        default
    }
}

inline fun String.longOr(default: Long = 0): Long {
    return try {
        java.lang.Long.parseLong(this)
    } catch (n: NumberFormatException) {
        default
    }
}

inline fun String.doubleOr(default: Double = 0.0): Double {
    return try {
        java.lang.Double.parseDouble(this)
    } catch (n: NumberFormatException) {
        default
    }
}

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
    return "%.2f %s".format(n, s)
}