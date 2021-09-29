@file:Suppress("NOTHING_TO_INLINE")

package zz.utility.helpers

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.*

inline fun String.fileAsJsonArray(): JsonArray =
    JsonParser.parseReader(BufferedReader(FileReader(this))).asJsonArray

inline fun File.asJsonArray(): JsonArray =
    JsonParser.parseReader(BufferedReader(FileReader(this))).asJsonArray

inline fun String.asJsonArray(): JsonArray = JsonParser.parseString(this).asJsonArray

inline fun String.fileAsJsonObject(): JsonObject =
    JsonParser.parseReader(BufferedReader(FileReader(this))).asJsonObject

inline fun File.asJsonObject(): JsonObject =
    JsonParser.parseReader(BufferedReader(FileReader(this))).asJsonObject

inline fun String.asJsonObject(): JsonObject = JsonParser.parseString(this).asJsonObject

fun JsonObject.s(key: String, defaultValue: String = ""): String =
    { if (has(key)) get(key).asString else defaultValue }.or { defaultValue }

fun JsonObject.l(key: String, defaultValue: Long = 0): Long =
    { if (has(key)) get(key).asLong else defaultValue }.or { defaultValue }

fun JsonObject.i(key: String, defaultValue: Int = 0): Int =
    { if (has(key)) get(key).asInt else defaultValue }.or { defaultValue }

fun JsonObject.d(key: String, defaultValue: Double = 0.0): Double =
    { if (has(key)) get(key).asDouble else defaultValue }.or { defaultValue }

fun JsonObject.b(key: String, defaultValue: Boolean = false): Boolean =
    { if (has(key)) get(key).asBoolean else defaultValue }.or { defaultValue }

fun JsonObject.a(key: String, defaultValue: JsonArray = JsonArray()): JsonArray =
    { if (has(key)) get(key).asJsonArray else defaultValue }.or { defaultValue }

fun JsonObject.o(key: String, defaultValue: JsonObject = JsonObject()): JsonObject =
    { if (has(key)) get(key).asJsonObject else defaultValue }.or { defaultValue }

fun JsonArray.d(position: Int): Double = get(position).asDouble

fun JsonArray.i(position: Int): Int = get(position).asInt

fun JsonArray.b(position: Int): Boolean = get(position).asBoolean

fun JsonArray.s(position: Int): String = get(position).asString

fun JsonArray.o(position: Int): JsonObject = get(position).asJsonObject

fun JsonArray.isJsonObject(index: Int): Boolean = get(index).isJsonObject

inline fun <T> JsonArray.mapObject(f: JsonObject.() -> T): List<T> =
    this.map { it.asJsonObject.f() }

fun JsonObject.appendToFile(file: File) {
    var stream: FileOutputStream? = null
    try {
        stream = FileOutputStream(file, true)
        stream.write(this.toString().toByteArray())
        stream.write("\n".toByteArray())
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        { stream?.close() }.orPrint()
    }
}