package zz.utility.books

import android.app.Activity
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import zz.utility.helpers.intOr
import kotlinx.android.synthetic.main.activity_books.*
import zz.utility.R
import java.io.*
import java.util.*

class BooksActivity : Activity() {

    private var books: Array<Book>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books)

        findViewById<View>(R.id.book_add).setOnClickListener { doAdd() }

        findViewById<View>(R.id.book_search).setOnClickListener { doSearch() }
        doList()
    }

    inner class Book(
            var isbn: String,
            var title: String,
            var author: String,
            var series: String,
            var notes: String,
            var bookNumber: Int = 0,
            var setAmount: Int = 0,
            var tags: String,
            var edition: String,
            var printed: Int = 0
    ) {

        constructor(json: JsonObject) : this(
                json.get("isbn").asString,
                json.get("title").asString,
                json.get("author").asString,
                json.get("series").asString,
                json.get("notes").asString,
                json.get("set").asJsonObject.get("book").asInt,
                json.get("set").asJsonObject.get("of").asInt,
                json.get("tags").asString,
                json.get("edition").asString,
                json.get("printed").asInt
        )

        fun json(): JsonObject {
            val jsonObject = JsonObject()
            jsonObject.addProperty("isbn", isbn)
            jsonObject.addProperty("title", title)
            jsonObject.addProperty("author", author)
            jsonObject.addProperty("series", series)

            val jSet = JsonObject()
            jSet.addProperty("book", bookNumber)
            jSet.addProperty("of", setAmount)

            jsonObject.add("set", jSet)
            jsonObject.addProperty("notes", notes)
            jsonObject.addProperty("tags", tags)
            jsonObject.addProperty("edition", edition)
            jsonObject.addProperty("printed", printed)
            return jsonObject
        }

        internal fun titleString(): String {
            return if (bookNumber > 0 || setAmount > 0) "%d/%d %s".format(bookNumber, setAmount, title) else title
        }

        internal fun matches(check: String): Boolean {
            return isbn.contains(check) || title.toLowerCase().contains(check) || author.toLowerCase().contains(check) || series.toLowerCase().contains(check) || notes.toLowerCase().contains(check)
        }

        override fun toString(): String {
            return "ISBN: %s\nTitle: %s\nSeries: %s\nAuthor: %s\nTags: %s\nEdition: %s\nPrinted: %d\nNotes: %s".format(isbn, titleString(), series, author, tags, edition, printed, notes)
        }
    }

    private fun fetchAll(): ArrayList<Book> {
        val list = ArrayList<Book>()
        val path = Environment.getExternalStorageDirectory()
        val file = File(path, "books.json")
        try {
            val br = BufferedReader(FileReader(file))
            val array = JsonParser().parse(br).asJsonArray
            (0 until array.size()).mapTo(list) { Book(array.get(it).asJsonObject) }

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return list
    }

    private fun save() {
        if (books == null) return
        val path = Environment.getExternalStorageDirectory()
        val file = File(path, "books.json")
        var stream: FileOutputStream? = null
        try {
            stream = FileOutputStream(file, false)
            val array = JsonArray()
            for (book in books!!) array.add(book.json())
            stream.write(array.toString().toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (stream != null) stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    private fun doAdd() {

        val view = layoutInflater.inflate(R.layout.book_edit, null) as LinearLayout

        val isbn: EditText
        val title: EditText
        val series: EditText
        val author: EditText
        val tags: EditText
        val edition: EditText
        val printed: EditText
        val notes: EditText
        val bookNumber: EditText
        val bookAmount: EditText

        isbn = view.findViewById<View>(R.id.isbn) as EditText
        title = view.findViewById<View>(R.id.title) as EditText
        series = view.findViewById<View>(R.id.series) as EditText
        author = view.findViewById<View>(R.id.author) as EditText
        tags = view.findViewById<View>(R.id.tags) as EditText
        edition = view.findViewById<View>(R.id.edition) as EditText
        printed = view.findViewById<View>(R.id.printed) as EditText
        notes = view.findViewById<View>(R.id.notes) as EditText
        bookNumber = view.findViewById<View>(R.id.book) as EditText
        bookAmount = view.findViewById<View>(R.id.set) as EditText


        AlertDialog.Builder(this@BooksActivity)
                .setView(view)
                .setTitle("Edit Mode")
                .setNegativeButton("Back") { _, _ -> }
                .setPositiveButton("Save") { _, _ ->
                    val book = Book(
                            isbn.text.toString(),
                            title.text.toString(),
                            author.text.toString(),
                            series.text.toString(),
                            notes.text.toString(),
                            bookNumber.text.toString().intOr(0),
                            bookAmount.text.toString().intOr(0),
                            tags.text.toString(),
                            edition.text.toString(),
                            printed.text.toString().intOr(0)
                    )

                    val newBooks = Arrays.copyOf(books, (books?.size ?: 0) + 1)
                    newBooks[newBooks.size - 1] = book
                    books = newBooks
                    save()
                }
                .show()
    }

    private fun doSearch() {

        val input = EditText(this)
        AlertDialog.Builder(this)
                .setTitle("Search")
                .setView(input)
                .setPositiveButton("Find") { _, _ ->
                    val s = input.text.toString().toLowerCase()
                    books!!
                            .filter { it.matches(s) }
                            .forEach {
                                AlertDialog.Builder(this@BooksActivity)
                                        .setTitle("Found Book")
                                        .setMessage(it.toString())
                                        .show()
                            }
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                .show()
    }

    private fun doList() {
        books = fetchAll().toTypedArray()

        Arrays.sort(books, Comparator { o1, o2 ->
            var a: Int = o1.series.compareTo(o2.series)

            if (a != 0) return@Comparator a

            if (o1.bookNumber != o2.bookNumber) return@Comparator o1.bookNumber - o2.bookNumber

            a = o1.title.compareTo(o2.title)
            if (a != 0) a else o1.author.compareTo(o2.author)
        })

        list.removeAllViews()

        for (book in books!!) {
            val ll = layoutInflater.inflate(R.layout.book_line, list, false) as LinearLayout
            (ll.findViewById<View>(R.id.isbn) as TextView).text = book.isbn
            (ll.findViewById<View>(R.id.title) as TextView).text = book.titleString()
            if (book.series.isNotEmpty()) {
                val series = ll.findViewById<View>(R.id.series) as TextView
                series.visibility = View.VISIBLE
                series.text = book.series
            }
            (ll.findViewById<View>(R.id.author) as TextView).text = book.author
            ll.setOnClickListener {
                AlertDialog.Builder(this@BooksActivity)
                        .setTitle("Selected Book")
                        .setMessage(book.toString())
                        .show()
            }
            ll.setOnLongClickListener {
                val view = layoutInflater.inflate(R.layout.book_edit, null) as LinearLayout

                val isbn: EditText
                val title: EditText
                val series: EditText
                val author: EditText
                val tags: EditText
                val edition: EditText
                val printed: EditText
                val notes: EditText
                val bookNumber: EditText
                val bookAmount: EditText

                isbn = view.findViewById<View>(R.id.isbn) as EditText
                isbn.setText(book.isbn)
                title = view.findViewById<View>(R.id.title) as EditText
                title.setText(book.title)
                series = view.findViewById<View>(R.id.series) as EditText
                series.setText(book.series)
                author = view.findViewById<View>(R.id.author) as EditText
                author.setText(book.author)
                tags = view.findViewById<View>(R.id.tags) as EditText
                tags.setText(book.tags)
                edition = view.findViewById<View>(R.id.edition) as EditText
                edition.setText(book.edition)
                printed = view.findViewById<View>(R.id.printed) as EditText
                printed.setText("%d".format(book.printed))
                notes = view.findViewById<View>(R.id.notes) as EditText
                notes.setText(book.notes)
                bookNumber = view.findViewById<View>(R.id.book) as EditText
                bookNumber.setText("%d".format(book.bookNumber))
                bookAmount = view.findViewById<View>(R.id.set) as EditText
                bookAmount.setText("%d".format(book.setAmount))


                AlertDialog.Builder(this@BooksActivity)
                        .setView(view)
                        .setTitle("Edit Mode")
                        .setNegativeButton("Back") { _, _ -> }
                        .setPositiveButton("Save") { _, _ ->
                            book.isbn = isbn.text.toString()
                            book.title = title.text.toString()
                            book.author = author.text.toString()
                            book.series = series.text.toString()
                            book.tags = tags.text.toString()
                            book.edition = edition.text.toString()
                            book.notes = notes.text.toString()
                            book.bookNumber = bookNumber.text.toString().intOr(0)
                            book.setAmount = bookAmount.text.toString().intOr(0)
                            book.printed = printed.text.toString().intOr(0)

                            save()
                        }
                        .show()
                false
            }
            list.addView(ll)
        }
    }
}
