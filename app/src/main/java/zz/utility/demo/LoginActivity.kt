package zz.utility.demo

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import zz.utility.databinding.ActivityLoginBinding
import zz.utility.helpers.*
import java.util.*

class LoginActivity : Activity() {

    // UI references.
    private lateinit var previousUsers: MutableSet<String>
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Set up the login form.
        binding.email.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_NEXT) {
                binding.password.requestFocus()
                return@OnEditorActionListener true
            }
            false
        })
        // Set up the login form.
        binding.password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                doLogin()
                return@OnEditorActionListener true
            }
            false
        })

        binding.emailSignInButton.setOnClickListener { doLogin() }

        binding.loginProgress.root.translationZ = 100F

        previousUsers = prefGetSet("previousUsers", TreeSet()) as MutableSet<String>
        binding.email.setAdapter(
            ArrayAdapter(
                this@LoginActivity,
                android.R.layout.simple_dropdown_item_1line,
                ArrayList(previousUsers)
            )
        )
    }

    private fun doLogin() {
        Toast.makeText(applicationContext, "You clicked enter", Toast.LENGTH_LONG).show()
        binding.loginProgress.root.show()
        Handler(Looper.myLooper()!!).postDelayed({
            binding.loginProgress.root.hide()
            Toast.makeText(applicationContext, "Done", Toast.LENGTH_LONG).show()
            preferences {
                previousUsers.add(binding.email.text.toString())
                putStringSet("previousUsers", previousUsers)
            }
            Handler(Looper.myLooper()!!).postDelayed({
                goto(TestScreenActivity::class.java)
            }, 250)
        }, 2000)
    }
}
