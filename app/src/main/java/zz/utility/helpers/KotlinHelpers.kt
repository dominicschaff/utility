@file:Suppress("NOTHING_TO_INLINE")

package zz.utility.helpers

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import java.io.File

inline fun <T> tryCatch(f: () -> T, d: T): T = try {
    f()
} catch (e: Exception) {
    d
}

fun Context.createChooser(title: String, options: Array<String>, clickListener: DialogInterface.OnClickListener) {
    AlertDialog.Builder(this)
            .setTitle(title)
            .setItems(options, clickListener)
            .show()
}


fun Activity.requestPermissions(permissions: Array<String>): Boolean {
    if (permissions.isEmpty()) return true
    permissions.forEach {
        if (ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1)
            return false
        }
    }
    return true
}

inline fun tryIgnore(f: () -> Unit) {
    try {
        f()
    } catch (ignored: Exception) {
    }
}

inline fun tryPrint(f: () -> Unit) {
    try {
        f()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

inline fun <T> List<T>.random(): T {
    return this[(Math.random() * size).toInt()]
}

inline fun Context.longToast(s: String) = Toast.makeText(this, s, Toast.LENGTH_LONG).show()
inline fun Context.shortToast(s: String) = Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
inline fun View.see() {
    visibility = View.VISIBLE
}

inline fun View.unsee() {
    visibility = View.GONE
}


inline fun Activity.openFile(f: File) {
    try {
        val apkUri = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", f)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = apkUri
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(intent)
    } catch (e: Exception) {
        longToast("No application to open this file")
    }
}


inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}

inline fun consumeNo(f: () -> Unit): Boolean {
    f()
    return false
}

fun <T> doAsync(f: () -> T?, g: (T?) -> Unit) {
    object : AsyncTask<Void, Void, T>() {
        override fun doInBackground(vararg params: Void?): T? = f()

        override fun onPostExecute(result: T?) = g(result)
    }.execute()
}