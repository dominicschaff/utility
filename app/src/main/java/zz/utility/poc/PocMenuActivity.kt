package zz.utility.poc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_poc_menu.*
import zz.utility.R
import zz.utility.bot.BotActivity
import zz.utility.helpers.goto
import zz.utility.poc.heavy.DataHeavyActivity

class PocMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poc_menu)
        goto_test_bottom_navigation.setOnClickListener { goto(TestBottomNavigationActivity::class.java) }
        goto_test_login.setOnClickListener { goto(LoginActivity::class.java) }
        goto_test_bot.setOnClickListener { goto(BotActivity::class.java) }
        goto_demo.setOnClickListener { goto(TestScreenActivity::class.java) }
        goto_car_dash.setOnClickListener { goto(CarDashActivity::class.java) }
        goto_data_heavy.setOnClickListener { goto(DataHeavyActivity::class.java) }
        goto_stars.setOnClickListener { goto(StarsActivity::class.java) }
        goto_dashboard.setOnClickListener { goto(DashboardActivity::class.java) }
    }
}
