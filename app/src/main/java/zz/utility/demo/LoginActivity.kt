package zz.utility.demo

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import zz.utility.R
import zz.utility.helpers.goto
import zz.utility.helpers.prefGetSet
import zz.utility.helpers.preferences
import zz.utility.helpers.show
import java.util.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : Activity() {

    // UI references.
    private lateinit var previousUsers: MutableSet<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
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

        login_progress.translationZ = 100F

        previousUsers = prefGetSet("previousUsers", TreeSet()) as MutableSet<String>
        email.setAdapter(ArrayAdapter(this@LoginActivity, android.R.layout.simple_dropdown_item_1line, ArrayList(previousUsers)))
    }

    private fun doLogin() {
        Toast.makeText(applicationContext, "You clicked enter", Toast.LENGTH_LONG).show()
        login_progress.show()
        Handler().postDelayed({
            login_progress.visibility = View.GONE
            Toast.makeText(applicationContext, "Done", Toast.LENGTH_LONG).show()
            preferences {
                previousUsers.add(email.text.toString())
                putStringSet("previousUsers", previousUsers)
            }
            Handler().postDelayed({
                goto(TestScreenActivity::class.java)
            }, 250)
        }, 2000)
    }
}
