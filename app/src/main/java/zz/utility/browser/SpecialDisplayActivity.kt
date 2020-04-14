package zz.utility.browser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zz.utility.R
import zz.utility.helpers.a
import zz.utility.helpers.asJsonObject
import zz.utility.helpers.mapObject
import zz.utility.helpers.s
import java.io.File

data class Config(
        val conf: File,
        val dir: File,
        val items: ArrayList<Item>
)

data class Item(
        val file: File,
        val text: String
)

class SpecialDisplayActivity : AppCompatActivity() {

    lateinit var config: Config

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_special_display)


        val path = File(intent.extras?.getString(PATH) ?: "/1")
        if (!path.exists()) {
            finish()
            return
        }

        val configFile = File(path, "display.json")
        if (!configFile.exists()) {
            finish()
            return
        }

        config = Config(configFile, path, ArrayList())

        val json = configFile.asJsonObject()

        json.a("items").mapObject {
            config.items.add(Item(File(config.dir, s("file")), s("description")))
        }
    }
}
