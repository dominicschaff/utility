package zz.utility.browser

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.storage.StorageManager
import zz.utility.helpers.error
import java.io.File


fun File?.getRootOfInnerSdCardFolder(): File? {
    var localFile: File = this ?: return null
    if (localFile.absolutePath.contains("/Android/data/")) {
        val t = localFile.absolutePath.indexOf("/Android/data/")
        localFile = File(localFile.absolutePath.substring(0, t))
    }
    val totalSpace = localFile.totalSpace
    while (true) {
        val parentFile = localFile.parentFile
        "attempt: ${parentFile.absolutePath}".error()
        if (parentFile == null || parentFile.totalSpace != totalSpace || !parentFile.canWrite())
            return localFile
        localFile = parentFile
    }
}

fun File.moveToBin(): Boolean {
    val bin = File(Environment.getExternalStorageDirectory(), ".bin")
    if (!bin.exists()) bin.mkdir()
    return renameTo(File(bin, name))
}

fun Activity.takeCardUriPermission(sdCardRootPath: String) {
    val sdCard = File(sdCardRootPath)
    val storageManager = getSystemService(Context.STORAGE_SERVICE) as StorageManager?
    val storageVolume = storageManager!!.getStorageVolume(sdCard)
    val intent = storageVolume!!.createAccessIntent(null)
    try {
        startActivityForResult(intent, 4010)
    } catch (e: ActivityNotFoundException) {
    }
}

fun Activity.checkStoragePermissions(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    if (requestCode == 4010 && resultCode == Activity.RESULT_OK && data != null) {

        val uri = data.data!!

        grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val takeFlags = data.flags and (Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)

        contentResolver.takePersistableUriPermission(uri, takeFlags)
        return true
    }
    return false
}

fun Activity.getUri(): Uri? {
    val persistedUriPermissions = contentResolver.persistedUriPermissions
    if (persistedUriPermissions.size > 0) {
        val uriPermission = persistedUriPermissions[0]
        return uriPermission.uri
    }
    return null
}

fun ArrayList<File>.sortFiles() {
    sortWith(Comparator { o1, o2 ->
        o1.name.compareTo(o2.name, ignoreCase = true)
    })
}

fun Array<File>.sortFiles() {
    sortWith(Comparator { o1, o2 ->
        o1.name.compareTo(o2.name, ignoreCase = true)
    })
}