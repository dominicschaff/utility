package zz.utility.poc

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import zz.utility.R
import android.print.pdf.PrintedPdfDocument
import android.graphics.pdf.PdfDocument
import android.print.PrintAttributes.Margins
import android.print.PrintAttributes
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class POSActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pos)

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    private fun doprint() {
        val printAttrs = PrintAttributes.Builder()
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(PrintAttributes.Resolution("zooey", Context.PRINT_SERVICE, 300, 300))
                .setMinMargins(Margins.NO_MARGINS).build()
        val document = PrintedPdfDocument(this, printAttrs)

        // crate a page description
        val pageInfo = PageInfo.Builder(300, 300, 1).create()
        // create a new page from the PageInfo
        val page = document.startPage(pageInfo)

//        page.canvas.

        // do final processing of the page
        document.finishPage(page)
        // accept a String/CharSequence. Meh.
        try {
            val pdfDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            pdfDirPath.mkdirs()
            val file = File(pdfDirPath, "pdfsend.pdf")
            val os = FileOutputStream(file)
            document.writeTo(os)
            document.close()
            os.close()
        } catch (e: IOException) {
            throw RuntimeException("Error generating file", e)
        }

    }
}
