@file:Suppress("NOTHING_TO_INLINE")

package zz.utility.helpers

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream


inline fun Activity.goto(c: Class<*>) = startActivity(Intent(this, c))
inline fun Activity.gotoNewWindow(c: Class<*>) {
    startActivity(
        Intent(this, c).setFlags(
            Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
    )
}

inline fun Activity.hasLocationPermissions(): Boolean = ContextCompat.checkSelfPermission(
    this,
    Manifest.permission.ACCESS_FINE_LOCATION
) != PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED

inline fun Activity.intentClearTop(cls: Class<*>) {
    this.startActivity(
        Intent(
            this,
            cls
        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    )
}

inline fun String.log(type: String = "App") = Log.i(type, this)
inline fun String.warn(type: String = "App") = Log.w(type, this)
inline fun String.debug(type: String = "App") = Log.d(type, this)
inline fun String.error(type: String = "App") = Log.e(type, this)
inline fun String.wtf(type: String = "App") = Log.wtf(type, this)
inline fun String.verbose(type: String = "App") = Log.v(type, this)

fun View.screenshot(file: File) {
    screenshot(false).compress(CompressFormat.JPEG, 95, FileOutputStream(file))
    isDrawingCacheEnabled = false
}

fun View.screenshot(autoCleanup: Boolean = true): Bitmap {
    isDrawingCacheEnabled = true
    val b = drawingCache
    if (autoCleanup) isDrawingCacheEnabled = false
    return b
}

fun Context.copyToClipboard(str: String) {

    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(str, str))
    Toast.makeText(this, "Set clipboard to: $str", Toast.LENGTH_LONG).show()
}