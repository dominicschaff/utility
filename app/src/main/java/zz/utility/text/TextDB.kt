package zz.utility.text

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

const val TYPE_TEXT = 0
const val TYPE_URL = 1

data class Category(
        val id: Int,
        val name: String,
        val type: Int
)

data class Content(
        val id: Int,
        val category: Category,
        val content: String
)

const val DATABASE_VERSION = 1
const val DATABASE_NAME = "text.db"

class TextDB(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE category (id INTEGER PRIMARY KEY, name TEXT, type INTEGER)")
        db.execSQL("CREATE TABLE content (id INTEGER PRIMARY KEY, category INTEGER, content TEXT)")
        db.execSQL("INSERT INTO category (id, name, type) VALUES (0, \"Default\", 0)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS category")
        db.execSQL("DROP TABLE IF EXISTS content")
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun add(category: Category) {

        val db = writableDatabase

        val values = ContentValues().apply {
            put("name", category.name)
            put("type", category.type)
        }

        db.insert("category", null, values)
    }

    fun add(content: Content) {
        val db = writableDatabase

        val values = ContentValues().apply {
            put("content", content.content)
            put("category", content.category.id)
        }

        db.insert("content", null, values)
    }

    fun categories(): Array<Category> {
        val db = readableDatabase

        val cursor = db.query(
                "category",
                arrayOf("id", "name", "type"),
                null,
                null,
                null,
                null,
                "id ASC"
        )
        val categories = mutableListOf<Category>()
        with(cursor) {
            while (moveToNext()) {
                categories.add(
                        Category(
                                getInt(0),
                                getString(1),
                                getInt(2)
                        )
                )
            }
        }
        return categories.toTypedArray()
    }

    fun getCategory(category_id: Int): Category? {
        val db = readableDatabase

        val cursor = db.query(
                "category",
                arrayOf("id", "name", "type"),
                "id = $category_id",
                null,
                null,
                null,
                "id ASC"
        )
        var category: Category? = null
        with(cursor) {
            while (moveToNext()) {
                category = Category(
                        getInt(0),
                        getString(1),
                        getInt(2)
                )
            }
        }
        return category
    }

    fun getContentCategory(category: Int): Array<Content> {
        val db = readableDatabase

        val cursor = db.query(
                "content",
                arrayOf("id", "category", "content"),
                "category = $category",
                null,
                null,
                null,
                "id ASC"
        )
        val contents = mutableListOf<Content>()
        with(cursor) {
            while (moveToNext()) {
                contents.add(
                        Content(
                                getInt(0),
                                getCategory(getInt(1))!!,
                                getString(2)
                        )
                )
            }
        }
        return contents.toTypedArray()
    }

    fun modifyContentType(content_id: Int, type_id: Int) {

    }
}