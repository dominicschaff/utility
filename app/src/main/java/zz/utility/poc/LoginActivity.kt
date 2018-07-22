package zz.utility.poc

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import zz.utility.R
import zz.utility.helpers.prefGetSet
import java.util.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : Activity() {

    // UI references.
    private var previousUsers: Set<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        // Set up the login form.
        email.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_NEXT) {
                password.requestFocus()
                return@OnEditorActionListener true
            }
            false
        })
        // Set up the login form.
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                doLogin()
                return@OnEditorActionListener true
            }
            false
        })

        email_sign_in_button.setOnClickListener { doLogin() }

        previousUsers = prefGetSet("previousUsers", TreeSet())
        email.setAdapter(ArrayAdapter(this@LoginActivity, android.R.layout.simple_dropdown_item_1line, ArrayList(previousUsers)))
    }

    private fun doLogin() {
        Toast.makeText(applicationContext, "You clicked enter", Toast.LENGTH_LONG).show()
        login_scrolly.visibility = View.GONE
        login_progress.visibility = View.VISIBLE
        Thread({
            Thread.sleep(2000)
            runOnUiThread({
                login_progress.visibility = View.GONE
                login_scrolly.visibility = View.VISIBLE
                Toast.makeText(applicationContext, "Done", Toast.LENGTH_LONG).show()
            })
        }).start()
    }
}
