package zz.utility.bot

import android.accounts.AccountManager
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_bot.*
import zz.utility.R
import zz.utility.helpers.fullDate
import java.util.*


class BotActivity : AppCompatActivity() {
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bot)
        add("Initializing system...")
        add("Local time: ${Date().fullDate()}")
        tts = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale.UK)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported")
                }
                add("Speech enabled")
                speak("Hello")

            } else {
                Log.e("TTS", "Initilization Failed!")
                add("Voice cannot be used")
            }
            doContinue()
        })
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

    public override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }

    private fun doContinue() {
        add("Searching calendar")
        add("Searching local events")
        add("Searching Accounts...")
        getAccounts()
        add("Searching Contacts...")
        getContacts()
    }

    private fun getAccounts() {

        val manager = AccountManager.get(this)
        val accounts = manager.accounts
//        val accounts = manager.getAccountsByType("com.google")

        accounts.forEach { add("Found account: ${it.type}\n\t\t${it.name}") }
    }

    private fun getContacts() {
        val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME)
        cursor ?: return

        val count = cursor.count
        add("Found $count contacts")
        cursor.moveToFirst()

        var previous = ""
        do {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
            val hasNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))
            if (hasNumber.endsWith("0")) continue
//            val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.))

            if (previous != name) {
                add("Contact: $name")
                previous = name
            }
            val contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            val phones = contentResolver.query(Phone.CONTENT_URI, null,
                    Phone.CONTACT_ID + " = " + contactId, null, null)
            phones ?: continue
            while (phones.moveToNext()) {
                val number = phones.getString(phones.getColumnIndex(Phone.NUMBER))
                val type = phones.getInt(phones.getColumnIndex(Phone.TYPE))
                add("\t\tNumber: $number -> " +
                        when (type) {
                            Phone.TYPE_HOME -> "home"
                            Phone.TYPE_MOBILE -> "mobile"
                            Phone.TYPE_WORK -> "work"
                            else -> "Unknown:$type"
                        })
            }
            phones.close()

        } while (cursor.moveToNext())

        cursor.close()
    }

    private fun add(s: String) = mainText.append("$s\n")
    private fun speak(text: String) = tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
}
