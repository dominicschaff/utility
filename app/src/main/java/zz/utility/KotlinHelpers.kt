@file:Suppress("unused", "NOTHING_TO_INLINE")

package zz.utility

import android.os.Environment
import zz.utility.helpers.fileAsJsonObject
import java.io.File

fun File.isImage(): Boolean = extension.toLowerCase() in arrayOf("jpg", "jpeg", "png", "gif")
fun File.isVideo(): Boolean = extension.toLowerCase() in arrayOf("mp4", "avi", "m4v", "webm")
fun File.isText(): Boolean = extension.toLowerCase() in arrayOf("txt", "md", "py", "json", "java", "kt")


fun File.imageIcon(): Int =
        if (isFile) when (extension.toLowerCase()) {
            in arrayOf("zip", "gz", "tar", "bundle") -> R.drawable.ic_file_archive
            in arrayOf("mp3", "wav", "aac", "m4a") -> R.drawable.ic_file_audio
            in arrayOf("c", "json", "js", "java", "py") -> R.drawable.ic_file_code
            in arrayOf("jpg", "jpeg", "png", "gif") -> R.drawable.ic_file_image
            in arrayOf("mp4", "avi", "m4v") -> R.drawable.ic_file_movie
            "pdf" -> R.drawable.ic_file_pdf
            in arrayOf("md", "txt", "log") -> R.drawable.ic_file_text
            else -> R.drawable.ic_file
        } else R.drawable.ic_file_folder


val HOME = "${Environment.getExternalStorageDirectory().absolutePath}/utility"
val MAIN = "$HOME/utility.json"
val MAIN_CONFIG = MAIN.fileAsJsonObject()