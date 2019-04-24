package zz.utility.browser

import java.io.File


fun File?.getRootOfInnerSdCardFolder(): File? {
    var localFile: File? = this ?: return null
    val totalSpace = localFile!!.totalSpace
    while (true) {
        val parentFile = localFile!!.parentFile
        if (parentFile == null || parentFile.totalSpace != totalSpace)
            return localFile
        localFile = parentFile
    }
}

fun File.moveToBin(): Boolean {
    val bin = File(this.getRootOfInnerSdCardFolder(), ".bin")
    if (!bin.exists()) bin.mkdir()
    return renameTo(File(bin, name))
}