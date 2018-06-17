package zz.utility

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.os.Bundle
import android.os.Environment
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import kotlinx.android.synthetic.main.activity_qrcode_generator.*
import zz.utility.helpers.ignore
import zz.utility.helpers.now
import zz.utility.helpers.orPrint
import java.io.File
import java.io.FileOutputStream

class QRCodeGeneratorActivity : Activity() {
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode_generator)

        generate.setOnClickListener {
            {
                qr_code.setImageBitmap(encodeAsBitmap(text.text.toString()))
            }.orPrint()
        }

        qr_code.setOnClickListener {
            val fileName = "qr_code_" + now() + ".png"
            var out: FileOutputStream? = null
            try {
                out = FileOutputStream(File(Environment.getExternalStorageDirectory().toString(), fileName))
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) // bmp is your Bitmap instance
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                { out?.close() }.ignore()
            }
        }
    }

    @Throws(WriterException::class)
    private fun encodeAsBitmap(str: String): Bitmap? {
        val size = 1024
        val result: BitMatrix
        try {
            result = MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, size, size, null)

            val w = result.width
            val h = result.height
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                val offset = y * w
                for (x in 0 until w)
                    pixels[offset + x] = if (result.get(x, y)) BLACK else WHITE
            }
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, size, 0, 0, w, h)
        } catch (iae: Exception) {
            iae.printStackTrace()
            return null
        }

        return bitmap
    }
}
