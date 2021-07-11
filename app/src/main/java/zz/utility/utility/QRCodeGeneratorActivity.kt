package zz.utility.utility

import android.graphics.Bitmap
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import zz.utility.R
import zz.utility.databinding.ActivityQrcodeGeneratorBinding
import zz.utility.externalFile
import zz.utility.helpers.ignore
import zz.utility.helpers.now
import zz.utility.helpers.orPrint
import java.io.FileOutputStream

class QRCodeGeneratorActivity : AppCompatActivity() {
    private lateinit var bitmap: Bitmap
    private lateinit var binding: ActivityQrcodeGeneratorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrcodeGeneratorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.subtitle = "Click to save image"

        binding.generate.setOnClickListener {
            {
                binding.qrCode.setImageBitmap(encodeAsBitmap(binding.text.text.toString()))
            }.orPrint()
        }

        binding.qrCode.setOnClickListener {
            val fileName = "qr_code_" + now() + ".png"
            var out: FileOutputStream? = null
            try {
                out = FileOutputStream(externalFile(fileName))
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
