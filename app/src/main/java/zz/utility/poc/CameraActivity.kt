package zz.utility.poc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.*
import kotlinx.android.synthetic.main.activity_camera.*
import zz.utility.R
import zz.utility.helpers.fileDate
import zz.utility.helpers.toast
import java.io.File
import java.util.*

class CameraActivity : AppCompatActivity() {

    private lateinit var fotoapparat: Fotoapparat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        fotoapparat = Fotoapparat(
                context = this,
                view = camera_view,                   // view which will draw the camera preview
                scaleType = ScaleType.CenterCrop,    // (optional) we want the preview to fill the view
                lensPosition = back(),               // (optional) we want back camera
                cameraConfiguration = CameraConfiguration(
                        pictureResolution = highestResolution(),
                        previewResolution = lowestResolution(),
                        previewFpsRange = lowestFps(),
                        focusMode = firstAvailable(continuousFocusPicture(), autoFocus(), fixed()),
                        jpegQuality = manualJpegQuality(90)
                )
        )
        take_a_picture.setOnClickListener {
            val lastPictureName = "IMG_${Date().fileDate()}.jpg"
            fotoapparat.takePicture().saveToFile(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), lastPictureName))
            toast("$lastPictureName - saved")
        }
    }

    override fun onStart() {
        super.onStart()
        fotoapparat.start()
    }

    override fun onStop() {
        super.onStop()
        fotoapparat.stop()
    }

}
