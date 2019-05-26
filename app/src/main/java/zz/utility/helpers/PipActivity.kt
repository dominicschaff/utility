package zz.utility.helpers

import android.app.PictureInPictureParams

abstract class PipActivity : FullscreenActivity() {

    override fun onUserLeaveHint() {
        enterPictureInPictureMode(PictureInPictureParams.Builder().build())
    }
}