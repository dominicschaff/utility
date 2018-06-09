@file:Suppress("NOTHING_TO_INLINE", "unused")

package zz.utility.helpers

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat

inline fun Activity.goto(c: Class<*>) = this.startActivity(Intent(this, c))

inline fun Activity.hasLocationPermissions(): Boolean = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

inline fun Activity.intentClearTop(cls: Class<*>) {
    this.startActivity(Intent(this, cls).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
}