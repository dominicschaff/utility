package zz.utility.environment

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder

class SoundMeter {

    internal var ar: AudioRecord? = null
    private var minSize: Int = 0

    val amplitude: Int
        get() {
            val buffer = ShortArray(minSize)
            ar?.read(buffer, 0, minSize)

            return buffer.map { Math.abs(it.toInt()) }.max() ?: -1
        }

    fun start() {
        minSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        ar = AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minSize)
        ar!!.startRecording()
    }

    fun stop() {
        ar?.stop()
        ar = null
    }

}