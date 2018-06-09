package zz.utility.poc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_poc_menu.*
import zz.utility.R
import zz.utility.bot.BotActivity
import zz.utility.helpers.goto

class PocMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poc_menu)
        goto_test_bottom_navigation.setOnClickListener { goto(TestBottomNavigationActivity::class.java) }
        goto_test_login.setOnClickListener { goto(LoginActivity::class.java) }
        goto_test_bot.setOnClickListener { goto(BotActivity::class.java) }
        goto_test_pos.setOnClickListener { goto(POSActivity::class.java) }
        goto_welcome.setOnClickListener { goto(WelcomeActivity::class.java) }
        goto_dashboard.setOnClickListener { goto(DashboardActivity::class.java) }
        goto_card.setOnClickListener { goto(CardDemoActivity::class.java) }
    }
}
