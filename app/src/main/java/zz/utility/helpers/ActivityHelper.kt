@file:Suppress("NOTHING_TO_INLINE", "unused")

package zz.utility.helpers

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat

inline fun Activity.goto(c: Class<*>) = startActivity(Intent(this, c))
inline fun Activity.gotoNewWindow(c: Class<*>) {
    startActivity(Intent(this, c).setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
            Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_MULTIPLE_TASK))
}

inline fun Activity.hasLocationPermissions(): Boolean = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

inline fun Activity.intentClearTop(cls: Class<*>) {
    this.startActivity(Intent(this, cls).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
}

inline fun String.log(type: String = "App") = Log.i(type, this)
inline fun String.warn(type: String = "App") = Log.w(type, this)
inline fun String.debug(type: String = "App") = Log.d(type, this)
inline fun String.error(type: String = "App") = Log.e(type, this)
inline fun String.wtf(type: String = "App") = Log.wtf(type, this)
inline fun String.verbose(type: String = "App") = Log.v(type, this)