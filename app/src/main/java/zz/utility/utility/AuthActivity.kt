package zz.utility.utility

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator
import dev.turingcomplete.kotlinonetimepassword.HmacAlgorithm
import dev.turingcomplete.kotlinonetimepassword.TimeBasedOneTimePasswordConfig
import dev.turingcomplete.kotlinonetimepassword.TimeBasedOneTimePasswordGenerator
import kotlinx.android.synthetic.main.activity_auth.*
import zz.utility.R
import java.util.concurrent.TimeUnit

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        refreshCodes()
        swipe_to_refresh.setOnRefreshListener { refreshCodes() }
    }

    private fun refreshCodes() {
        swipe_to_refresh.isRefreshing = true
        val googleAuthenticator = GoogleAuthenticator("")
        code.text = googleAuthenticator.generate()
        swipe_to_refresh.isRefreshing = false
    }
}
