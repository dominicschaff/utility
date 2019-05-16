package zz.utility.poc

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import zz.utility.browser.checkStoragePermissions
import zz.utility.browser.getRootOfInnerSdCardFolder
import zz.utility.browser.getUri
import zz.utility.browser.takeCardUriPermission
import zz.utility.helpers.error

class TestAccessorsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val x = ContextCompat.getExternalFilesDirs(this, null).map { it.getRootOfInnerSdCardFolder() }
        val uri = getUri()
        if (uri == null) {
            takeCardUriPermission(x[1]!!.absolutePath)
        } else {
            uri.toString().error()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!checkStoragePermissions(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
